package com.afaryn.kaoslab.presentation.ui_customer.cart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.afaryn.kaoslab.databinding.ActivityCartBinding
import com.afaryn.kaoslab.domain.model.CartProduct

class CartActivity : AppCompatActivity() {

    private var _binding: ActivityCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        observeCart()
    }

    private fun setActions() = with(binding) {
        btnBack.setOnClickListener { finish() }
    }

    private fun observeCart(data: List<CartProduct> = emptyList()) {
        binding.layoutCartEmpty.isVisible = data.isEmpty()
        binding.totalBoxContainer.isVisible = data.isNotEmpty()
        binding.buttonCheckout.isVisible = data.isNotEmpty()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}