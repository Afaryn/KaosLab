package com.afaryn.kaoslab.presentation.ui_customer.desain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.databinding.ActivityDetailDesainBinding
import com.afaryn.kaoslab.domain.model.License
import com.afaryn.kaoslab.utils.CustomBottomDialog
import com.afaryn.kaoslab.utils.toast

class DetailDesainActivity : AppCompatActivity() {

    private var _binding: ActivityDetailDesainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailDesainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val licenses = listOf(
            License(
                "License 1", "standard",
                name = "Standard",
                description = "Lorem the ipsum badabim",
                features = listOf(),
                price = 30000.0,
                isDefault = false,
            ),
            License(
                "License 2", "exclusive",
                name = "Exclusive",
                description = "Lorem the ipsum badabim",
                features = listOf(),
                price = 80000.0,
                isDefault = false,
            ),
        )

        binding.btnBuyNow.setOnClickListener {
            CustomBottomDialog(this).show(licenses) {
                toast(it.name)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}