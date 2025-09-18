package com.afaryn.kaoslab.ui_customer.custome

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityOrderSummaryBinding
import com.afaryn.kaoslab.model.Order
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private var orderData: Order? = null
    private var selectedColor: String? = null
    private var customTextContent: String? = null

    private var currentQuantity: Int = 0

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
        orderData = intent.getParcelableExtra("ORDER_DATA")
        selectedColor = intent.getStringExtra("SELECTED_COLOR")
        customTextContent = intent.getStringExtra("CUSTOM_TEXT_CONTENT")

        if (orderData == null) {
            Log.e("OrderSummary", "Error: Order data is null.")
            Toast.makeText(this, "Data pesanan tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
        } else {
            currentQuantity = orderData!!.totalPieces

            // Tambahkan log untuk memeriksa data pesanan
            Log.d("OrderSummary", "Order data received:")
            Log.d("OrderSummary", "Title: ${orderData?.title}")
            Log.d("OrderSummary", "Design ID: ${orderData?.designId}")
            Log.d("OrderSummary", "Design Image URL: ${orderData?.designImageUrl}")
            Log.d("OrderSummary", "Size: ${orderData?.size}")
            Log.d("OrderSummary", "Total Pieces: ${orderData?.totalPieces}")
            Log.d("OrderSummary", "Total Amount: ${orderData?.totalAmount}")
            Log.d("OrderSummary", "Selected Color: $selectedColor")
            Log.d("OrderSummary", "Custom Text Content: $customTextContent")
        }
    }

    private fun displayOrderDetails() {
        val order = orderData ?: return

        Glide.with(this)
            .load(order.designImageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.productMainImage)

        binding.productTitle.text = order.title
        binding.sizeValue.text = "${order.size}"

        // --- Menampilkan Pilihan Warna ---
        binding.colorRadioGroup.removeAllViews()
        selectedColor?.let { colorHex ->
            val colorLabel = TextView(this).apply {
                text = colorHex
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(ContextCompat.getColor(context, R.color.darkBlue))
            }
            binding.colorRadioGroup.addView(colorLabel)

            val colorIndicator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.color_circle_size_small),
                    resources.getDimensionPixelSize(R.dimen.color_circle_size_small)
                ).apply {
                    leftMargin = resources.getDimensionPixelSize(R.dimen.margin_small)
                }
                background = ContextCompat.getDrawable(context, R.drawable.circle_background)
            }
            try {
                (colorIndicator.background as? ColorDrawable)?.color = Color.parseColor(colorHex)
            } catch (e: IllegalArgumentException) {
                colorIndicator.setBackgroundColor(Color.GRAY)
            }
            binding.colorRadioGroup.addView(colorIndicator)
        } ?: run {
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

        when (order.designId) {
            "image_upload", "image_your_design" -> {
                binding.screenPrintingLabel.text = "Kustomisasi: Gambar"
                val imageUrlToLoad = if (order.designId == "image_upload") {
                    order.designImageUrl?.let { Uri.parse(it) }
                } else {
                    order.designImageUrl
                }

                Glide.with(this)
                    .load(imageUrlToLoad)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.designPreviewImage)

                binding.designPreviewImage.visibility = View.VISIBLE
            }
            "text" -> {
                binding.screenPrintingLabel.text = "Kustomisasi: Teks"
                binding.screenPrintingValue.text = customTextContent ?: "Tidak ada teks"
                binding.screenPrintingValue.visibility = View.VISIBLE
            }
            else -> { // "none"
                binding.screenPrintingLabel.text = "Kustomisasi: Tidak Ada"
            }
        }
    }

    private fun setupQuantityControls() {
        val order = orderData ?: return

        binding.quantityText.text = currentQuantity.toString()

        binding.decrementButton.setOnClickListener {
            if (currentQuantity > 1) {
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