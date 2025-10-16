package com.afaryn.kaoslab.presentation.ui_customer.account

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
import com.afaryn.kaoslab.presentation.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.FragmentAccountBinding
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.presentation.ui_customer.account.design.MyDesignActivity
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.OrdersActivity
import com.afaryn.kaoslab.presentation.ui_customer.address.AddressActivity
import com.afaryn.kaoslab.presentation.ui_designer.DesignerActivity
import com.afaryn.kaoslab.utils.Constants.DESIGNER
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.confirmDialog
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.showBottomNav
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        showBottomNav()
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUser()
        setupToolbar()
        setActions()
    }

    private fun observeUser() = lifecycleScope.launch {
        viewModel.user().collect { state ->
            when (state) {
                is Response.Success -> setupView(state.data)
                is Response.Error -> toast(state.message)
                else -> {}
            }
        }
    }

    private fun setupView(data: User) = binding.run {
        txtName.text = data.name?.replaceFirstChar { it.uppercaseChar() }

        data.profilePicture.takeIf { it.isNotEmpty() }?.let {
            imgProfile.glide(it)
        }

        btnBecomeDesigner.apply {
            text = if (data.role == DESIGNER) "Seller Centre" else "Become a Seller"
            setOnClickListener {
                if (data.role == DESIGNER)
                    startActivity(Intent(requireContext(), DesignerActivity::class.java))
            }
            show()
        }
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

    private fun setActions() = with(binding) {
        btnMyOrder.setOnClickListener {
            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }

        btnMyDesign.setOnClickListener {
            startActivity(Intent(requireContext(), MyDesignActivity::class.java))
        }

        btnAddress.setOnClickListener {
            startActivity(Intent(requireContext(), AddressActivity::class.java))
        }

        btnUserSecurity.setOnClickListener {
            findNavController().navigate(R.id.action_AccountFragment_to_editProfileFragment2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}