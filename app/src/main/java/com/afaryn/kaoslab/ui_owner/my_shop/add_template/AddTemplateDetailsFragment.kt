package com.afaryn.kaoslab.ui_owner.my_shop.add_template

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentAddTemplateDetailsBinding
import com.afaryn.kaoslab.databinding.ItemColorInputBinding
import com.afaryn.kaoslab.databinding.ItemSizeInputBinding
import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.SizeOption
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.successDialog
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTemplateDetailsFragment : Fragment() {

    private var _binding: FragmentAddTemplateDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTemplateViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    private val sizeInputs = mutableListOf<View>()
    private val colorInputs = mutableListOf<View>()
    private var selectedCategory: String = ""

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivPreview.setImageURI(uri)
                binding.ivPreview.visibility = View.VISIBLE
                binding.ivUploadIcon.visibility = View.GONE
                binding.tvUploadText.setText(R.string.image_selected)
                validateForm()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTemplateDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get selected category from arguments
        selectedCategory = arguments?.getString("selectedCategory") ?: ""

        setupClickListeners()
        setupObservers()
        addInitialSizeAndColor()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutImageUpload.setOnClickListener {
            openImagePicker()
        }

        binding.btnAddSize.setOnClickListener {
            addSizeInput()
        }

        binding.btnAddColor.setOnClickListener {
            addColorInput()
        }

        binding.btnUpload.setOnClickListener {
            uploadTemplate()
        }

        // Text change listeners for validation
        binding.etTemplateName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateForm() }
        })

        binding.etBasePrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateForm() }
        })
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uploadState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        binding.btnUpload.isEnabled = false
                        binding.btnUpload.setText(R.string.upload)
                    }
                    is Response.Loading -> {
                        binding.btnUpload.isEnabled = false
                        binding.btnUpload.setText(R.string.uploading)
                    }
                    is Response.Success -> {
                        binding.btnUpload.isEnabled = true
                        binding.btnUpload.setText(R.string.upload)
                        showSuccessDialog()
                    }
                    is Response.Error -> {
                        binding.btnUpload.isEnabled = true
                        binding.btnUpload.setText(R.string.upload)
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun addInitialSizeAndColor() {
        addSizeInput()
        addColorInput()
    }

    private fun addSizeInput() {
        val sizeBinding = ItemSizeInputBinding.inflate(layoutInflater)
        sizeInputs.add(sizeBinding.root)

        sizeBinding.btnRemoveSize.setOnClickListener {
            if (sizeInputs.size > 1) {
                binding.layoutSizes.removeView(sizeBinding.root)
                sizeInputs.remove(sizeBinding.root)
            }
        }

        sizeBinding.etSizeLabel.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateForm() }
        })

        sizeBinding.etAdditionalPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateForm() }
        })

        binding.layoutSizes.addView(sizeBinding.root)
    }

    private fun addColorInput() {
        val colorBinding = ItemColorInputBinding.inflate(layoutInflater)
        colorInputs.add(colorBinding.root)

        colorBinding.btnRemoveColor.setOnClickListener {
            if (colorInputs.size > 1) {
                binding.layoutColors.removeView(colorBinding.root)
                colorInputs.remove(colorBinding.root)
            }
        }

        // Set up text change listeners
        colorBinding.etColorHex.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
                updateColorPreview(colorBinding)
            }
        })

        // Set up color preview click to open color picker
        colorBinding.colorPreview.setOnClickListener {
            openColorPicker { selectedColor ->
                colorBinding.etColorHex.setText(selectedColor)
                updateColorPreview(colorBinding)
            }
        }

        binding.layoutColors.addView(colorBinding.root)
    }

    private fun updateColorPreview(colorBinding: ItemColorInputBinding) {
        val hexColor = colorBinding.etColorHex.text?.toString()?.trim()
        if (hexColor?.isNotEmpty() == true && isValidHexColor(hexColor)) {
            try {
                val color = android.graphics.Color.parseColor(hexColor)
                colorBinding.colorPreview.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(color)
            } catch (e: IllegalArgumentException) {
                // Invalid color format, keep default
                colorBinding.colorPreview.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            }
        }
    }

    private fun isValidHexColor(hex: String): Boolean {
        return hex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))
    }

    private fun openColorPicker(onColorSelected: (String) -> Unit) {
        val colors = arrayOf(
            "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
            "#FFFFFF", "#000000", "#808080", "#800000", "#008000", "#000080"
        )

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Color")
        builder.setItems(colors) { _, which ->
            onColorSelected(colors[which])
        }
        builder.show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun validateForm() {
        val hasImage = selectedImageUri != null
        val hasName = binding.etTemplateName.text?.isNotEmpty() == true
        val hasBasePrice = binding.etBasePrice.text?.isNotEmpty() == true
        val hasSizes = getSizes().isNotEmpty()
        val hasColors = getColors().isNotEmpty()

        binding.btnUpload.isEnabled = hasImage && hasName && hasBasePrice && hasSizes && hasColors
    }

    private fun getSizes(): List<SizeOption> {
        return sizeInputs.mapNotNull { view ->
            val sizeBinding = ItemSizeInputBinding.bind(view)
            val label = sizeBinding.etSizeLabel.text?.toString()?.trim()
            val priceText = sizeBinding.etAdditionalPrice.text?.toString()?.trim()

            if (label?.isNotEmpty() == true && priceText?.isNotEmpty() == true) {
                try {
                    SizeOption(label, priceText.toInt())
                } catch (e: NumberFormatException) {
                    null
                }
            } else null
        }
    }

    private fun getColors(): List<String> {
        return colorInputs.mapNotNull { view ->
            val colorBinding = ItemColorInputBinding.bind(view)
            val hexValue = colorBinding.etColorHex.text?.toString()?.trim()

            if (hexValue?.isNotEmpty() == true && isValidHexColor(hexValue)) {
                hexValue
            } else null
        }
    }

    private fun uploadTemplate() {
        val name = binding.etTemplateName.text?.toString()?.trim() ?: return
        val basePrice = binding.etBasePrice.text?.toString()?.trim()?.toIntOrNull() ?: return
        val sizes = getSizes()
        val colors = getColors()
        val maxPrice = basePrice + (sizes.maxOfOrNull { it.additionalPrice } ?: 0)

        val template = ProductTemplate(
            name = name,
            basePrice = basePrice,
            maxPrice = maxPrice,
            type = selectedCategory,
            sizes = sizes,
            colors = colors,
            createdAt = Timestamp.now()
        )

        selectedImageUri?.let { uri ->
            viewModel.uploadTemplate(template, uri)
        }
    }

    private fun showSuccessDialog() {
        successDialog(
            requireContext(),
            title = "Success!",
            message = "Product template has been added successfully",
            positiveAction = {
                findNavController().popBackStack()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
