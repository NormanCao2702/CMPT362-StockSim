package com.example.cmpt362_stocksim.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.ui.achievement.AchievementActivity
import com.example.cmpt362_stocksim.ui.achievement.AchievementChecker
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.userDataManager.NetWorthHistoryManager
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.ui.inventory.StockInventory
import com.example.cmpt362_stocksim.databinding.FragmentHomeBinding
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    // Initialize textview
    private lateinit var tvCashBalance: TextView
    private lateinit var tvNetWorth: TextView

    // Initialize achievement checker and data manager
    private val userDataManager by lazy { UserDataManager(requireContext()) }
    private val achievementChecker by lazy { AchievementChecker(requireContext(), viewLifecycleOwner) }
    private lateinit var netWorthHistoryManager: NetWorthHistoryManager

    // Initialize flag
    private var newUserChecker = true

    private var _binding: FragmentHomeBinding? = null

    // Initialize line chart
    private lateinit var lineChart: LineChart

    // Setup backend repository
    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Connect pref and manager
        netWorthHistoryManager = NetWorthHistoryManager(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Check if its a new login and setup sharepref for setup
        if (newUserChecker == true){
            sharedPreferences.edit().putBoolean("hasSetup", false).apply()
            newUserChecker = false
        }

        // Set textviews
        tvCashBalance = binding.tvCashBalance
        tvNetWorth = binding.tvNetWorth

        // Display networth
        lifecycleScope.launch {
            try {
                // First refresh user info to get latest net worth
                val success = userDataManager.refreshUserInfo()
                if (success) {
                    // Now get and display net worth
                    val netWorth = userDataManager.getNetWorth()
                    tvNetWorth.text = "$${String.format("%.2f", netWorth)}"
                }
            } catch (e: IllegalArgumentException) {
                Log.d("Networth", "Error: ${e.message}")
            }
        }

        // Display user cash
        lifecycleScope.launch {
            try {
                val userId = userDataManager.getUserId()
                val response = userId?.let { backendViewModel.getCash(it) }
                if (response != null) {
                    tvCashBalance.text = "$${String.format("%.2f", response.cash)}"
                }
            } catch (e: IllegalArgumentException) {
                Log.d("Cash", "Error: ${e.message}")
            }
        }

        // Create graph line chart
        lineChart = binding.lineChart // Make sure you have this ID in your layout
        setupChart()
        fetchNetWorthAndUpdateChart()
        setupButton()

        return binding.root
    }

    // This function updates the chart data based on user history
    private fun updateChartData(history: List<NetWorthHistoryManager.NetWorthEntry>) {
        val entries = if (history.all { it.value == history[0].value }) {
            // If all values are the same (like 10000.0), create two points with slight difference
            listOf(
                Entry(history[0].timestamp.toFloat(), history[0].value.toFloat()),
                Entry(history[0].timestamp.toFloat() + 3600000, history[0].value.toFloat() + 0.01f)
            )
        } else {
            history.map { Entry(it.timestamp.toFloat(), it.value.toFloat()) }
        }

        // Set min/max values
        val value = history[0].value.toFloat()
        val padding = value * 0.1f // 10% padding

        val dataSet = LineDataSet(entries, "Net Worth").apply {
            color = ContextCompat.getColor(requireContext(), R.color.purple_500)
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR

            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
            fillAlpha = 50
        }
        Handler(Looper.getMainLooper()).post {
            lineChart.apply {
                axisLeft.apply {
                    axisMinimum = value - padding
                    axisMaximum = value + padding
                }

                data = LineData(dataSet)
                animateX(1000)
                invalidate()
            }
        }
    }

    // This function gets users networth and updates the chart
    private fun fetchNetWorthAndUpdateChart() {
        lifecycleScope.launch {
            try {
                val userId = userDataManager.getUserId()

                if (userId != null) {
                    // Always refresh user info when opening/resuming app
                    val success = userDataManager.refreshUserInfo()
                    if (success) {
                        val currentNetWorth = userDataManager.getNetWorth()
                        Log.d("ChartDebug", "Current net worth: $currentNetWorth")

                        // Save the current value
                        netWorthHistoryManager.saveNetWorthValue(userId, currentNetWorth)
                    }

                    // Get and display history
                    val history = netWorthHistoryManager.getNetWorthHistory(userId)
                    Log.d("ChartDebug", "History size: ${history.size}")

                    // Add this debug log
                    history.forEach { entry ->
                        Log.d("ChartDebug", "History entry: timestamp=${entry.timestamp}, value=${entry.value}")
                    }

                    if (history.isEmpty()) {
                        Log.d("ChartDebug", "No history found, creating initial entry")
                        val currentNetWorth = userDataManager.getNetWorth()
                        val initialEntry = NetWorthHistoryManager.NetWorthEntry(
                            timestamp = System.currentTimeMillis(),
                            value = currentNetWorth
                        )
                        updateChartData(listOf(initialEntry))
                    } else {
                        updateChartData(history)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChartDebug", "Error in fetch: ${e.message}", e)
                Toast.makeText(context, "Failed to fetch net worth", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // This function sets up the homepage chart
    private fun setupChart() {
        lineChart.apply {
            // Disable description
            description.isEnabled = false

            // Enable touch gestures
            setTouchEnabled(true)
            setPinchZoom(true)
            isDragEnabled = false  // Disable drag since we might have single point

            // Customize grid background
            setDrawGridBackground(false)

            // Customize X axis (time)
            xAxis.apply {
                setDrawAxisLine(true)
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f    // Prevent duplicate labels
                labelCount = 4      // Show only 4-5 labels
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())  // Simplified to just hours:minutes
                    override fun getFormattedValue(value: Float): String {
                        return dateFormat.format(Date(value.toLong()))
                    }
                }
            }

            // Customize Y axis (net worth)
            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$${String.format("%.2f", value)}"
                    }
                }
                setDrawZeroLine(true)
            }

            // Disable right Y axis
            axisRight.isEnabled = false

            // Disable legend
            legend.isEnabled = false

            // Set minimum height for better visibility
            minOffset = 20f
        }
    }

    // On app resume resetup chart and achievement check for new achievements
    override fun onResume() {
        super.onResume()
        // Reset and reinitialize the chart
        setupChart()
        fetchNetWorthAndUpdateChart()

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val alreadySetUp = sharedPreferences.getBoolean("hasSetup", false)

        if (!alreadySetUp) {
            println("Setting up achievements...")
            achievementChecker.setupChecker()
            sharedPreferences.edit().putBoolean("hasSetup", true).apply()
        } else {
            achievementChecker.checkForAchievements()
        }
    }

    // Setup buttons in homepage
    private fun setupButton() {
        binding.btnStockInventory.setOnClickListener {
            val intent = Intent(requireActivity(), StockInventory::class.java)
            startActivity(intent)
        }

        binding.btnAchievements.setOnClickListener{
            val intent = Intent(requireActivity(), AchievementActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}