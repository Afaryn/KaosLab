package com.afaryn.kaoslab.presentation.ui_customer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentHomeBinding
import com.afaryn.kaoslab.domain.model.Product
import com.afaryn.kaoslab.presentation.ui_customer.home.adapter.BannerAdapter
import com.afaryn.kaoslab.presentation.ui_customer.home.adapter.ProductAdapter
import com.afaryn.kaoslab.presentation.ui_customer.home.viewModel.HomeViewModel
import com.afaryn.kaoslab.utils.UiState
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var recyclerViewAdapter: ProductAdapter
    private lateinit var bannerAdapter: BannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBanner()    // Tambahkan banner manual di sini
        observer()
        action()
    }

    private fun initBanner() {
        val banners = listOf(
            R.drawable.img_banner, // pastikan gambar ini ada di drawable
            R.drawable.img_banner,
            R.drawable.img_banner
        )

        bannerAdapter = BannerAdapter(banners)
        binding.viewPager2.adapter = bannerAdapter
        binding.dotIndicator.setViewPager2(binding.viewPager2)
        binding.dotIndicator.visibility = View.VISIBLE
    }

    private fun observer() {
        viewModel.product.observe(viewLifecycleOwner) { it ->
            when (it) {
                is UiState.Loading -> {
                    if (it.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    setRvRekom(it.data ?: emptyList())
                }

                is UiState.Error -> {
                    binding.progressBar.hide()
                    toast(it.error.toString())
                }

                else -> Unit
            }
        }
    }

    private fun action() {
        // Tambahkan action listener jika diperlukan
    }

    private fun setRvRekom(items: List<Product>) {
        val limitedItems = items.take(4)
        recyclerViewAdapter = ProductAdapter(limitedItems)
        binding.viewRecommendation.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = recyclerViewAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
