package com.afaryn.kaoslab.presentation.ui_customer.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentHomeBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.presentation.ui_customer.cart.CartActivity
import com.afaryn.kaoslab.presentation.ui_customer.custome.CustomeActivity
import com.afaryn.kaoslab.presentation.ui_customer.custome.all.AllProductsActivity
import com.afaryn.kaoslab.presentation.ui_customer.desain.all.AllDesignActivity
import com.afaryn.kaoslab.presentation.ui_customer.desain.DetailDesainActivity
import com.afaryn.kaoslab.presentation.ui_customer.desain.favorite.FavoriteDesignActivity
import com.afaryn.kaoslab.presentation.ui_customer.home.adapter.BannerAdapter
import com.afaryn.kaoslab.presentation.ui_customer.home.adapter.ProductAdapter
import com.afaryn.kaoslab.presentation.ui_customer.home.viewModel.HomeViewModel
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.SearchListener
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private val recyclerViewAdapter by lazy { ProductAdapter() }
    private lateinit var bannerAdapter: BannerAdapter
    private var searchListener: SearchListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ((context is SearchListener)) searchListener = context
        else throw RuntimeException(SearchListener.runtimeException(context))
    }

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
        binding.dotIndicator.attachTo(binding.viewPager2)
        binding.dotIndicator.visibility = View.VISIBLE
    }

    private fun observer() = lifecycleScope.launch {
        viewModel.getProduct().collect {
            when (it) {
                is Resource.Loading -> binding.progressBar.show()

                is Resource.Success -> {
                    binding.progressBar.hide()
                    setRvRekom(it.data ?: emptyList())
                }

                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(it.error.toString())
                }
            }
        }
    }

    private fun action() = with(binding) {
        btnCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        listOf(searchCard, searchEditText).forEach {
            it.setOnClickListener {
                searchListener?.triggerSearchView(true)
            }
        }

        btnTop.setOnClickListener {
            startActivity(Intent(requireContext(), CustomeActivity::class.java).apply {
                putExtra("type", "0")
            })
        }

        btnBottom.setOnClickListener {
            startActivity(Intent(requireContext(), CustomeActivity::class.java).apply {
                putExtra("type", "1")
            })
        }

        btnHat.setOnClickListener {
            startActivity(Intent(requireContext(), CustomeActivity::class.java).apply {
                putExtra("type", "2")
            })
        }

        merchSeeAll.setOnClickListener {
            startActivity(Intent(requireContext(), AllDesignActivity::class.java))
        }

        categorySeeAll.setOnClickListener {
            startActivity(Intent(requireContext(), AllProductsActivity::class.java))
        }

        btnFavorite.setOnClickListener {
            startActivity(Intent(requireContext(), FavoriteDesignActivity::class.java))
        }
    }

    private fun setRvRekom(items: List<Design>) {
        val limitedItems = items.take(4)

        recyclerViewAdapter.onItemClick = { desain ->
            startActivity(Intent(requireContext(), DetailDesainActivity::class.java).apply {
                putExtra("desain", desain)
            })
        }

        binding.viewRecommendation.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = recyclerViewAdapter
        }

        recyclerViewAdapter.differ.submitList(limitedItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
