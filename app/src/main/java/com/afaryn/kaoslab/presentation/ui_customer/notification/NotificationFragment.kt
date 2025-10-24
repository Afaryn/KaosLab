package com.afaryn.kaoslab.presentation.ui_customer.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.NotificationAdapter
import com.afaryn.kaoslab.databinding.FragmentNotificationBinding
import com.afaryn.kaoslab.utils.isToday
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<NotificationViewModel>()
    private val todayAdapter by lazy { NotificationAdapter() }
    private val pastAdapter by lazy { NotificationAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        observeData()
        setActions()
    }

    private fun setActions() = binding.run {
        tvClearAll.setOnClickListener {
            vm.clearNotification()
        }
    }

    private fun setupRv() {
        binding.recyclerToday.apply {
            adapter = todayAdapter
            layoutManager = LinearLayoutManager(requireContext())
            todayAdapter.onItemDelete = {
                vm.deleteNotification(it.id)
            }
        }

        binding.recyclerYesterday.apply {
            adapter = pastAdapter
            layoutManager = LinearLayoutManager(requireContext())
            pastAdapter.onItemDelete = {
                vm.deleteNotification(it.id)
            }
        }
    }

    private fun observeData() = binding.run {
        lifecycleScope.launch {
            vm.notifications.collect { data ->
                tvNoData.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
                llNotifications.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE

                val todayData = data.filter { it.createdAt?.isToday() == true }
                val pastData = data.filter { it.createdAt?.isToday() == false }

                todayAdapter.differ.submitList(todayData)
                pastAdapter.differ.submitList(pastData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}