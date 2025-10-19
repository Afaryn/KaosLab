package com.afaryn.kaoslab.presentation.ui_customer.account.becomeseller

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.databinding.ActivityBecomeSellerBinding
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.setLoading
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BecomeSellerActivity : AppCompatActivity() {

    private var _binding: ActivityBecomeSellerBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<BecomeSellerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBecomeSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnConfirm.setOnClickListener {
            val accountNo = etNorek.text.toString()

            if (accountNo.isEmpty()) {
                toast("Please fill out your payment account number")
                return@setOnClickListener
            }

            becomeSeller(accountNo)
        }
    }

    private fun becomeSeller(accountNo: String) = lifecycleScope.launch {
        vm.becomeSeller(accountNo).collect {
            when(it) {
                is Resource.Loading -> {
                    binding.btnConfirm.setLoading(true, "Become A Seller")
                    binding.progressBar.show()
                }
                is Resource.Error -> {
                    binding.btnConfirm.setLoading(false, "Become A Seller")
                    binding.progressBar.hide()
                }
                is Resource.Success -> {
                    binding.btnConfirm.setLoading(false, "Become A Seller")
                    binding.progressBar.hide()
                    toast("You are now a seller")
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}