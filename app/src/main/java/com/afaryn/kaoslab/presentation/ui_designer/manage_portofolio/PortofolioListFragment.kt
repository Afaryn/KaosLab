package com.afaryn.kaoslab.presentation.ui_designer.manage_portofolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentPortfolioListBinding
import com.afaryn.kaoslab.domain.model.Portfolio
import com.afaryn.kaoslab.presentation.ui_designer.manage_portofolio.adapter.PortfolioAdapter
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PortfolioListFragment : Fragment() {

    private var _binding: FragmentPortfolioListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PortfolioViewModel by viewModels()
    private lateinit var portfolioAdapter: PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNav()
        _binding = FragmentPortfolioListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupActions()

        viewModel.getPortfolios()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        portfolioAdapter = PortfolioAdapter(
            onItemClick = { portfolio ->
                // Navigate to edit portfolio screen
                val action = PortfolioListFragmentDirections.actionPortfolioListFragmentToEditPortfolioFragment(portfolio.id)
                findNavController().navigate(action)
            },
            onMoreClick = { portfolio, view ->
                showMoreOptionsMenu(portfolio, view)
            }
        )

        binding.rvPortfolios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = portfolioAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.portfoliosState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        hideLoading()
                    }
                    is Response.Loading -> {
                        showLoading()
                    }
                    is Response.Success -> {
                        hideLoading()
                        handlePortfoliosSuccess(state.data)
                    }
                    is Response.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.deletePortfolioState.collect { state ->
                when (state) {
                    is Response.Success -> {
                        showSuccess(state.data)
                        viewModel.getPortfolios() // Refresh list
                        viewModel.resetDeletePortfolioState()
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
        binding.btnAddNewPortfolio.setOnClickListener {
            findNavController().navigate(R.id.action_portfolioListFragment_to_addPortfolioFragment)
        }

        binding.btnAddFirstPortfolio.setOnClickListener {
            findNavController().navigate(R.id.action_portfolioListFragment_to_addPortfolioFragment)
        }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                portfolioAdapter.filter(s?.toString() ?: "")
            }
        })
    }

    private fun handlePortfoliosSuccess(portfolios: List<Portfolio>) {
        if (portfolios.isEmpty()) {
            showEmptyState()
        } else {
            showPortfolios(portfolios)
        }
    }

    private fun showPortfolios(portfolios: List<Portfolio>) {
        binding.rvPortfolios.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        portfolioAdapter.submitList(portfolios)
    }

    private fun showEmptyState() {
        binding.rvPortfolios.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvPortfolios.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showSuccess(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showMoreOptionsMenu(portfolio: Portfolio, anchorView: View) {
        val popup = androidx.appcompat.widget.PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.menu_portfolio_options, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // Navigate to edit portfolio
                    val action = PortfolioListFragmentDirections.actionPortfolioListFragmentToEditPortfolioFragment(portfolio.id)
                    findNavController().navigate(action)
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmationDialog(portfolio)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun showDeleteConfirmationDialog(portfolio: Portfolio) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Portfolio")
            .setMessage("Are you sure you want to delete \"${portfolio.title}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePortfolio(portfolio) // Pass the complete portfolio object
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}