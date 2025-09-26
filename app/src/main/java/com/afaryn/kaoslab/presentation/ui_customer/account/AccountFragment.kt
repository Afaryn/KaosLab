package com.afaryn.kaoslab.presentation.ui_customer.account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.afaryn.kaoslab.presentation.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.FragmentAccountBinding
import com.afaryn.kaoslab.utils.confirmDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
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
}