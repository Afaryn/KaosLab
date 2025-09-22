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
import androidx.navigation.fragment.navArgs
import com.afaryn.kaoslab.databinding.FragmentEditPortfolioBinding
import com.afaryn.kaoslab.model.Portfolio
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavDesigner
import com.afaryn.kaoslab.utils.successDialog
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditPortfolioFragment : Fragment() {

    private var _binding: FragmentEditPortfolioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PortfolioViewModel by viewModels()
    private val args: EditPortfolioFragmentArgs by navArgs()

    private var currentPortfolio: Portfolio? = null
    private var selectedNewImageUri: Uri? = null

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
                selectedNewImageUri = uri
                showImagePreview(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavDesigner()
        _binding = FragmentEditPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupActions()

        // Load portfolio details
        viewModel.getPortfolioById(args.portfolioId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.portfolioDetailState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        // Initial state
                    }
                    is Response.Loading -> {
                        showLoading()
                    }
                    is Response.Success -> {
                        hideLoading()
                        currentPortfolio = state.data
                        populatePortfolioData(state.data)
                    }
                    is Response.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updatePortfolioState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        binding.btnUpdate.text = "Update Portfolio"
                        binding.btnUpdate.isEnabled = true
                    }
                    is Response.Loading -> {
                        binding.btnUpdate.text = "Updating..."
                        binding.btnUpdate.isEnabled = false
                    }
                    is Response.Success -> {
                        showSuccessDialog("Portfolio updated successfully") {
                            viewModel.resetUpdatePortfolioState()
                            findNavController().navigateUp()
                        }
                    }
                    is Response.Error -> {
                        binding.btnUpdate.text = "Update Portfolio"
                        binding.btnUpdate.isEnabled = true
                        showError(state.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.deletePortfolioState.collect { state ->
                when (state) {
                    is Response.Success -> {
                        showSuccessDialog("Portfolio deleted successfully") {
                            viewModel.resetDeletePortfolioState()
                            findNavController().navigateUp()
                        }
                    }
                    is Response.Error -> {
                        showError(state.message)
                        viewModel.resetDeletePortfolioState()
                    }
                    else -> { /* No action needed */ }
                }
            }
        }
    }

    private fun setupActions() {
        binding.btnChangeImage.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.btnUpdate.setOnClickListener {
            updatePortfolio()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun populatePortfolioData(portfolio: Portfolio) {
        binding.etTitle.setText(portfolio.title)
        binding.etDescription.setText(portfolio.description)

        // Load current image
        if (portfolio.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(portfolio.imageUrl)
                .centerCrop()
                .into(binding.ivCurrentImage)
        }

        // Format and display created date
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvCreatedDate.text = dateFormat.format(portfolio.createdAt.toDate())
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
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.ivCurrentImage)
    }

    private fun updatePortfolio() {
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
        }

        currentPortfolio?.let { portfolio ->
            val updatedPortfolio = portfolio.copy(
                title = title,
                description = description
            )

            viewModel.updatePortfolio(updatedPortfolio, selectedNewImageUri)
        }
    }

    private fun showDeleteConfirmationDialog() {
        currentPortfolio?.let { portfolio ->
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Portfolio")
                .setMessage("Are you sure you want to delete \"${portfolio.title}\"? This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deletePortfolio(portfolio)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun showLoading() {
        // You can add a progress bar or loading indicator here
    }

    private fun hideLoading() {
        // Hide loading indicator
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccessDialog(message: String, onDismiss: () -> Unit) {
        successDialog(
            requireContext(),
            title = "Success!",
            message = message,
            positiveAction = onDismiss
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
