package com.afaryn.kaoslab.ui_owner.my_shop.product_template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentProductTemplateBinding
import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.ProductType
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductTemplateFragment : Fragment() {

    private var _binding: FragmentProductTemplateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductTemplateViewModel by viewModels()

    private lateinit var topAdapter: ProductTemplateAdapter
    private lateinit var bottomAdapter: ProductTemplateAdapter
    private lateinit var hatAdapter: ProductTemplateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavOwner()
        _binding = FragmentProductTemplateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Setup Top products adapter
        topAdapter = ProductTemplateAdapter { template ->
            onTemplateClick(template)
        }

        // Setup Bottom products adapter
        bottomAdapter = ProductTemplateAdapter { template ->
            onTemplateClick(template)
        }

        // Setup Hat products adapter
        hatAdapter = ProductTemplateAdapter { template ->
            onTemplateClick(template)
        }

        binding.apply {
            // Setup RecyclerViews with regular LinearLayoutManager
            rvTopTemplates.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = topAdapter
                setHasFixedSize(false)
            }

            rvBottomTemplates.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = bottomAdapter
                setHasFixedSize(false)
            }

            rvHatTemplates.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = hatAdapter
                setHasFixedSize(false)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnAddNewTemplate.setOnClickListener {
                viewModel.addNewTemplate()
                // TODO: Navigate to add new template screen
                Toast.makeText(requireContext(), "Add new template functionality will be implemented", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe product templates
            viewModel.productTemplates.collect { templates ->
                updateTemplatesByCategory(templates)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observe loading state
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observe error messages
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    viewModel.clearErrorMessage()
                }
            }
        }
    }

    private fun updateTemplatesByCategory(templates: List<ProductTemplate>) {
        // Filter templates by type directly from the passed templates parameter
        val topTemplates = templates.filter { it.type == ProductType.TOP.value }
        val bottomTemplates = templates.filter { it.type == ProductType.BOTTOM.value }
        val hatTemplates = templates.filter { it.type == ProductType.HAT.value }


        bottomTemplates.forEach { template ->
            android.util.Log.d("ProductTemplate", "Bottom item: ${template.name}, type: ${template.type}")
        }

        topAdapter.submitList(topTemplates)
        bottomAdapter.submitList(bottomTemplates)
        hatAdapter.submitList(hatTemplates)
    }

    private fun onTemplateClick(template: ProductTemplate) {
        // TODO: Navigate to template detail or edit screen
        Toast.makeText(requireContext(), "Clicked on ${template.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
