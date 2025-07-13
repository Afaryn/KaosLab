package com.afaryn.kaoslab.ui_customer.custome.stepThree

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentStepThreeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StepThreeFragment : Fragment() {

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!

    // Simulasi apakah user telah memilih desain atau memasukkan teks
    private var designSelected: Boolean = false
    private var inputTextValid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepThreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabListeners()
        setupTextWatcher()
        updateNextButtonState()
    }

    private fun setupTabListeners() {
        val tabs = listOf(
            binding.tabUpload to ::showUploadTab,
            binding.tabYourDesign to ::showYourDesignTab,
            binding.tabAddText to ::showAddTextTab
        )

        tabs.forEach { (tabView, action) ->
            tabView.setOnClickListener {
                resetTabStyles()
                tabView.setBackgroundResource(R.drawable.tab_selected)
                tabView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                action()
            }
        }
    }

    private fun resetTabStyles() {
        val tabs = listOf(binding.tabUpload, binding.tabYourDesign, binding.tabAddText)
        tabs.forEach {
            it.setBackgroundResource(R.drawable.tab_unselected)
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBlue))
        }
    }

    private fun showUploadTab() {
        binding.designRecyclerView.visibility = View.VISIBLE
        binding.customTextInput.visibility = View.GONE
        designSelected = false // reset
        updateNextButtonState()
    }

    private fun showYourDesignTab() {
        binding.designRecyclerView.visibility = View.VISIBLE
        binding.customTextInput.visibility = View.GONE
        designSelected = false // reset
        updateNextButtonState()
    }

    private fun showAddTextTab() {
        binding.designRecyclerView.visibility = View.GONE
        binding.customTextInput.visibility = View.VISIBLE
        inputTextValid = binding.customTextInput.text.toString().isNotBlank()
        updateNextButtonState()
    }

    private fun setupTextWatcher() {
        binding.customTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                inputTextValid = s?.isNotBlank() == true
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun onDesignSelected() {
        // Panggil fungsi ini jika user memilih desain dari recyclerview
        designSelected = true
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        val isValid = designSelected || inputTextValid
        binding.btnNext.isEnabled = isValid
        binding.btnNext.setBackgroundTintList(
            ContextCompat.getColorStateList(
                requireContext(),
                if (isValid) R.color.darkBlue else R.color.textDisable
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
