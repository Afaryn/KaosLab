package com.afaryn.kaoslab.ui_designer.account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.FragmentAccountDesignerBinding
import com.afaryn.kaoslab.utils.confirmDialog
import com.afaryn.kaoslab.utils.showBottomNavDesigner
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountDesignerFragment : Fragment() {

    private var _binding: FragmentAccountDesignerBinding? = null
    private val binding get() = _binding!!
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}