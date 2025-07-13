package com.afaryn.kaoslab.ui_customer.custome.stepOne

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentTopBinding
import com.afaryn.kaoslab.ui_customer.custome.CustomeActivity
import com.afaryn.kaoslab.ui_customer.custome.adapter.CustomProductAdapter
import com.afaryn.kaoslab.ui_customer.custome.stepTwo.StepTwoFragment
import com.afaryn.kaoslab.ui_customer.custome.viewModel.CustomViewModel
import com.afaryn.kaoslab.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopFragment : Fragment() {

    private var _binding: FragmentTopBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<CustomViewModel>()
    private lateinit var adapter: CustomProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        viewModel.fetchTopProducts()

        binding.btnNext.setOnClickListener {
            val selectedItem = adapter.getSelectedItem()
            if (selectedItem != null) {
                viewModel.setSelectedProduct(selectedItem)
                Log.d("STEP_ONE_SELECTION", "Item yang dikirim ke StepTwo: ${selectedItem.name}")

                (activity as? CustomeActivity)?.goToStep(2)
            }
        }

    }

    private fun setupRecyclerView() {
        adapter = CustomProductAdapter(onItemSelected = { updateNextButtonVisibility() })
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.productState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = if (state.isLoading == true) View.VISIBLE else View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)
                    updateNextButtonVisibility()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateNextButtonVisibility() {
        val selectedItem = adapter.getSelectedItem()
        binding.btnNext.visibility = if (selectedItem != null) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
