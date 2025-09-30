package com.afaryn.kaoslab.presentation.ui_customer.address

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.AddressAdapter
import com.afaryn.kaoslab.databinding.ActivityAddressBinding
import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.presentation.ui_customer.address.create.CreateAddressActivity
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private var _binding: ActivityAddressBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<AddressViewModel>()
    private val addressAdapter by lazy { AddressAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRv()
        setActions()
        getAddress()
    }

    private fun setupRv() = binding.rvAddress.apply {
        adapter = addressAdapter
        layoutManager = LinearLayoutManager(this@AddressActivity)
        addressAdapter.onItemClick = {
            if (intent.getBooleanExtra("pickAddress", false)) {
                setResult(RESULT_OK, Intent().apply {
                    putExtra("address", it)
                })
                finish()
            }
        }
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }
        btnAddAddress.setOnClickListener {
            startActivity(Intent(this@AddressActivity, CreateAddressActivity::class.java))
        }
    }

    private fun getAddress() = lifecycleScope.launch {
        vm.getAddress().collect { r ->
            when (r) {
                is Resource.Loading -> binding.progressBar.show()
                is Resource.Error -> {
                    binding.progressBar.hide()
                    toast(r.error)
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    r.data?.let { setupView(it) }
                }
            }
        }
    }

    private fun setupView(addresses: List<Address>) = binding.run {
        tvNoData.isVisible = addresses.isEmpty()
        addressAdapter.differ.submitList(addresses)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}