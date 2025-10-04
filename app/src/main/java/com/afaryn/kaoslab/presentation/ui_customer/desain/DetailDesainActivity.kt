package com.afaryn.kaoslab.presentation.ui_customer.desain

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.databinding.ActivityDetailDesainBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.License
import com.afaryn.kaoslab.presentation.ui_customer.desain.checkout.DesignCheckOutActivity
import com.afaryn.kaoslab.utils.CustomBottomDialog
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.glide

class DetailDesainActivity : AppCompatActivity() {

    private var _binding: ActivityDetailDesainBinding? = null
    private val binding get() = _binding!!
    private var design: Design? = null
    private var licenses: List<License>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailDesainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        setActions()
    }

    @SuppressLint("SetTextI18n")
    private fun getData() = binding.run {
        val data = intent.getParcelable<Design>("desain") ?: return
        design = data
        licenses = data.licenses

        data.thumbnailUrl.takeIf { it.isNotEmpty() }?.let { ivDesign.glide(it) }
        tvDesigner.text = data.designerName
        tvPrice.text = "from ${data.minPrice.toInt().formatRupiah()}"
        tvTitle.text = data.title
        tvDescription.text = data.description
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnBuyNow.setOnClickListener {
            if (licenses == null || design == null) return@setOnClickListener
            CustomBottomDialog(this@DetailDesainActivity).show(licenses!!) { l ->
                startActivity(
                    Intent(this@DetailDesainActivity, DesignCheckOutActivity::class.java).apply {
                        putExtra("desain", design!!.copy(selectedLicense = l))
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}