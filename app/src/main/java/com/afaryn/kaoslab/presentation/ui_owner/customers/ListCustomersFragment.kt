package com.afaryn.kaoslab.presentation.ui_owner.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentListCustomersBinding
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCustomersFragment : Fragment() {

    private var _binding: FragmentListCustomersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListCustomersViewModel by viewModels()
    private lateinit var customersAdapter: CustomersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavOwner()
        _binding = FragmentListCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        observeCustomers()
        setupRetryButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        customersAdapter = CustomersAdapter()
        binding.rvCustomers.apply {
            adapter = customersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchCustomers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchCustomers(it) }
                return true
            }
        })
    }

    private fun observeCustomers() {
        viewModel.customers.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    showLoading(true)
                    showError(false)
                    showEmptyState(false)
                }
                is Response.Success -> {
                    showLoading(false)
                    showError(false)

                    if (response.data.isEmpty()) {
                        showEmptyState(true)
                        customersAdapter.submitList(emptyList())
                    } else {
                        showEmptyState(false)
                        customersAdapter.submitList(response.data)
                    }
                }
                is Response.Error -> {
                    showLoading(false)
                    showError(true, response.message)
                    showEmptyState(false)
                    customersAdapter.submitList(emptyList())
                }
                else -> {}
            }
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.refreshCustomers()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvCustomers.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean, message: String? = null) {
        binding.layoutErrorState.visibility = if (isError) View.VISIBLE else View.GONE
        if (isError && message != null) {
            binding.tvErrorMessage.text = message
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
