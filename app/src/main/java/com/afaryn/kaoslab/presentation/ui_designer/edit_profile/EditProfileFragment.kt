package com.afaryn.kaoslab.presentation.ui_designer.edit_profile

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
import com.afaryn.kaoslab.databinding.FragmentEditProfileBinding
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavDesigner
import com.afaryn.kaoslab.utils.successDialog
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private var currentUser: User? = null
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
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupActions()

        // Load user profile
        viewModel.getUserProfile()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.userProfileState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        // Initial state
                    }
                    is Response.Loading -> {
                        showLoading()
                    }
                    is Response.Success -> {
                        hideLoading()
                        currentUser = state.data
                        populateUserData(state.data)
                    }
                    is Response.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateProfileState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        binding.btnUpdateProfile.text = "Update Profile"
                        binding.btnUpdateProfile.isEnabled = true
                    }
                    is Response.Loading -> {
                        binding.btnUpdateProfile.text = "Updating..."
                        binding.btnUpdateProfile.isEnabled = false
                    }
                    is Response.Success -> {
                        showSuccessDialog("Profile updated successfully") {
                            viewModel.resetUpdateProfileState()
                            findNavController().navigateUp()
                        }
                    }
                    is Response.Error -> {
                        binding.btnUpdateProfile.text = "Update Profile"
                        binding.btnUpdateProfile.isEnabled = true
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun setupActions() {
        binding.fabChangePhoto.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.ivProfilePicture.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun populateUserData(user: User) {
        binding.etFullName.setText(user.name)
        binding.etEmail.setText(user.email)
        binding.etPhone.setText(user.phone)

        // Load profile picture
        if (user.profilePicture.isNotEmpty()) {
            Glide.with(this)
                .load(user.profilePicture)
                .circleCrop()
                .into(binding.ivProfilePicture)
        }

        // Set role
        binding.tvUserRole.text = user.role.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // Format and display member since date
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.tvMemberSince.text = dateFormat.format(user.createdAt.toDate())
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
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Profile Picture"))
    }

    private fun showImagePreview(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivProfilePicture)
    }

    private fun updateProfile() {
        val name = binding.etFullName.text?.toString()?.trim()
        val phone = binding.etPhone.text?.toString()?.trim()

        when {
            name.isNullOrEmpty() -> {
                binding.etFullName.error = "Name is required"
                binding.etFullName.requestFocus()
                return
            }
            phone.isNullOrEmpty() -> {
                binding.etPhone.error = "Phone number is required"
                binding.etPhone.requestFocus()
                return
            }
        }

        currentUser?.let { user ->
            val updatedUser = user.copy(
                name = name,
                phone = phone
            )

            viewModel.updateProfile(updatedUser, selectedNewImageUri)
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
