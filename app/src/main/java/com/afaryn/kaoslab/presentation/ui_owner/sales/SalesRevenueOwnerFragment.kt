package com.afaryn.kaoslab.presentation.ui_owner.sales

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentSalesRevenueOwnerBinding
import com.afaryn.kaoslab.domain.model.TransactionType
import com.afaryn.kaoslab.presentation.ui_owner.sales.adapter.TransactionHistoryAdapter
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class SalesRevenueOwnerFragment : Fragment() {

    private var _binding: FragmentSalesRevenueOwnerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SalesRevenueViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavOwner()
        _binding = FragmentSalesRevenueOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionHistoryAdapter()
        binding.transactionRecyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.selectTypeButton.setOnClickListener {
            showTransactionTypeDialog()
        }

        binding.selectDateButton.setOnClickListener {
            showDateRangeDialog()
        }

        binding.withdrawButton.setOnClickListener {
            // Implement withdrawal functionality
            showWithdrawalDialog()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalBalance.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state if needed
                    }
                    is Response.Success -> {
                        updateBalanceDisplay(response.data)
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
            viewModel.transactionHistory.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state
                    }
                    is Response.Success -> {
                        transactionAdapter.submitList(response.data)
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
            viewModel.selectedTransactionType.collect { type ->
                updateTypeButtonText(type)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDateRange.collect { dateRange ->
                updateDateButtonText(dateRange)
            }
        }
    }

    private fun updateBalanceDisplay(balance: Double) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.currency = Currency.getInstance("IDR")
        binding.totalBalanceAmount.text = formatter.format(balance).replace("IDR", "Rp")
    }

    private fun updateTypeButtonText(type: TransactionType?) {
        binding.selectTypeButton.text = when (type) {
            TransactionType.PAYMENT -> "Payment"
            TransactionType.WITHDRAWAL -> "Withdrawal"
            null -> "Select type"
        }
    }

    private fun updateDateButtonText(dateRange: Pair<Timestamp?, Timestamp?>) {
        if (dateRange.first != null && dateRange.second != null) {
            val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
            val startDate = sdf.format(dateRange.first!!.toDate())
            val endDate = sdf.format(dateRange.second!!.toDate())
            binding.selectDateButton.text = "$startDate - $endDate"
        } else {
            binding.selectDateButton.text = "Select Date"
        }
    }

    private fun showTransactionTypeDialog() {
        val options = arrayOf("All", "Payment", "Withdrawal")
        val currentSelection = when (viewModel.selectedTransactionType.value) {
            null -> 0
            TransactionType.PAYMENT -> 1
            TransactionType.WITHDRAWAL -> 2
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Transaction Type")
            .setSingleChoiceItems(options, currentSelection) { dialog, which ->
                val selectedType = when (which) {
                    0 -> null
                    1 -> TransactionType.PAYMENT
                    2 -> TransactionType.WITHDRAWAL
                    else -> null
                }
                viewModel.setTransactionTypeFilter(selectedType)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDateRangeDialog() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val startDate = Calendar.getInstance()
                startDate.set(year, month, dayOfMonth, 0, 0, 0)

                // Show end date picker
                DatePickerDialog(
                    requireContext(),
                    { _, endYear, endMonth, endDayOfMonth ->
                        val endDate = Calendar.getInstance()
                        endDate.set(endYear, endMonth, endDayOfMonth, 23, 59, 59)

                        viewModel.setDateRangeFilter(
                            Timestamp(startDate.time),
                            Timestamp(endDate.time)
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    setTitle("Select End Date")
                }.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setTitle("Select Start Date")
        }.show()
    }

    private fun showWithdrawalDialog() {
        // Basic withdrawal dialog - you can enhance this with amount input
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Withdrawal")
            .setMessage("Withdrawal functionality will be implemented here")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}