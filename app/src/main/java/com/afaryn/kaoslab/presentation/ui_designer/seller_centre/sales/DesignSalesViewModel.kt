package com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.domain.repository.DesignerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DesignSalesViewModel @Inject constructor(
    private val designerRepository: DesignerRepository
): ViewModel() {

    fun getDesigns(status: DesignOrderStatus) = designerRepository.getDesignSales(status)
}