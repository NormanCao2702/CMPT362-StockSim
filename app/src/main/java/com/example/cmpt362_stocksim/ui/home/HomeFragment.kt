package com.example.cmpt362_stocksim.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.example.cmpt362_stocksim.AchievementActivity
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.BackendViewModel
import com.example.cmpt362_stocksim.BackendViewModelFactory
import com.example.cmpt362_stocksim.NetWorthHistoryManager
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.StockInventory
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

    private lateinit var tvCashBalance: TextView
    private val userDataManager by lazy { UserDataManager(requireContext()) }

    private var _binding: FragmentHomeBinding? = null
    private lateinit var lineChart: LineChart
    private var isAchievementUnlocked = false

    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)

    private lateinit var netWorthHistoryManager: NetWorthHistoryManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        netWorthHistoryManager = NetWorthHistoryManager(requireContext())

        checkAchievement()

        tvCashBalance = binding.tvCashBalance

        lifecycleScope.launch {
            try {
                val userId = userDataManager.getUserId() ?: "15"
                val response = backendViewModel.getCash(userId)
                if (response != null) {
                   tvCashBalance.text = "$${response.cash}"
                }
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }

        lineChart = binding.lineChart // Make sure you have this ID in your layout
        setupChart()
        updateChartWithLatestNetWorth()
        setupButton()

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return binding.root
    }

    private fun updateChartWithLatestNetWorth() {
        lifecycleScope.launch {
            try {
                val currentNetWorth = UserDataManager(requireContext()).getNetWorth()
                Log.d("ChartDebug", "Current net worth: $currentNetWorth")

                netWorthHistoryManager.saveNetWorthValue(currentNetWorth)

                val history = netWorthHistoryManager.getNetWorthHistory()
                Log.d("ChartDebug", "History size: ${history.size}")

                if (history.isNotEmpty()) {
                    updateChartData(history)
                }

            } catch (e: Exception) {
                Log.e("ChartDebug", "Error updating chart: ${e.message}", e)
                Toast.makeText(context, "Failed to update chart: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateChartData(history: List<NetWorthHistoryManager.NetWorthEntry>) {
        if (history.isEmpty()) {
            Log.d("ChartDebug", "No data to display")
            return
        }

        Log.d("ChartDebug", "Creating entries...")

        // Create entries with normalized x values (0, 1, 2, etc.)
        val entries = history.mapIndexed { index, entry ->
            Log.d("ChartDebug", "Entry: time=${entry.timestamp}, value=${entry.value}")
            Entry(index.toFloat(), entry.value.toFloat())
        }

        val minValue = history.minOfOrNull { it.value }?.toFloat() ?: 0f
        val maxValue = history.maxOfOrNull { it.value }?.toFloat() ?: 0f
        val padding = (maxValue - minValue) * 0.1f

        try {
            val dataSet = LineDataSet(entries, "Net Worth").apply {
                color = ContextCompat.getColor(requireContext(), R.color.purple_500)
                lineWidth = 2f
                setDrawCircles(true) // Enable circles to see data points
                circleRadius = 4f
                setCircleColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
                setDrawValues(false)
                mode = LineDataSet.Mode.LINEAR
                setDrawFilled(true)
                fillColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
                fillAlpha = 50
            }

            lineChart.apply {
                xAxis.labelCount = entries.size // Show all points

                axisLeft.apply {
                    // Set min and max with padding
                    axisMinimum = minValue - padding
                    axisMaximum = maxValue + padding
                }

                data = LineData(dataSet)
                animateY(1000)
                invalidate()
            }
            Log.d("ChartDebug", "Chart updated with ${entries.size} entries")
        } catch (e: Exception) {
            Log.e("ChartDebug", "Error setting chart data: ${e.message}", e)
        }
    }

    private fun setupChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)

            xAxis.apply {
                setDrawAxisLine(true)
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        // value is already normalized to position
                        val history = netWorthHistoryManager.getNetWorthHistory()
                        val index = value.toInt()
                        if (index >= 0 && index < history.size) {
                            return dateFormat.format(Date(history[index].timestamp))
                        }
                        return ""
                    }
                }
            }

            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$${value.toInt()}"
                    }
                }
                // Remove fixed min/max
                setDrawZeroLine(true)
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun checkAchievement(){
        var count = 0.0

        // Do a lunach and check if the user has it...
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isAchievementUnlocked = sharedPreferences.getBoolean("isAchievementUnlocked", false)


        lifecycleScope.launch {
            try {
                val response = backendViewModel.getInv("15")
                if (response != null) {
                    for (stock in response.stocks) {

                        count += stock.amount.toFloat()
                    }
                }
                if (count >= 5.0 && !isAchievementUnlocked) {
                    isAchievementUnlocked = true
                    sharedPreferences.edit().putBoolean("isAchievementUnlocked", true).apply()
                    Toast.makeText(
                        requireContext(),
                        "Beginner Investor Achievement Unlocked",
                        Toast.LENGTH_SHORT
                    ).show()

                    lifecycleScope.launch {
                        try {
                            val response = backendViewModel.setUsersAchievement("1")
                        } catch (e: IllegalArgumentException) {
                            Log.d("MJR", e.message!!)
                        }
                    }
                }

            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }
    }




    private fun setupButton() {
        binding.notificationButton.setOnClickListener{
            // notification button click event
        }

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