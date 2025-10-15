package com.afaryn.kaoslab.presentation.ui_customer.account.orders.fragments

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.OrderAdapter
import com.afaryn.kaoslab.data.adapter.OrderAdapter.OrderViewHolder
import com.afaryn.kaoslab.databinding.FragmentMyOrderBinding
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.OrderStatus
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.OrdersViewModel
import com.afaryn.kaoslab.utils.PaymentConstants.KEY_TRANSACTION_RESULT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_FAILED
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_PENDING
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SETTLEMENT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SUCCESS
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.setLoading
import com.afaryn.kaoslab.utils.toast
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class OrderBaseFragment : Fragment() {

    abstract val status: OrderStatus
    private var _binding: FragmentMyOrderBinding? = null
    private val binding get() = _binding!!
    protected val vm: OrdersViewModel by activityViewModels()
    private val orderAdapter by lazy { OrderAdapter() }

    @Inject
    lateinit var uiKitApi: UiKitApi

    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val transactionResult = it.getParcelable<TransactionResult>(KEY_TRANSACTION_RESULT)

                when (transactionResult?.status) {
                    STATUS_SUCCESS, STATUS_PENDING, STATUS_SETTLEMENT -> {
                        toast("Click update status if you already paid")
                    }

                    STATUS_FAILED -> {
                        toast("Payment Failed: ${transactionResult.status}")
                    }

                    else -> {
                        toast("Payment Canceled or Unknown")
                    }
                }
            }
        } else {
            toast("Payment UI Canceled")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        observeData()
    }

    private fun setupRv() = binding.rvMyOrder.run {
        adapter = orderAdapter
        layoutManager = LinearLayoutManager(requireContext())
        orderAdapter.onUpdateStatus = { order, position ->
            updateStatus(order, position)
        }
    }

    private fun updateStatus(order: Order, position: Int) = lifecycleScope.launch {
        vm.updateStatus(order).collect {
            when (it) {
                is Resource.Loading -> setBtnLoading(position, true)
                is Resource.Error -> {
                    setBtnLoading(position, false)
                    toast(it.error)
                }
                is Resource.Success -> it.data?.let { (msg, paid) ->
                    setBtnLoading(position, false)
                    if (paid) toast(msg)
                    else showMidtransUi(msg)
                }
            }
        }
    }

    private fun showMidtransUi(token: String) {
        try {
            uiKitApi.startPaymentUiFlow(
                activity = requireActivity(),
                launcher = paymentLauncher,
                snapToken = token
            )
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Failed: ${e.message}")
        }
    }

    private fun observeData() = lifecycleScope.launch {
        vm.getOrders(status.value).collect {
            when (it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> setupView(it.data.orEmpty())
                else -> {}
            }
        }
    }

    private fun setupView(data: List<Order>) {
        binding.tvNoData.isVisible = data.isEmpty()
        orderAdapter.differ.submitList(data)
    }

    fun setBtnLoading(position: Int, isLoading: Boolean) {
        val holder = binding.rvMyOrder.findViewHolderForAdapterPosition(position) as? OrderViewHolder
        holder?.binding?.btnContactSeller?.setLoading(isLoading, "Update Status")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}