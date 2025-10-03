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
import com.afaryn.kaoslab.domain.model.SnapResponse
import com.afaryn.kaoslab.presentation.ui_customer.MainActivity
import com.afaryn.kaoslab.presentation.ui_customer.address.AddressActivity
import com.afaryn.kaoslab.utils.PaymentConstants.KEY_TRANSACTION_RESULT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_FAILED
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_PENDING
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SETTLEMENT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SUCCESS
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.orZero
import com.afaryn.kaoslab.utils.toast
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CheckOutActivity : AppCompatActivity() {

    private var _binding: ActivityCheckOutBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<CheckOutViewModel>()
    private val cartAdapter by lazy { CheckoutAdapter() }
    private var selectedAddress: Address? = null
    private var order: Order? = null

    @Inject
    lateinit var uiKitApi: UiKitApi

    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val transactionResult = it.getParcelable<TransactionResult>(KEY_TRANSACTION_RESULT)

                when (transactionResult?.status) {
                    STATUS_SUCCESS, STATUS_PENDING, STATUS_SETTLEMENT -> {
                        order?.let { o -> clearCart(o) }
                    }

                    STATUS_FAILED -> {
                        toast("Payment Failed: ${transactionResult.status}")
                    }

                    else -> {
                        toast("Payment Canceled or Unknown")
                    }
                }
            }
        } else {
            toast("Payment UI Canceled")
        }
    }

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

        btnPayment.setOnClickListener {
            if (selectedAddress == null || order == null) {
                toast("Please pick a shipping address")
                return@setOnClickListener
            }

            getSnapToken(order!!)
        }
    }

    private fun getSnapToken(order: Order) = lifecycleScope.launch {
        vm.getSnapToken(order).collect {
            when (it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Error -> {
                    setLoading(false)
                    toast(it.error)
                }

                is Resource.Success -> {
                    setLoading(false)
                    it.data?.let { r -> showMidtransUi(r) }
                }
            }
        }
    }

    private fun showMidtransUi(response: SnapResponse) {
        try {
            uiKitApi.startPaymentUiFlow(
                activity = this,
                launcher = paymentLauncher,
                snapToken = response.token
            )
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Failed: ${e.message}")
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnPayment.isEnabled = !isLoading
        binding.btnPayment.text = if (isLoading) "Loading..." else "Payment"
    }

    private fun setupRv() = binding.rvProducts.apply {
        adapter = cartAdapter
        layoutManager = LinearLayoutManager(this@CheckOutActivity)
    }

    @SuppressLint("SetTextI18n")
    private fun getData() = with(binding) {
        val data = intent.getParcelable<Order>("order")
        order = data

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


    private fun clearCart(order: Order) = lifecycleScope.launch {
        vm.clearCart(order).collect {
            when(it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> {
                    toast("Payment is being processed")
                    startActivity(Intent(this@CheckOutActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("order", true)
                    })
                }
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
