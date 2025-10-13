package com.afaryn.kaoslab.presentation.ui_customer.desain.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityDesignCheckOutBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.License
import com.afaryn.kaoslab.domain.model.SnapResponse
import com.afaryn.kaoslab.presentation.ui_customer.MainActivity
import com.afaryn.kaoslab.utils.PaymentConstants.KEY_TRANSACTION_RESULT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_FAILED
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_PENDING
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SETTLEMENT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SUCCESS
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.toast
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DesignCheckOutActivity : AppCompatActivity() {

    private var _binding: ActivityDesignCheckOutBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<DesignCheckOutViewModel>()
    private var design: Design? = null
    private var license: License? = null

    @Inject
    lateinit var uiKitApi: UiKitApi

    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val transactionResult = it.getParcelable<TransactionResult>(KEY_TRANSACTION_RESULT)

                when (transactionResult?.status) {
                    STATUS_PENDING -> addDesign(design!!, true)
                    STATUS_SUCCESS, STATUS_SETTLEMENT -> addDesign(design!!, false)
                    STATUS_FAILED -> toast("Payment Failed: ${transactionResult.status}")
                    else -> toast("Payment canceled or design is owned")
                }
            }
        } else {
            toast("Payment UI Canceled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDesignCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        getData()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnPayment.setOnClickListener {
            if (design == null || license == null) {
                toast("Trouble getting design data")
                return@setOnClickListener
            }

            getSnapToken(design!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() = binding.apply {
        val design = intent.getParcelable<Design>("desain") ?: return@apply
        val license = design.selectedLicense ?: return@apply

        this@DesignCheckOutActivity.design = design
        this@DesignCheckOutActivity.license = license

        design.thumbnailUrl.takeIf { it.isNotEmpty() }?.let { ivDesign.glide(it) }
        tvProductName.text = design.title
        tvLicenseType.text = license.name
        tvPrice.text = license.price.toInt().formatRupiah()
        tvSubTotal2.text = license.price.toInt().formatRupiah()

        tvTncHolder.text = "${license.name} Features"

        val adapter = object : ArrayAdapter<String>(
            this@DesignCheckOutActivity,
            R.layout.item_feature,
            R.id.tv_feature,
            license.features
        ) {}
        lvFeatures.adapter = adapter
    }

    private fun getSnapToken(design: Design) = lifecycleScope.launch {
        vm.getSnapToken(design).collect {
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

    private fun addDesign(design: Design, isPending: Boolean) = lifecycleScope.launch {
        vm.addDesign(design, isPending).collect {
            when(it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> {
                    toast("Payment is being processed")
                    startActivity(Intent(this@DesignCheckOutActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("design", true)
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