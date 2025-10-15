package com.afaryn.kaoslab.presentation.ui_customer.account.design

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.DesignAdapter
import com.afaryn.kaoslab.databinding.FragmentPendingPaymentBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.collections.orEmpty

@AndroidEntryPoint
abstract class MyDesignBaseFragment : Fragment() {

    abstract val isPending: Boolean
    private var _binding: FragmentPendingPaymentBinding? = null
    private val binding get() = _binding!!
    protected val vm: MyDesignViewModel by activityViewModels()
    private val designAdapter by lazy { DesignAdapter(isPending) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingPaymentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        observeData()
    }

    private fun setupRv() = binding.rvMyDesign.run {
        adapter = designAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() = lifecycleScope.launch {
        vm.getDesigns(isPending).collect {
            when (it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> setupView(it.data.orEmpty())
                else -> {}
            }
        }
    }

    private fun setupView(data: List<Design>) {
        binding.tvNoData.isVisible = data.isEmpty()
        designAdapter.differ.submitList(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}