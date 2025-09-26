package com.afaryn.kaoslab.presentation.ui_owner.report

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.databinding.FragmentBusinessReportBinding
import com.afaryn.kaoslab.domain.model.BusinessInsights
import com.afaryn.kaoslab.domain.model.ChartData
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.showBottomNavOwner
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class BusinessReportFragment : Fragment() {

    private var _binding: FragmentBusinessReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BusinessReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusinessReportBinding.inflate(inflater, container, false)
        showBottomNavOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.thisWeekDropdown.setOnClickListener {
            // TODO: Implement period selection dropdown
            when (viewModel.selectedPeriod.value) {
                "week" -> {
                    binding.thisWeekDropdown.text = "This Month"
                    viewModel.loadSellingProductData("month")
                }
                "month" -> {
                    binding.thisWeekDropdown.text = "This Week"
                    viewModel.loadSellingProductData("week")
                }
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.businessInsights.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state
                    }
                    is Response.Success -> {
                        updateBusinessInsights(response.data)
                    }
                    is Response.Error -> {
                        // Handle error
                    }
                    is Response.Idle -> {
                        // Initial state
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellingProductData.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state for chart
                    }
                    is Response.Success -> {
                        updateChart(response.data)
                    }
                    is Response.Error -> {
                        // Handle error
                    }
                    is Response.Idle -> {
                        // Initial state
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedPeriod.collect { period ->
                binding.thisWeekDropdown.text = when (period) {
                    "week" -> "This Week"
                    "month" -> "This Month"
                    else -> "This Week"
                }
            }
        }
    }

    private fun updateBusinessInsights(insights: BusinessInsights) {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)

        // Update card values using the new IDs
        binding.tvOrdersValue.text = numberFormat.format(insights.totalOrders)
        binding.tvSalesValue.text = "Rp\n${numberFormat.format(insights.totalSales)}"
        binding.tvVisitorsValue.text = numberFormat.format(insights.totalVisitors)
        binding.tvBuyersValue.text = numberFormat.format(insights.totalBuyers)
        binding.tvTotalStockValue.text = numberFormat.format(insights.totalStock)

        // Update selling product amount
        binding.sellingProductAmount.text = "Rp ${numberFormat.format(insights.totalSales)}"
    }

    private fun updateChart(chartData: List<ChartData>) {
        val entries = chartData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.value)
        }

        val dataSet = BarDataSet(entries, "Sales").apply {
            colors = listOf(
                Color.parseColor("#FF6B9D"),
                Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"),
                Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7"),
                Color.parseColor("#DDA0DD"),
                Color.parseColor("#98D8C8")
            )
            valueTextSize = 12f
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSet)

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = false

            // Configure X-axis
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(chartData.map { it.label })
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            // Configure Y-axis
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                axisMinimum = 0f
            }
            axisRight.isEnabled = false

            // Refresh chart
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}