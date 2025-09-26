package com.afaryn.kaoslab.presentation.ui_customer.cart

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.CartAdapter
import com.afaryn.kaoslab.databinding.ActivityCartBinding
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.presentation.ui_customer.cart.checkout.CheckOutActivity
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private var _binding: ActivityCartBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<CartViewModel>()
    private val cartAdapter by lazy { CartAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRv()
        setActions()
        observeCart()
    }

    private fun setupRv() = binding.rvCart.apply {
        adapter = cartAdapter
        layoutManager = LinearLayoutManager(this@CartActivity)
        cartAdapter.onItemDelete = { cart ->
            MaterialAlertDialogBuilder(this@CartActivity)
                .setTitle("Confirmation")
                .setMessage("Are you sure to delete this product?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteCart(cart.id)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setActions() = with(binding) {
        btnBack.setOnClickListener { finish() }

        buttonCheckout.setOnClickListener {
            val totalAmount = vm.calculatePrice(cartAdapter.differ.currentList)
            val totalQty = vm.totalQty(cartAdapter.differ.currentList)

            if (totalAmount <= 0 || totalQty <= 0) return@setOnClickListener

            val order = Order(
                totalAmount = totalAmount,
                totalPieces = totalQty,
                cartProducts = cartAdapter.differ.currentList
            )

            startActivity(Intent(this@CartActivity, CheckOutActivity::class.java).apply {
                putExtra("order", order)
            })
        }
    }

    private fun observeCart() = lifecycleScope.launch {
        vm.getCart().collect {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> toast(it.error)
                is Resource.Success -> setupView(it.data.orEmpty())
            }
        }
    }

    private fun setupView(data: List<CartProduct> = emptyList()) {
        binding.layoutCartEmpty.isVisible = data.isEmpty()
        binding.totalBoxContainer.isVisible = data.isNotEmpty()
        binding.buttonCheckout.isVisible = data.isNotEmpty()

        binding.tvTotalPrice.text = vm.calculatePrice(data).toInt().formatRupiah()

        cartAdapter.differ.submitList(data)
    }

    private fun deleteCart(id: String) = lifecycleScope.launch {
        vm.deleteCart(id).collect {
            when (it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> setLoading(false)
                is Resource.Error -> {
                    setLoading(false)
                    toast(it.error)
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) = binding.run {
        buttonCheckout.isEnabled = !isLoading
        buttonCheckout.text = if (isLoading) "Loading..." else "Checkout"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}