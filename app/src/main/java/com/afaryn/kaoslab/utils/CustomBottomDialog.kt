package com.afaryn.kaoslab.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.LayoutBottomDialogBinding
import com.afaryn.kaoslab.domain.model.License
import com.google.android.material.radiobutton.MaterialRadioButton

class CustomBottomDialog(context: Context) {

    private val dialog = Dialog(context)
    private val binding: LayoutBottomDialogBinding =
        LayoutBottomDialogBinding.inflate(LayoutInflater.from(context))

    private var selectedLicense: License? = null
    private var onConfirmClick: ((License) -> Unit)? = null

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.BOTTOM)
            attributes.windowAnimations = android.R.style.Animation_Dialog
        }
        dialog.setCancelable(false)
    }

    fun show(licenses: List<License>, onConfirm: (License) -> Unit) {
        onConfirmClick = onConfirm
        binding.containerLicenses.removeAllViews()

        val inflater = LayoutInflater.from(binding.root.context)

        licenses.forEach { license ->
            val itemView = inflater.inflate(R.layout.item_license_dialog, binding.containerLicenses, false)

            val rb = itemView.findViewById<MaterialRadioButton>(R.id.rb_license)
            val tvName = itemView.findViewById<TextView>(R.id.tv_license_name)
            val tvDesc = itemView.findViewById<TextView>(R.id.tv_license_desc)
            val tvPrice = itemView.findViewById<TextView>(R.id.tv_price)

            tvName.text = license.name
            tvDesc.text = license.description
            tvPrice.text = license.price.toInt().formatRupiah()

            listOf(itemView, rb).forEach {
                it.setOnClickListener {
                    (0 until binding.containerLicenses.childCount).forEach { i ->
                        val other = binding.containerLicenses.getChildAt(i)
                            .findViewById<MaterialRadioButton>(R.id.rb_license)
                        other.isChecked = false
                    }
                    rb.isChecked = true
                    selectedLicense = license
                }
            }

            if (license.isDefault) {
                rb.isChecked = true
                selectedLicense = license
            }

            binding.containerLicenses.addView(itemView)
        }

        binding.btnContinue.setOnClickListener {
            selectedLicense?.let { onConfirmClick?.invoke(it) }
            dismiss()
        }

        dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) dialog.dismiss()
    }
}
