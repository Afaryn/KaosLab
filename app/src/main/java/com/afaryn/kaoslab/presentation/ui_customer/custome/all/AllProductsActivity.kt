package com.afaryn.kaoslab.presentation.ui_customer.custome.all

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.databinding.ActivityAllProductsBinding
import com.afaryn.kaoslab.domain.model.CustomProduct
import com.afaryn.kaoslab.presentation.ui_customer.custome.CustomeActivity
import com.afaryn.kaoslab.presentation.ui_customer.custome.adapter.CustomProductAdapter
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllProductsActivity : AppCompatActivity() {

    private var _binding: ActivityAllProductsBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<AllProductsViewModel>()
    private val productAdapter by lazy { CustomProductAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAllProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setActions()
        setupRv()
        collectAllProducts()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }
    }

    private fun setupRv() = binding.rvProducts.apply {
        adapter = productAdapter
        layoutManager = GridLayoutManager(this@AllProductsActivity, 2)
        productAdapter.onItemClicked = {
            startActivity(Intent(this@AllProductsActivity, CustomeActivity::class.java).apply {
                putExtra("product", it)
            })
        }
    }

    private fun collectAllProducts() = lifecycleScope.launch {
        vm.allProducts.collect {
            when(it) {
                is Resource.Loading -> binding.progressBar.show()
                is Resource.Error -> {
                    binding.progressBar.hide()
                    binding.tvNoData.show()
                    toast(it.error)
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    setupView(it.data.orEmpty())
                }
            }
        }
    }

    private fun setupView(data: List<CustomProduct>) = binding.run {
        tvNoData.isVisible = data.isEmpty()
        productAdapter.submitList(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}