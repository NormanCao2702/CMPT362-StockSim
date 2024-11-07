package com.example.cmpt362_stocksim.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_stocksim.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var lineChart: LineChart

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

        lineChart = binding.lineChart // Make sure you have this ID in your layout
        setupChart()
        setData()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}