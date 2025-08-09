package com.afaryn.kaoslab.ui_owner.my_shop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.FragmentMyShopOwnerBinding
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
        // Inflate the layout for this fragment
        _binding = FragmentMyShopOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
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
    }

}