package com.afaryn.kaoslab.presentation.ui_customer.account.becomeseller

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.R
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

        setupBankType()
        setActions()
    }

    private fun setupBankType() {
        val options = arrayOf(
            "BCA",
            "Mandiri",
            "BNI",
            "BRI",
            "Permata",
            "CIMB Niaga"
        )

        val adapter = ArrayAdapter(this, R.layout.dropdown_menu_item, options)
        binding.etType.setAdapter(adapter)

        binding.etType.setOnItemClickListener { parent, view, position, id ->
            val selectedOption = parent.getItemAtPosition(position) as String
            binding.etType.setText(selectedOption)
            binding.etType.dismissDropDown()
        }

        binding.etType.setOnClickListener {
            adapter.filter.filter(null)
            binding.etType.showDropDown()
        }
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnConfirm.setOnClickListener {
            val accountNo = etNorek.text.toString()
            val type = etType.text.toString()

            if (accountNo.isEmpty() || type.isEmpty()) {
                toast("Please fill out your payment detail")
                return@setOnClickListener
            }

            becomeSeller(accountNo, type)
        }
    }

    private fun becomeSeller(accountNo: String, type: String) = lifecycleScope.launch {
        vm.becomeSeller(accountNo, type).collect {
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