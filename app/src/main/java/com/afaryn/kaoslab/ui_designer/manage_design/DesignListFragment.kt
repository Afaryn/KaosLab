package com.afaryn.kaoslab.ui_designer.manage_design

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentDesignListBinding
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.ui_designer.manage_design.adapter.DesignAdapter
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavDesigner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DesignListFragment : Fragment() {

    private var _binding: FragmentDesignListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DesignViewModel by viewModels()
    private lateinit var designAdapter: DesignAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavDesigner()
        _binding = FragmentDesignListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupActions()

        viewModel.getDesigns()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        designAdapter = DesignAdapter(
            onItemClick = { design ->
                // Navigate to design detail or edit
            },
            onMoreClick = { design, view ->
                // Show more options menu
                showMoreOptionsMenu(design, view)
            }
        )

        binding.rvDesigns.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = designAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.designsState.collect { state ->
                when (state) {
                    is Response.Idle -> {
                        hideLoading()
                    }
                    is Response.Loading -> {
                        showLoading()
                    }
                    is Response.Success -> {
                        hideLoading()
                        handleDesignsSuccess(state.data)
                    }
                    is Response.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun setupActions() {
        binding.btnAddNewDesign.setOnClickListener {
            findNavController().navigate(R.id.action_designListFragment_to_addDesignFragment)
        }

        // Fix search functionality with TextInputEditText
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                designAdapter.filter(s?.toString() ?: "")
            }
        })
    }

    private fun handleDesignsSuccess(designs: List<Design>) {
        if (designs.isEmpty()) {
            showEmptyState()
        } else {
            showDesigns(designs)
        }
    }

    private fun showDesigns(designs: List<Design>) {
        binding.rvDesigns.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        designAdapter.submitList(designs)
    }

    private fun showEmptyState() {
        binding.rvDesigns.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvDesigns.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        // Show error message using Snackbar or Toast
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showMoreOptionsMenu(design: Design, anchorView: View) {
        // Show popup menu with edit/delete options anchored to the specific item
        val popup = androidx.appcompat.widget.PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.menu_design_options, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    // Navigate to edit design
                    val action = DesignListFragmentDirections.actionDesignListFragmentToDetailEditDesignFragment(design.id)
                    findNavController().navigate(action)
                    true
                }
                R.id.action_delete -> {
                    // Show delete confirmation
                    showDeleteConfirmation(design)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showDeleteConfirmation(design: Design) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Design")
            .setMessage("Are you sure you want to delete this design?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteDesign(design.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
