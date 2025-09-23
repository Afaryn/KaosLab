package com.afaryn.kaoslab.ui_owner.sales.my_sales

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemSalesBinding
import com.afaryn.kaoslab.model.Order
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class SalesAdapter(
    private val onArrangeShipment: (Order) -> Unit,
    private val onSeeDetails: (Order) -> Unit,
    private val onContactCustomer: (Order, String) -> Unit
) : ListAdapter<Order, SalesAdapter.SalesViewHolder>(SalesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val binding = ItemSalesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SalesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SalesViewHolder(
        private val binding: ItemSalesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                // User info
                userName.text = order.customerName
                Glide.with(itemView.context)
                    .load(order.customerAvatarUrl)
                    .into(userAvatar)

                // Status tag
                salesStatusTag.text = getStatusDisplayText(order.status)

                // Product info
                productName.text = order.title
                productDetails.text = "Size: ${order.size}"
                productQuantity.text = "x${order.totalPieces}"

                Glide.with(itemView.context)
                    .load(order.designImageUrl)
                    .into(productImage)

                // Total amount
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                totalAmount.text = formatter.format(order.totalAmount)

                // Courier info section - show only for to_deliver and shipping status
                if (order.status == "processing" || order.status == "shipped") {
                    courierInfoSection.visibility = View.VISIBLE
                    if (order.courierInfo != null && order.courierLogo != null) {
                        Glide.with(itemView.context)
                            .load(order.courierLogo)
                            .into(courierLogo)
                    }
                } else {
                    courierInfoSection.visibility = View.GONE
                }

                // Setup buttons based on status
                setupButtonsForStatus(order)
            }
        }

        private fun getStatusDisplayText(status: String): String {
            return when (status) {
                "pending" -> "Unpaid"
                "processing" -> "To Deliver"
                "shipped" -> "Shipping"
                "delivered" -> "Completed"
                else -> status.capitalize()
            }
        }

        private fun setupButtonsForStatus(order: Order) {
            binding.apply {
                when (order.status) {
                    "pending" -> {
                        primaryButton.text = "Contact Customer"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.GONE

                        primaryButton.setOnClickListener {
                            onContactCustomer(order, "")
                        }
                    }
                    "processing" -> {
                        primaryButton.text = "Arrange Shipment"
                        secondaryButton.text = "See Details"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.VISIBLE

                        primaryButton.setOnClickListener {
                            onArrangeShipment(order)
                        }

                        secondaryButton.setOnClickListener {
                            onSeeDetails(order)
                        }
                    }
                    "shipped" -> {
                        primaryButton.text = "See Details"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.GONE

                        primaryButton.setOnClickListener {
                            onSeeDetails(order)
                        }
                    }
                    "delivered" -> {
                        primaryButton.text = "See Details"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.GONE

                        primaryButton.setOnClickListener {
                            onSeeDetails(order)
                        }
                    }
                }
            }
        }
    }

    private class SalesDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
