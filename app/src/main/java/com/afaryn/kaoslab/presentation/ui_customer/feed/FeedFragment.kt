package com.afaryn.kaoslab.presentation.ui_customer.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.data.adapter.FeedAdapter
import com.afaryn.kaoslab.databinding.FragmentFeedBinding
import com.afaryn.kaoslab.domain.model.Portfolio
import com.afaryn.kaoslab.presentation.ui_customer.feed.post.PostFeedActivity
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<FeedViewModel>()
    private val feedAdapter by lazy { FeedAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        observeFeeds()
        setActions()
    }

    private fun setActions() = with(binding) {
        btnPostFeed.setOnClickListener {
            startActivity(Intent(requireContext(), PostFeedActivity::class.java))
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                observeFeeds()
                return@doOnTextChanged
            }

            val filteredData = feedAdapter.differ.currentList.filter {
                it.description.contains(
                    text.toString(),
                    ignoreCase = true
                ) || it.user?.name?.contains(text.toString(), ignoreCase = true) == true
            }

            tvNoData.isVisible = filteredData.isEmpty()
            feedAdapter.differ.submitList(filteredData)
        }
    }

    private fun setupRv() = binding.rvFeed.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = feedAdapter.also {
            it.setUserId(vm.getUserId())
        }
        feedAdapter.onLike = { feed, isLiking ->
            likeFeed(feed.id, isLiking)
        }
    }

    private fun likeFeed(feedId: String, liking: Boolean) = lifecycleScope.launch {
        vm.likeFeed(feedId, liking).collect {
            when (it) {
                is Resource.Error -> toast(it.error)
                else -> {}
            }
        }
    }

    private fun observeFeeds() = lifecycleScope.launch {
        vm.getFeeds().collect {
            when (it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> setupView(it.data.orEmpty())
                else -> {}
            }
        }
    }

    private fun setupView(feeds: List<Portfolio>) = binding.run {
        try {
            tvNoData.isVisible = feeds.isEmpty()
            feedAdapter.differ.submitList(feeds)
        } catch (_: Exception) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}