package com.afaryn.kaoslab.presentation.ui_designer.seller_centre

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.domain.repository.AuthRepository
import com.afaryn.kaoslab.domain.repository.DesignerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SellerCentreViewModel @Inject constructor(
    designerRepository: DesignerRepository,
    authRepository: AuthRepository
) : ViewModel() {

    val user = authRepository.getCurrentUser()
    val sales = designerRepository.getDesignSales(DesignOrderStatus.Owned)
    val designs = designerRepository.getDesigns()
}