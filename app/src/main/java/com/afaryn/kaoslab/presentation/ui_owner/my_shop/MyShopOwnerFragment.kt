package com.afaryn.kaoslab.presentation.ui_owner.my_shop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.presentation.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.FragmentMyShopOwnerBinding
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.showBottomNavOwner
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyShopOwnerFragment : Fragment() {
    private var _binding: FragmentMyShopOwnerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyShopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        showBottomNavOwner()
        // Inflate the layout for this fragment
        _binding = FragmentMyShopOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observers()
    }

    private fun setupToolbar() {
        binding.btnLogout.setOnClickListener {
            viewModel.logOut()
            startActivity(
                Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

        // Add navigation to Product Template
        binding.btnProductTemplate.setOnClickListener {
            findNavController().navigate(R.id.action_myShopOwnerFragment_to_productTemplateFragment)
        }

        binding.btnDelivery.setOnClickListener {
            findNavController().navigate(R.id.action_myShopOwnerFragment_to_ekspedisiFragment)
        }

        binding.btnCustomer.setOnClickListener {
            findNavController().navigate(R.id.action_myShopOwnerFragment_to_listCustomersFragment)
        }

        binding.btnShopPerformance.setOnClickListener {
            findNavController().navigate(R.id.action_myShopOwnerFragment_to_shopPerformanceFragment)
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_myShopOwnerFragment_to_editProfileOwnerFragment2)
        }
    }

    private fun observers() {
        viewModel.getCurrentUser().observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Response.Loading -> {
                    // Show loading state if needed
                }

                is Response.Success -> {
                    val user = resource.data
                    binding.txtName.text = user.name
                    if (user.profilePicture.isNotEmpty()) {
                        Glide.with(this)
                            .load(user.profilePicture)
                            .circleCrop()
                            .into(binding.imgProfile)
                    }
                }

                is Response.Error -> {
                    // Handle error state if needed
                }

                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}