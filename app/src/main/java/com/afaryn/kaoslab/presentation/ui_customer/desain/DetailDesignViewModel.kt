package com.afaryn.kaoslab.presentation.ui_customer.desain

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailDesignViewModel @Inject constructor(
    private val userRepo: UserRepository
): ViewModel() {

    fun checkFavorite(designId: String) = userRepo.checkFavorite(designId)
    fun modifyFavorite(design: Design) = userRepo.modifyFavorite(design)
}