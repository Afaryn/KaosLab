package com.afaryn.kaoslab.presentation.ui_customer.account.design

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
import com.afaryn.kaoslab.data.adapter.DesignAdapter
import com.afaryn.kaoslab.databinding.FragmentPendingPaymentBinding
import com.afaryn.kaoslab.domain.model.DesignOrder
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.utils.PaymentConstants.KEY_TRANSACTION_RESULT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_FAILED
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_PENDING
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SETTLEMENT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SUCCESS
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.downloadDesign
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.setLoading
import com.afaryn.kaoslab.utils.showRatingDialog
import com.afaryn.kaoslab.utils.toast
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class MyDesignBaseFragment : Fragment() {

    abstract val status: DesignOrderStatus
    private var _binding: FragmentPendingPaymentBinding? = null
    private val binding get() = _binding!!
    protected val vm: MyDesignViewModel by activityViewModels()
    private val designAdapter by lazy { DesignAdapter() }

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
        _binding = FragmentPendingPaymentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        observeData()
    }

    private fun setupRv() = binding.rvMyDesign.run {
        adapter = designAdapter
        layoutManager = LinearLayoutManager(requireContext())
        designAdapter.apply {
            onUpdateStatus = { order, position ->
                updateStatus(order, position)
            }
            onUseDesign = { order, position ->
                order.design.fileUrl.takeIf { it.isNotEmpty() }?.let {
                    setBtnLoading(position, true, "Download")
                    requireContext().downloadDesign(
                        it,
                        onFinished = {
                            setDownloaded(order, position)
                        },
                        onError = {
                            toast("Failed to download design")
                            setBtnLoading(position, false, "Download")
                        }
                    )
                }
            }
            onLimit = {
                toast("You have downloaded this design before, buy the exclusive package for unlimited downloads")
            }
            onRateDesign = { order, designId, position ->
                requireActivity().showRatingDialog { rate ->
                    rateDesign(order, designId, rate, position)
                }
            }
        }

    }
    private fun setDownloaded(order: DesignOrder, position: Int) = lifecycleScope.launch {
        vm.setDownloaded(order).collect {
            when (it) {
                is Resource.Error -> {
                    setBtnLoading(position, false, "Download")
                    toast(it.error)
                }

                is Resource.Success -> {
                    setBtnLoading(position, false, "Download")
                    toast("Design downloaded successfully")
                }

                else -> {}
            }
        }
    }

    private fun updateStatus(order: DesignOrder, position: Int) = lifecycleScope.launch {
        vm.updateStatus(order).collect {
            when (it) {
                is Resource.Loading -> setBtnLoading(position, true, "Update Status")
                is Resource.Error -> {
                    setBtnLoading(position, false, "Update Status")
                    toast(it.error)
                }
                is Resource.Success -> it.data?.let { (msg, paid) ->
                    setBtnLoading(position, false, "Update Status")
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
        vm.getDesigns(status).collect {
            when (it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> setupView(it.data.orEmpty())
                else -> {}
            }
        }
    }

    private fun rateDesign(order: DesignOrder, designId: String, rate: Float, position: Int) = lifecycleScope.launch {
        vm.rateDesign(order, designId, rate).collect {
            when (it) {
                is Resource.Loading -> setBtnRateLoading(position, true, "Rate")

                is Resource.Error -> {
                    setBtnRateLoading(position, false, "Rate")
                    toast(it.error)
                }

                is Resource.Success -> {
                    toast("You rated the design successfully")
                }
            }
        }
    }

    private fun setupView(data: List<DesignOrder>) {
        binding.tvNoData.isVisible = data.isEmpty()
        designAdapter.differ.submitList(data)
    }

    fun setBtnLoading(position: Int, isLoading: Boolean, placeholder: String) {
        val holder = binding.rvMyDesign.findViewHolderForAdapterPosition(position) as? DesignAdapter.DesignViewHolder
        holder?.binding?.btnUse?.setLoading(isLoading, placeholder)
    }

    fun setBtnRateLoading(position: Int, isLoading: Boolean, placeholder: String) {
        val holder = binding.rvMyDesign.findViewHolderForAdapterPosition(position) as? DesignAdapter.DesignViewHolder
        holder?.binding?.btnRate?.setLoading(isLoading, placeholder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}