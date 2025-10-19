package com.afaryn.kaoslab.presentation.ui_customer.feed.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.Feed
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

//    fun postFeed(feed: Feed, imgUri: Uri) = userRepository.postFeed(feed, imgUri)
}