package com.afaryn.kaoslab.ui_customer.custome

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityOrderSummaryBinding
import com.afaryn.kaoslab.model.DesignUplType
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.show
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private var orderData: Order? = null
    private var selectedColor: String? = null
    private var customTextContent: String? = null

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
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun retrieveOrderData() {
        selectedColor = intent.getStringExtra("SELECTED_COLOR")

        val orderData = intent.getParcelable<Order>("ORDER_DATA") ?: run {
            Toast.makeText(this, "Data pesanan tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        this.orderData = orderData
        currentQuantity = orderData.totalPieces
        totalPcs = orderData.totalPieces
    }

    @SuppressLint("SetTextI18n")
    private fun displayOrderDetails() {
        val order = orderData ?: return

        binding.productTitle.text = order.title
        binding.sizeValue.text = "${order.size}"

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

        order.designType?.let {
            val customType =
                if (it.type == DesignUplType.Text.value) "Teks"
                else "Gambar"

            binding.screenPrintingLabel.text = "Kustomisasi: $customType"

            when (it.type) {
                DesignUplType.Text.value -> {
                    it.product?.let { url -> binding.tshirtImage.glide(url) }

                    binding.screenPrintingValue.text = it.text ?: "Tidak ada teks"
                    binding.screenPrintingValue.visibility = View.VISIBLE
                    binding.tvOverlay.text = it.text
                }
                DesignUplType.Upload.value -> {
                    it.product?.let { url -> binding.tshirtImage.glide(url) }
                    it.overlay?.let { url ->
                        binding.designPreviewImage.glide(url.toUri())
                        binding.designOverlay.glide(url.toUri())
                        binding.designPreviewImage.show()
                    }
                }
                DesignUplType.URL.value -> {
                    it.product?.let { url -> binding.tshirtImage.glide(url) }
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
        val order = orderData ?: return

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
        val pricePerUnit = if (order.totalPieces > 0) order.totalAmount / order.totalPieces else 0.0
        val total = pricePerUnit * currentQuantity
        binding.totalAmount.text = formatRupiah(total.toInt())
    }

    private fun setupBottomButtons() {
        binding.nextButton.setOnClickListener {
            // Logika untuk melanjutkan ke proses pembayaran / checkout
            Toast.makeText(this, "Melanjutkan ke Pembayaran (Total: ${binding.totalAmount.text})", Toast.LENGTH_SHORT).show()
        }

        binding.cartButton.setOnClickListener {
            // Logika untuk menambahkan ke keranjang
            Toast.makeText(this, "Menambahkan item ke Keranjang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        formatter.maximumFractionDigits = 0
        return formatter.format(amount.toDouble())
    }
}