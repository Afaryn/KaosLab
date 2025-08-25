package com.afaryn.kaoslab.ui_owner.my_shop.add_template

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.afaryn.kaoslab.databinding.DialogSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessDialogFragment(
    private val onOkClicked: () -> Unit
) : DialogFragment() {

    private var _binding: DialogSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSuccessBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        binding.btnOk.setOnClickListener {
            onOkClicked()
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
