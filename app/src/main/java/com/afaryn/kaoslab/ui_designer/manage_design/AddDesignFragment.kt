package com.afaryn.kaoslab.ui_designer.manage_design

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.databinding.FragmentAddDesignBinding
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.model.License
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavDesigner
import com.afaryn.kaoslab.utils.successDialog
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.google.android.material.chip.Chip

@AndroidEntryPoint
class AddDesignFragment : Fragment() {

    private var _binding: FragmentAddDesignBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DesignViewModel by viewModels()

    private var selectedImageUri: Uri? = null
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
        _binding = FragmentAddDesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCategoryDropdown()
        setupObservers()
        setupActions()
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
            viewModel.addDesignState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        hideLoading()
                    }
                    is Response.Loading -> {
                        showLoading()
                    }
                    is Response.Success -> {
                        hideLoading()
                        showSuccessDialog()
                    }
                    is Response.Error -> {
                        hideLoading()
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

        binding.btnDone.setOnClickListener {
            if (validateForm()) {
                submitDesign()
            }
        }

        setupTagsInput()
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

    private fun getSelectedTags(): List<String> {
        val tags = mutableListOf<String>()
        for (i in 0 until binding.chipGroupTags.childCount) {
            val chip = binding.chipGroupTags.getChildAt(i) as Chip
            tags.add(chip.text.toString())
        }
        return tags
    }

    private fun openImagePicker() {
        try {
            // For modern Android versions (API 33+), we can use the photo picker directly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ - Use the new photo picker which doesn't require permissions
                launchImagePicker()
            } else {
                // Android 12 and below - Check for storage permission
                val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                    launchImagePicker()
                } else {
                    permissionLauncher.launch(permission)
                }
            }
        } catch (e: Exception) {
            // Fallback: try to launch image picker directly
            android.util.Log.e("AddDesignFragment", "Error in openImagePicker", e)
            launchImagePicker()
        }
    }

    private fun launchImagePicker() {
        try {
            // Try multiple approaches for maximum compatibility
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+, use the new photo picker
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/webp"))
                }
            } else {
                // For older Android versions
                Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
            }

            // Add fallback intent
            val fallbackIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            val chooser = Intent.createChooser(fallbackIntent, "Select Design File")
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intent))

            imagePickerLauncher.launch(chooser)

        } catch (e: Exception) {
            android.util.Log.e("AddDesignFragment", "Error launching image picker", e)
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
        // For now, show a simple message that custom licenses will be added later
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
            selectedImageUri == null -> {
                showError("Please select a design file")
                return false
            }
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

    private fun submitDesign() {
        val title = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val tags = getSelectedTags()

        // Get selected licenses and calculate price range
        val licenses = getSelectedLicenses()
        val prices = licenses.map { it.price }.filter { it > 0 }
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 0.0

        val design = Design(
            title = title,
            description = description,
            category = category,
            tags = tags,
            fileUrl = selectedImageUri.toString(), // In real app, upload to Firebase Storage
            thumbnailUrl = selectedImageUri.toString(),
            licenses = licenses,
            minPrice = minPrice,
            maxPrice = maxPrice
        )

        viewModel.addDesign(design)
    }

    private fun showSuccessDialog() {
        successDialog(
            requireContext(),
            title = "Success!",
            message = "Design product has been added successfully",
            positiveAction = {
                viewModel.resetAddDesignState()
                findNavController().navigateUp()
            }
        )
    }

    private fun showLoading() {
        binding.btnDone.text = "Uploading..."
        binding.btnDone.isEnabled = false
    }

    private fun hideLoading() {
        binding.btnDone.text = "Done"
        binding.btnDone.isEnabled = true
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
