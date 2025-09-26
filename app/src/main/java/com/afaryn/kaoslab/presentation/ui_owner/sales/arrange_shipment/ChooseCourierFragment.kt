package com.afaryn.kaoslab.presentation.ui_owner.sales.arrange_shipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentChooseCourierBinding
import com.afaryn.kaoslab.domain.model.Kurir
import com.afaryn.kaoslab.presentation.ui_owner.sales.arrange_shipment.ChooseCourierFragmentArgs
import com.afaryn.kaoslab.presentation.ui_owner.sales.arrange_shipment.ChooseCourierFragmentDirections
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseCourierFragment : Fragment() {
    private var _binding: FragmentChooseCourierBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArrangeShipmentViewModel by activityViewModels()
    private val args: ChooseCourierFragmentArgs by navArgs()

    private lateinit var courierAdapter: CourierAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseCourierBinding.inflate(inflater, container, false)
        hideBottomNavOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        loadCouriers()
    }

    private fun setupToolbar() {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        courierAdapter = CourierAdapter { courier ->
            viewModel.selectCourier(courier)
            navigateToTrackingNumber(courier)
        }

        binding.rvCouriers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = courierAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.getCouriers().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvCouriers.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvCouriers.visibility = View.VISIBLE
                    courierAdapter.submitList(response.data)
                }
                is Response.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvCouriers.visibility = View.GONE
                    toast(response.message)
                }
                else -> {}
            }
        }
    }

    private fun loadCouriers() {
        // Couriers are loaded through LiveData observation
    }

    private fun navigateToTrackingNumber(courier: Kurir) {
        val action = ChooseCourierFragmentDirections
            .actionChooseCourierFragmentToTrackingNumberFragment(
                orderId = args.orderId,
                courierId = courier.id,
                courierName = courier.name ?: "Unknown"
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
