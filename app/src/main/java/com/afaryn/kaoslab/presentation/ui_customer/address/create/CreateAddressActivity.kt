package com.afaryn.kaoslab.presentation.ui_customer.address.create

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.databinding.ActivityCreateAddressBinding
import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAddressActivity : AppCompatActivity() {

    private var _binding: ActivityCreateAddressBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<CreateAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            if (listOf(etName, etLocation, etDetail, etPostalCode).any { it.text.toString().isEmpty() }) {
                toast("Harap isi semua bagian")
                return@setOnClickListener
            }

            val address = Address(
                name = etName.text.toString(),
                location = etLocation.text.toString(),
                postalCode = etPostalCode.text.toString(),
                detail = etDetail.text.toString()
            )

            createAddress(address)
        }
    }

    private fun createAddress(address: Address) = lifecycleScope.launch {
        vm.createAddress(address).collect {
            when(it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Error -> {
                    setLoading(false)
                    toast(it.error)
                }
                is Resource.Success -> {
                    setLoading(false)
                    finish()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) = binding.run {
        btnSave.isEnabled = !loading
        btnSave.text = if (loading) "Loading..." else "Save"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}