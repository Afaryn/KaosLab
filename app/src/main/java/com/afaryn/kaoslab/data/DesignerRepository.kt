package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.BusinessInsights
import com.afaryn.kaoslab.model.ChartData
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.model.OrderStatusCounts
import com.afaryn.kaoslab.model.Transaction
import com.afaryn.kaoslab.model.TransactionFilter
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.utils.Response
import kotlinx.coroutines.flow.Flow

interface DesignerRepository {
    // Design Management methods
    fun getDesigns(): Flow<Response<List<Design>>>
    fun addDesign(design: Design): Flow<Response<String>>
    fun updateDesign(design: Design): Flow<Response<String>>
    fun deleteDesign(designId: String): Flow<Response<String>>
    fun getDesignById(designId: String): Flow<Response<Design>>
}
