package com.afaryn.kaoslab.presentation.ui_customer.cart.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityCheckOutBinding

class CheckOutActivity : AppCompatActivity() {

    private var _binding: ActivityCheckOutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}