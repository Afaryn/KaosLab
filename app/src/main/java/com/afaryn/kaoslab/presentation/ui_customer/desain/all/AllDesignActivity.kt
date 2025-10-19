package com.afaryn.kaoslab.presentation.ui_customer.desain.all

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.databinding.ActivityUserDesignBinding
import com.afaryn.kaoslab.presentation.ui_customer.desain.DetailDesainActivity
import com.afaryn.kaoslab.presentation.ui_customer.home.adapter.ProductAdapter
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllDesignActivity : AppCompatActivity() {

    private var _binding: ActivityUserDesignBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<AllDesignViewModel>()
    private val designAdapter by lazy { ProductAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRv()
        collectDesignData()
    }

    private fun setupRv() = binding.rvDesign.apply {
        layoutManager = GridLayoutManager(this@AllDesignActivity, 2)
        adapter = designAdapter
        designAdapter.onItemClick = { desain ->
            startActivity(Intent(this@AllDesignActivity, DetailDesainActivity::class.java).apply {
                putExtra("desain", desain)
            })
        }
    }

    private fun collectDesignData() = lifecycleScope.launch {
        vm.getProduct().collect {
            when(it) {
                is Resource.Loading -> binding.progressBar.show()
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(it.error)
                }
                is Resource.Success -> binding.apply {
                    progressBar.hide()
                    tvNoData.isVisible = it.data.isNullOrEmpty()
                    designAdapter.differ.submitList(it.data.orEmpty())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}