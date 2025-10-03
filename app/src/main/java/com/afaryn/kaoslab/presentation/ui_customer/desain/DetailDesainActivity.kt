package com.afaryn.kaoslab.presentation.ui_customer.desain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.databinding.ActivityDetailDesainBinding

class DetailDesainActivity : AppCompatActivity() {

    private var _binding: ActivityDetailDesainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailDesainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}