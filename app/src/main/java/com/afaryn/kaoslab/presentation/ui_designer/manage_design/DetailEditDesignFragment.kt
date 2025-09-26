package com.afaryn.kaoslab.presentation.ui_designer.manage_design

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afaryn.kaoslab.databinding.FragmentDetailEditDesignBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.License
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavDesigner
import com.afaryn.kaoslab.utils.successDialog
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailEditDesignFragment : Fragment() {

    private var _binding: FragmentDetailEditDesignBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DesignViewModel by viewModels()
    private val args: DetailEditDesignFragmentArgs by navArgs()

    private var selectedImageUri: Uri? = null
    private var currentDesign: Design? = null
    private val categories = listOf(
        "Illustrations",
        "Logo Design",
        "Business Cards",
        "Flyers",
        "Posters",
        "T-Shirt Design",
        "Social Media",
        "Web Design",
        "Print Design",
        "Other"
    )

    // Permission launcher for storage access
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchImagePicker()
        } else {
            Toast.makeText(requireContext(), "Permission required to access images", Toast.LENGTH_SHORT).show()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                showImagePreview(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavDesigner()
        _binding = FragmentDetailEditDesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCategoryDropdown()
        setupObservers()
        setupActions()
        setupTagsInput()

        // Load design data
        viewModel.getDesignById(args.designId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.actvCategory.setAdapter(adapter)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.singleDesignState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        // Do nothing
                    }
                    is Response.Loading -> {
                        showLoading()
                    }
                    is Response.Success -> {
                        hideLoading()
                        currentDesign = state.data
                        populateFields(state.data)
                    }
                    is Response.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.editDesignState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        hideUpdateLoading()
                    }
                    is Response.Loading -> {
                        showUpdateLoading()
                    }
                    is Response.Success -> {
                        hideUpdateLoading()
                        showSuccessDialog()
                    }
                    is Response.Error -> {
                        hideUpdateLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun setupActions() {
        binding.layoutUploadPlaceholder.setOnClickListener {
            openImagePicker()
        }

        binding.ivPreview.setOnClickListener {
            openImagePicker()
        }

        binding.btnAddCustomLicense.setOnClickListener {
            showAddCustomLicenseDialog()
        }

        binding.btnUpdate.setOnClickListener {
            if (validateForm()) {
                updateDesign()
            }
        }
    }

    private fun setupTagsInput() {
        // Handle adding tags with enter key or end icon click
        binding.etNewTag.setOnEditorActionListener { _, _, _ ->
            addTag()
            true
        }

        binding.etNewTag.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.etNewTag.text.toString().trim().isNotEmpty()) {
                addTag()
            }
        }

        // Handle end icon click to add tag
        binding.etNewTag.setOnKeyListener { _, keyCode, event ->
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN) {
                addTag()
                true
            } else {
                false
            }
        }
    }

    private fun addTag() {
        val tagText = binding.etNewTag.text.toString().trim()
        if (tagText.isNotEmpty()) {
            // Check if tag already exists
            val existingTags = mutableListOf<String>()
            for (i in 0 until binding.chipGroupTags.childCount) {
                val chip = binding.chipGroupTags.getChildAt(i) as Chip
                existingTags.add(chip.text.toString())
            }

            if (!existingTags.contains(tagText)) {
                createTagChip(tagText)
                binding.etNewTag.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Tag already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createTagChip(tagText: String) {
        val chip = Chip(requireContext())
        chip.text = tagText
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.chipGroupTags.removeView(chip)
        }
        binding.chipGroupTags.addView(chip)
    }

    private fun populateFields(design: Design) {
        // Populate basic fields
        binding.etProductName.setText(design.title)
        binding.etProductDescription.setText(design.description)
        binding.actvCategory.setText(design.category, false)

        // Load design image
        if (design.thumbnailUrl.isNotEmpty()) {
            Glide.with(this)
                .load(design.thumbnailUrl)
                .into(binding.ivPreview)
            binding.ivPreview.visibility = View.VISIBLE
            binding.layoutUploadPlaceholder.visibility = View.GONE
        }

        // Populate tags
        design.tags.forEach { tag ->
            createTagChip(tag)
        }

        // Populate licenses
        populateLicenseFields(design.licenses)
    }

    private fun populateLicenseFields(licenses: List<License>) {
        val standardLicense = licenses.find { it.type == "standard" }
        val exclusiveLicense = licenses.find { it.type == "exclusive" }

        // Reset all checkboxes first
        binding.cbStandard1.isChecked = false
        binding.cbStandard2.isChecked = false
        binding.cbStandard3.isChecked = false
        binding.cbStandard4.isChecked = false
        binding.cbStandard5.isChecked = false
        binding.cbExclusive1.isChecked = false
        binding.cbExclusive2.isChecked = false
        binding.cbExclusive3.isChecked = false
        binding.cbExclusive4.isChecked = false
        binding.cbExclusive5.isChecked = false

        // Populate standard license
        standardLicense?.let { license ->
            binding.etStandardPrice.setText(license.price.toString())
            license.features.forEach { feature ->
                when (feature) {
                    "Use for custom only (one-time use)" -> binding.cbStandard1.isChecked = true
                    "Commercial use" -> binding.cbStandard2.isChecked = true
                    "Resale rights" -> binding.cbStandard3.isChecked = true
                    "Unlimited downloads" -> binding.cbStandard4.isChecked = true
                    "Print on demand" -> binding.cbStandard5.isChecked = true
                }
            }
        }

        // Populate exclusive license
        exclusiveLicense?.let { license ->
            binding.etExclusivePrice.setText(license.price.toString())
            license.features.forEach { feature ->
                when (feature) {
                    "Use for custom only (one-time use)" -> binding.cbExclusive1.isChecked = true
                    "Commercial use" -> binding.cbExclusive2.isChecked = true
                    "Resale rights" -> binding.cbExclusive3.isChecked = true
                    "Unlimited downloads" -> binding.cbExclusive4.isChecked = true
                    "Print on demand" -> binding.cbExclusive5.isChecked = true
                }
            }
        }
    }

    private fun openImagePicker() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchImagePicker()
            } else {
                val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                    launchImagePicker()
                } else {
                    permissionLauncher.launch(permission)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetailEditDesignFragment", "Error in openImagePicker", e)
            launchImagePicker()
        }
    }

    private fun launchImagePicker() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/webp"))
                }
            } else {
                Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
            }

            val fallbackIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            val chooser = Intent.createChooser(fallbackIntent, "Select Design File")
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intent))

            imagePickerLauncher.launch(chooser)

        } catch (e: Exception) {
            android.util.Log.e("DetailEditDesignFragment", "Error launching image picker", e)
            Toast.makeText(requireContext(), "Unable to open image picker. Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showImagePreview(uri: Uri) {
        binding.layoutUploadPlaceholder.visibility = View.GONE
        binding.ivPreview.visibility = View.VISIBLE

        Glide.with(this)
            .load(uri)
            .into(binding.ivPreview)
    }

    private fun showAddCustomLicenseDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Custom License")
            .setMessage("Custom license feature will be available in future updates")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun validateForm(): Boolean {
        val title = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()

        when {
            title.isEmpty() -> {
                binding.etProductName.error = "Product name is required"
                return false
            }
            description.isEmpty() -> {
                binding.etProductDescription.error = "Description is required"
                return false
            }
            category.isEmpty() -> {
                showError("Please select a category")
                return false
            }
            else -> return true
        }
    }

    private fun getSelectedTags(): List<String> {
        val tags = mutableListOf<String>()
        for (i in 0 until binding.chipGroupTags.childCount) {
            val chip = binding.chipGroupTags.getChildAt(i) as Chip
            tags.add(chip.text.toString())
        }
        return tags
    }

    private fun getSelectedLicenses(): List<License> {
        val licenses = mutableListOf<License>()

        // Get standard price
        val standardPriceText = binding.etStandardPrice.text.toString().trim()
        val standardPrice = standardPriceText.toDoubleOrNull() ?: 0.0

        // Get exclusive price
        val exclusivePriceText = binding.etExclusivePrice.text.toString().trim()
        val exclusivePrice = exclusivePriceText.toDoubleOrNull() ?: 0.0

        // Check which standard licenses are selected
        val standardFeatures = mutableListOf<String>()
        if (binding.cbStandard1.isChecked) standardFeatures.add("Use for custom only (one-time use)")
        if (binding.cbStandard2.isChecked) standardFeatures.add("Commercial use")
        if (binding.cbStandard3.isChecked) standardFeatures.add("Resale rights")
        if (binding.cbStandard4.isChecked) standardFeatures.add("Unlimited downloads")
        if (binding.cbStandard5.isChecked) standardFeatures.add("Print on demand")

        // Check which exclusive licenses are selected
        val exclusiveFeatures = mutableListOf<String>()
        if (binding.cbExclusive1.isChecked) exclusiveFeatures.add("Use for custom only (one-time use)")
        if (binding.cbExclusive2.isChecked) exclusiveFeatures.add("Commercial use")
        if (binding.cbExclusive3.isChecked) exclusiveFeatures.add("Resale rights")
        if (binding.cbExclusive4.isChecked) exclusiveFeatures.add("Unlimited downloads")
        if (binding.cbExclusive5.isChecked) exclusiveFeatures.add("Print on demand")

        // Add standard license if any features selected and price > 0
        if (standardFeatures.isNotEmpty() && standardPrice > 0) {
            licenses.add(
                License(
                    id = "standard",
                    type = "standard",
                    name = "Standard License",
                    description = "Standard usage rights",
                    features = standardFeatures,
                    price = standardPrice,
                    isDefault = true
                )
            )
        }

        // Add exclusive license if any features selected and price > 0
        if (exclusiveFeatures.isNotEmpty() && exclusivePrice > 0) {
            licenses.add(
                License(
                    id = "exclusive",
                    type = "exclusive",
                    name = "Exclusive License",
                    description = "Exclusive usage rights",
                    features = exclusiveFeatures,
                    price = exclusivePrice,
                    isDefault = false
                )
            )
        }

        return licenses
    }

    private fun updateDesign() {
        val title = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val tags = getSelectedTags()

        // Get selected licenses and calculate price range
        val licenses = getSelectedLicenses()
        val prices = licenses.map { it.price }.filter { it > 0 }
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 0.0

        val updatedDesign = currentDesign?.copy(
            title = title,
            description = description,
            category = category,
            tags = tags,
            licenses = licenses,
            minPrice = minPrice,
            maxPrice = maxPrice,
            fileUrl = selectedImageUri?.toString() ?: currentDesign?.fileUrl ?: "",
            thumbnailUrl = selectedImageUri?.toString() ?: currentDesign?.thumbnailUrl ?: ""
        )

        updatedDesign?.let { design ->
            viewModel.updateDesign(design)
        }
    }

    private fun showSuccessDialog() {
        successDialog(
            requireContext(),
            title = "Success!",
            message = "Design product has been updated successfully",
            positiveAction = {
                viewModel.resetEditDesignState()
                findNavController().navigateUp()
            }
        )
    }

    private fun showLoading() {
        // Show loading for data fetch - you could add a progress bar here
    }

    private fun hideLoading() {
        // Hide loading for data fetch
    }

    private fun showUpdateLoading() {
        binding.btnUpdate.text = "Updating..."
        binding.btnUpdate.isEnabled = false
    }

    private fun hideUpdateLoading() {
        binding.btnUpdate.text = "Update"
        binding.btnUpdate.isEnabled = true
    }

    private fun showError(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}