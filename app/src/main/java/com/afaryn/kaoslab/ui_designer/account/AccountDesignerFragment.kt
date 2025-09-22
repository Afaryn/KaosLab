package com.afaryn.kaoslab.ui_designer.account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.FragmentAccountDesignerBinding
import com.afaryn.kaoslab.model.User
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.confirmDialog
import com.afaryn.kaoslab.utils.showBottomNavDesigner
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class AccountDesignerFragment : Fragment() {

    private var _binding: FragmentAccountDesignerBinding? = null
    private val binding get() = _binding!!

    private var currentUser: User? = null
    private val viewModel: AccountDesignerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        showBottomNavDesigner()
        // Inflate the layout for this fragment
        _binding = FragmentAccountDesignerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupActions()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.btnLogout.setOnClickListener {
            confirmDialog(
                requireContext(),
                title = "Logout confirmation",
                message = "Are you sure you want to logout?",
                positiveButton = "Yes",
                negativeButton = "No",
                positiveAction = {
                    viewModel.logout()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                },
            )
        }
    }

    private fun setupActions() {
        binding.btnSellerCentre.setOnClickListener {
            findNavController().navigate(R.id.action_accountDesignerFragment_to_sellerCentreFragment)
        }
        binding.btnUserSecurity.setOnClickListener {
            findNavController().navigate(R.id.action_accountDesignerFragment_to_editProfileFragment)
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
//                        showLoading()
                    }

                    is Response.Success -> {
//                        hideLoading()
                        currentUser = state.data
                        populateUserData(state.data)
                    }

                    is Response.Error -> {
//                        hideLoading()
//                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun populateUserData(user: User) {
        binding.txtName.setText(user.name)
        // Load profile picture
        if (user.profilePicture.isNotEmpty()) {
            Glide.with(this)
                .load(user.profilePicture)
                .circleCrop()
                .into(binding.imgProfile)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}