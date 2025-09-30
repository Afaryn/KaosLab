package com.afaryn.kaoslab.presentation.ui_customer.cart.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.CheckoutAdapter
import com.afaryn.kaoslab.databinding.ActivityCheckOutBinding
import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.presentation.ui_customer.address.AddressActivity
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.orZero
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckOutActivity : AppCompatActivity() {

    private var _binding: ActivityCheckOutBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<CheckOutViewModel>()
    private val cartAdapter by lazy { CheckoutAdapter() }
    private var selectedAddress: Address? = null

    private val addressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val address = result.data?.getParcelable<Address>("address")
                address?.let { setupAddress(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        setupRv()
        getData()
        getLastAddress()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }
        btnChangeAddress.setOnClickListener {
            addressLauncher.launch(
                Intent(this@CheckOutActivity, AddressActivity::class.java).apply {
                    putExtra("pickAddress", true)
                }
            )
        }
    }

    private fun setupRv() = binding.rvProducts.apply {
        adapter = cartAdapter
        layoutManager = LinearLayoutManager(this@CheckOutActivity)
    }

    @SuppressLint("SetTextI18n")
    private fun getData() = with(binding) {
        val data = intent.getParcelable<Order>("order")

        tvTotal.text = data?.totalAmount?.toInt()?.formatRupiah()
        tvSubTotal.text = (data?.totalAmount?.toInt().orZero() + 7000).toString()
        tvSubTotal2.text = (data?.totalAmount?.toInt().orZero() + 7000).toString()

        cartAdapter.differ.submitList(data?.cartProducts.orEmpty())
    }

    private fun getLastAddress() = lifecycleScope.launch {
        vm.getLastAddress().collect { r ->
            when (r) {
                is Resource.Error -> toast(r.error)
                is Resource.Success -> r.data?.let { setupAddress(it) }
                else -> {}
            }
        }
    }

    private fun setupAddress(address: Address) = binding.run {
        selectedAddress = address
        tvAddressName.text = address.name
        tvAddressDetail.text = address.location
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}