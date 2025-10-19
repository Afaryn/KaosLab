package com.afaryn.kaoslab.presentation.ui_customer.custome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityOrderSummaryBinding
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.model.DesignUplType
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.presentation.ui_customer.MainActivity
import com.afaryn.kaoslab.presentation.ui_customer.cart.checkout.CheckOutActivity
import com.afaryn.kaoslab.presentation.ui_customer.custome.viewModel.OrderSummaryViewModel
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private val vm by viewModels<OrderSummaryViewModel>()
    private var orderData: CartProduct? = null
    private var selectedColor: String? = null

    private var currentQuantity: Int = 0
    private var totalPcs: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppBar()
        retrieveOrderData()
        displayOrderDetails()
        setupQuantityControls()
        setupBottomButtons()
        updateTotalAmount()
    }

    private fun setupAppBar() {
        binding.backButton.setOnClickListener { finish() }
    }

    private fun retrieveOrderData() {
        selectedColor = intent.getStringExtra("SELECTED_COLOR")

        val orderData = intent.getParcelable<CartProduct>("ORDER_DATA") ?: run {
            Toast.makeText(this, "Data pesanan tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        this.orderData = orderData
        currentQuantity = orderData.quantity
        totalPcs = orderData.quantity
    }

    @SuppressLint("SetTextI18n")
    private fun displayOrderDetails() {
        val order = orderData ?: return

        binding.productTitle.text = order.orderItem?.title
        binding.sizeValue.text = "${order.orderItem?.size}"

        // --- Menampilkan Pilihan Warna ---
        selectedColor?.let { colorHex ->
            binding.tvColor.text = colorHex
            binding.civColor.circleBackgroundColor = colorHex.toColorInt()
        } ?: run {
            binding.colorRadioGroup.removeAllViews()
            val noColorLabel = TextView(this).apply {
                text = "Tidak ada warna dipilih"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(ContextCompat.getColor(context, R.color.darkBlue))
            }
            binding.colorRadioGroup.addView(noColorLabel)
        }

        // --- Menampilkan Detail Kustomisasi (Gambar atau Teks) ---
        binding.designPreviewImage.visibility = View.GONE
        binding.screenPrintingValue.visibility = View.GONE

        order.orderItem?.designType?.let {
            val customType =
                if (it.type == DesignUplType.Text.value) "Teks"
                else "Gambar"

            binding.screenPrintingLabel.text = "Kustomisasi: $customType"

            when (it.type) {
                DesignUplType.Text.value -> {
                    it.product?.imageUrl?.let { url -> binding.tshirtImage.glide(url) }

                    binding.screenPrintingValue.text = it.text ?: "Tidak ada teks"
                    binding.screenPrintingValue.visibility = View.VISIBLE
                    binding.tvOverlay.text = it.text
                }
                DesignUplType.Upload.value -> {
                    it.product?.imageUrl?.let { url -> binding.tshirtImage.glide(url) }
                    it.overlay?.let { url ->
                        binding.designPreviewImage.glide(url.toUri())
                        binding.designOverlay.glide(url.toUri())
                        binding.designPreviewImage.show()
                    }
                }
                DesignUplType.URL.value -> {
                    it.product?.imageUrl?.let { url -> binding.tshirtImage.glide(url) }
                    it.overlay?.let { url ->
                        binding.designOverlay.glide(url)
                        binding.designPreviewImage.glide(url)
                        binding.designPreviewImage.show()
                    }
                }
                else -> {
                    binding.screenPrintingLabel.text = "Kustomisasi: Tidak Ada"
                }
            }
        }
    }

    private fun setupQuantityControls() {
        binding.quantityText.text = currentQuantity.toString()

        binding.decrementButton.setOnClickListener {
            if (currentQuantity > totalPcs) {
                currentQuantity--
                binding.quantityText.text = currentQuantity.toString()
                updateTotalAmount()
            }
        }

        binding.incrementButton.setOnClickListener {
            currentQuantity++
            binding.quantityText.text = currentQuantity.toString()
            updateTotalAmount()
        }
    }

    private fun updateTotalAmount() {
        val order = orderData ?: return
        val pricePerUnit = if (order.quantity > 0) order.totalAmount / order.quantity else 0.0
        val total = pricePerUnit * currentQuantity
        binding.totalAmount.text = total.toInt().formatRupiah()
    }

    private fun setupBottomButtons() {
        binding.nextButton.setOnClickListener {
            orderData?.copy(quantity = currentQuantity)?.let { cart ->
                val order = Order(
                    totalAmount = cart.totalAmount,
                    totalPieces = cart.quantity,
                    cartProducts = listOf(cart)
                )

                startActivity(Intent(this@OrderSummaryActivity, CheckOutActivity::class.java).apply {
                    putExtra("order", order)
                })
            }
        }

        binding.cartButton.setOnClickListener {
            orderData?.copy(quantity = currentQuantity)?.let { cart -> addToCart(cart) } ?: run {
                toast("Gagal mendapatkan data order")
                return@setOnClickListener
            }
        }
    }

    private fun addToCart(cart: CartProduct) = lifecycleScope.launch {
        vm.addToCart(cart).collect {
            when (it) {
                is Resource.Loading -> setLoading(true)
                is Resource.Error -> {
                    setLoading(false)
                    toast(it.error)
                }
                is Resource.Success -> {
                    setLoading(false)
                    toast("Produk ditambah ke keranjang")
                    startActivity(Intent(this@OrderSummaryActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) = binding.run {
        val text = if (isLoading) "Loading..." else "Next"

        cartButton.isEnabled = !isLoading
        nextButton.isEnabled = !isLoading
        nextButton.text = text
    }
}