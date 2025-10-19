package com.afaryn.kaoslab.presentation.ui_customer.search

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.databinding.ActivitySearchResultBinding
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
class SearchResultActivity : AppCompatActivity() {

    private var _binding: ActivitySearchResultBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<SearchResultViewModel>()
    private val productAdapter by lazy { CustomProductAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        setupRv()
        collectAllProducts()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        etSearch.setOnClickListener {
            searchView.show()
        }

        with(binding.searchView) {
            editText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startActivity(Intent(this@SearchResultActivity, SearchResultActivity::class.java).apply {
                        putExtra("query", v.text.toString())
                    })
                    hide()
                    clearFocus()
                    finish()
                    true
                } else false
            }
        }
    }

    private fun setupRv() = binding.rvProducts.apply {
        adapter = productAdapter
        layoutManager = GridLayoutManager(this@SearchResultActivity, 2)
        productAdapter.onItemClicked = {
            startActivity(Intent(this@SearchResultActivity, CustomeActivity::class.java).apply {
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
                    getSearchData(it.data.orEmpty())
                }
            }
        }
    }

    private fun getSearchData(products: List<CustomProduct>) = binding.run {
        val query = intent.getStringExtra("query").orEmpty()

        val filteredData = products.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.type.contains(query, ignoreCase = true)
        }

        etSearch.setText(query)
        tvNoData.isVisible = products.isEmpty()
        productAdapter.submitList(filteredData)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}