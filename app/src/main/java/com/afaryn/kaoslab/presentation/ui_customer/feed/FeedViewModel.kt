package com.afaryn.kaoslab.presentation.ui_customer.feed

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getUserId() = userRepository.getUserId()
    fun getFeeds() = userRepository.getFeeds()
    fun likeFeed(feedId: String, isLiking: Boolean) = userRepository.likeFeed(feedId, isLiking)
}