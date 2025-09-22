package com.afaryn.kaoslab.ui_designer.manage_portofolio

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.databinding.FragmentAddPortfolioBinding
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavDesigner
import com.afaryn.kaoslab.utils.successDialog
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPortfolioFragment : Fragment() {

    private var _binding: FragmentAddPortfolioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PortfolioViewModel by viewModels()

    private var selectedImageUri: Uri? = null

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
        _binding = FragmentAddPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupActions()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.addPortfolioState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        // Reset UI to normal state
                        binding.btnUpload.text = "Upload"
                        binding.btnUpload.isEnabled = true
                    }
                    is Response.Loading -> {
                        binding.btnUpload.text = "Uploading..."
                        binding.btnUpload.isEnabled = false
                    }
                    is Response.Success -> {
                        showSuccessDialog()
                    }
                    is Response.Error -> {
                        binding.btnUpload.text = "Upload"
                        binding.btnUpload.isEnabled = true
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun setupActions() {
        // Image upload click
        binding.layoutImagePlaceholder.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.ivSelectedImage.setOnClickListener {
            checkPermissionAndPickImage()
        }

        // Remove image
        binding.btnRemoveImage.setOnClickListener {
            removeSelectedImage()
        }

        // Upload button
        binding.btnUpload.setOnClickListener {
            validateAndUpload()
        }
    }

    private fun checkPermissionAndPickImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    launchImagePicker()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    launchImagePicker()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"))
    }

    private fun showImagePreview(uri: Uri) {
        binding.layoutImagePlaceholder.visibility = View.GONE
        binding.ivSelectedImage.visibility = View.VISIBLE
        binding.btnRemoveImage.visibility = View.VISIBLE

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.ivSelectedImage)
    }

    private fun removeSelectedImage() {
        selectedImageUri = null
        binding.layoutImagePlaceholder.visibility = View.VISIBLE
        binding.ivSelectedImage.visibility = View.GONE
        binding.btnRemoveImage.visibility = View.GONE
    }

    private fun validateAndUpload() {
        val title = binding.etTitle.text?.toString()?.trim()
        val description = binding.etDescription.text?.toString()?.trim()

        when {
            title.isNullOrEmpty() -> {
                binding.etTitle.error = "Title is required"
                binding.etTitle.requestFocus()
                return
            }
            description.isNullOrEmpty() -> {
                binding.etDescription.error = "Description is required"
                binding.etDescription.requestFocus()
                return
            }
            selectedImageUri == null -> {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Clear any previous errors
        binding.etTitle.error = null
        binding.etDescription.error = null

        viewModel.addPortfolio(title, description, selectedImageUri)
    }

    private fun showSuccessDialog() {
        successDialog(
            requireContext(),
            title = "Success!",
            message = "Portfolio has been added successfully",
            positiveAction = {
                viewModel.resetAddPortfolioState()
                findNavController().navigateUp()
            }
        )
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
