package com.afaryn.kaoslab.ui_customer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.databinding.FragmentHomeBinding
import com.afaryn.kaoslab.model.Product
import com.afaryn.kaoslab.ui_customer.home.adapter.ProductAdapter
import com.afaryn.kaoslab.ui_customer.home.viewModel.HomeViewModel
import com.afaryn.kaoslab.utils.UiState
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class HomeFragment : Fragment() {

    private var _binding :FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var recyclerViewAdapter: ProductAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        action()

    }

    private fun observer(){
        viewModel.product.observe(viewLifecycleOwner) { it ->
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    setRvRekom(it.data!!)
                }
                is UiState.Error -> {
                    binding.progressBar.hide()
                    toast(it.error.toString())
                }
                else -> {

                }
            }
        }
    }

    private fun action(){

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setRvRekom(items: List<Product>) {
        val limitedItems = items.take(4)
        recyclerViewAdapter = ProductAdapter(limitedItems)
        binding.viewRecommendation.adapter = recyclerViewAdapter
        binding.viewRecommendation.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}