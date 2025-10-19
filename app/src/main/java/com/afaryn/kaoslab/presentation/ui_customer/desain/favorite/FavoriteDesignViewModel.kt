package com.afaryn.kaoslab.presentation.ui_customer.desain.favorite

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteDesignViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val favoriteDesigns = userRepository.getFavorites()
}