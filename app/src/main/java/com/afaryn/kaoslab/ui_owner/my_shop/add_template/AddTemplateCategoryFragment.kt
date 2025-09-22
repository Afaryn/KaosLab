package com.afaryn.kaoslab.ui_owner.my_shop.add_template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentAddTemplateCategoryBinding
import com.afaryn.kaoslab.model.ProductType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTemplateCategoryFragment : Fragment() {

    private var _binding: FragmentAddTemplateCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTemplateCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupRadioGroupListener()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSelect.setOnClickListener {
            val selectedCategory = getSelectedCategory()
            selectedCategory?.let { category ->
                // Navigate with Bundle instead of SafeArgs for now
                val bundle = Bundle().apply {
                    putString("selectedCategory", category)
                }
                findNavController().navigate(R.id.action_addTemplateCategoryFragment_to_addTemplateDetailsFragment, bundle)
            }
        }
    }

    private fun setupRadioGroupListener() {
        binding.radioGroupCategory.setOnCheckedChangeListener { _, checkedId ->
            binding.btnSelect.isEnabled = checkedId != -1
        }
    }

    private fun getSelectedCategory(): String? {
        return when (binding.radioGroupCategory.checkedRadioButtonId) {
            R.id.radioTop -> ProductType.TOP.value
            R.id.radioBottom -> ProductType.BOTTOM.value
            R.id.radioHat -> ProductType.HAT.value
            R.id.radioOthers -> ProductType.OTHERS.value
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
