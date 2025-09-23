package com.afaryn.kaoslab.ui_owner.sales.arrange_shipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afaryn.kaoslab.databinding.FragmentTrackingNumberBinding
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackingNumberFragment : Fragment() {
    private var _binding: FragmentTrackingNumberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArrangeShipmentViewModel by activityViewModels()
    private val args: TrackingNumberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingNumberBinding.inflate(inflater, container, false)
        hideBottomNavOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupSelectedCourierInfo()
        observeViewModel()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSelectedCourierInfo() {
        // Display the selected courier name from navigation arguments
        binding.selectedCourierName.text = args.courierName
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.updateOrderState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        binding.btnConfirm.isEnabled = false
                        binding.btnConfirm.text = "Updating..."
                    }
                    is Response.Success -> {
                        if (response.data.isNotEmpty()) {
                            toast("Order updated successfully")
                            findNavController().popBackStack(
                                findNavController().graph.startDestinationId,
                                false
                            )
                        }
                    }
                    is Response.Error -> {
                        binding.btnConfirm.isEnabled = true
                        binding.btnConfirm.text = "Confirm Shipment"
                        toast(response.message)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnConfirm.setOnClickListener {
            val trackingNumber = binding.etTrackingNumber.text.toString().trim()
            if (trackingNumber.isEmpty()) {
                toast("Please enter tracking number")
                return@setOnClickListener
            }

            viewModel.setNoResi(trackingNumber)
            // Use the courierId from navigation arguments
            viewModel.updateOrderToShippedWithCourierInfo(args.orderId, args.courierId, trackingNumber)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
