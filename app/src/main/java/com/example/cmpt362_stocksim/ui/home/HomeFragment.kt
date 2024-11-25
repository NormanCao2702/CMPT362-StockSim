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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.AchievementActivity
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.BackendViewModel
import com.example.cmpt362_stocksim.BackendViewModelFactory
import com.example.cmpt362_stocksim.StockInventory
import com.example.cmpt362_stocksim.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var tvCashBalance: TextView

    private var _binding: FragmentHomeBinding? = null
    private lateinit var lineChart: LineChart
    private var isAchievementUnlocked = false

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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        checkAchievement()


        tvCashBalance = binding.tvCashBalance

        // NEED TO GET USER ID DYNAMICALLY AND PUT IT IN HERE         VVVVVVVVVV
        // DONT FORGETTTTT

        lifecycleScope.launch {
            try {
                val response = backendViewModel.getCash("15")
                if (response != null) {
                   tvCashBalance.text = "$${response.cash}"
                }
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }





        lineChart = binding.lineChart // Make sure you have this ID in your layout
        setupChart()
        setData()
        setupButton()

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return binding.root
    }

    private fun setupChart() {
        lineChart.apply {
            // Disable description
            description.isEnabled = false

            // Enable touch gestures
            setTouchEnabled(true)
            setPinchZoom(true)

            // Customize grid background
            setDrawGridBackground(false)

            // Customize X axis
            xAxis.apply {
                setDrawAxisLine(true)
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // Convert float to days
                        return "Day ${value.toInt() + 1}"
                    }
                }
            }

            // Customize left Y axis
            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$${value.toInt()}"
                    }
                }
                // Set axis minimum and maximum
                axisMinimum = 9000f
                axisMaximum = 11000f
            }

            // Disable right Y axis
            axisRight.isEnabled = false

            // Customize legend
            legend.isEnabled = false
        }
    }

    private fun setData() {
        // Create flat line data points for 7 days
        val entries = ArrayList<Entry>()
        for (i in 0..6) {  // 7 days (0-6)
            entries.add(Entry(i.toFloat(), 10000f))
        }

        // Create dataset
        val dataSet = LineDataSet(entries, "Portfolio Value").apply {
            color = Color.GREEN
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR

            // Fill color below the line
            setDrawFilled(true)
            fillColor = Color.GREEN
            fillAlpha = 50
        }

        // Set data to chart
        lineChart.data = LineData(dataSet)

        // Animate the chart
        lineChart.animateX(1000)

        // Refresh chart
        lineChart.invalidate()
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