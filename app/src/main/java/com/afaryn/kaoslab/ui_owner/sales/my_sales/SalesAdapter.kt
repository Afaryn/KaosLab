package com.afaryn.kaoslab.ui_owner.sales.my_sales

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
import java.util.Locale

class SalesAdapter(
    private val onPrimaryButtonClick: (Order) -> Unit,
    private val onSecondaryButtonClick: (Order) -> Unit = {}
) : ListAdapter<Order, SalesAdapter.SalesViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val binding = ItemSalesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
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
            with(binding) {
                // Set customer info
                userName.text = order.customerName

                // Load customer avatar
                if (order.customerAvatarUrl.isNotEmpty()) {
                    Glide.with(userAvatar.context)
                        .load(order.customerAvatarUrl)
                        .into(userAvatar)
                }

                // Set status tag
                salesStatusTag.text = when (order.status) {
                    "unpaid" -> "Unpaid"
                    "to_deliver" -> "To Deliver"
                    "shipping" -> "Shipping"
                    "completed" -> "Completed"
                    else -> order.status
                }

                // Set product info
                productName.text = order.title
                productQuantity.text = "x${order.totalPieces}"
                productDetails.text = "Size: ${order.size}"

                // Load product image
                if (order.designImageUrl.isNotEmpty()) {
                    Glide.with(productImage.context)
                        .load(order.designImageUrl)
                        .into(productImage)
                }

                // Set total amount
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                totalAmount.text = formatter.format(order.totalAmount)

                // Configure courier info based on status
                when (order.status) {
                    "to_deliver", "shipping" -> {
                        courierInfoSection.visibility = View.VISIBLE
                        order.courierInfo?.let { courierName.text = it }

                        // Load courier logo if available
                        order.courierLogo?.let { logoUrl ->
                            if (logoUrl.isNotEmpty()) {
                                Glide.with(courierLogo.context)
                                    .load(logoUrl)
                                    .into(courierLogo)
                            }
                        }
                    }
                    else -> {
                        courierInfoSection.visibility = View.GONE
                    }
                }

                // Configure buttons based on status
                configureButtons(order)
            }
        }

        private fun configureButtons(order: Order) {
            with(binding) {
                when (order.status) {
                    "unpaid" -> {
                        primaryButton.text = "Contact Customer"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.GONE

                        primaryButton.setOnClickListener {
                            onPrimaryButtonClick(order)
                        }
                    }

                    "to_deliver" -> {
                        primaryButton.text = "Arrange Shipment"
                        secondaryButton.text = "See Details"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.VISIBLE

                        primaryButton.setOnClickListener {
                            onPrimaryButtonClick(order)
                        }

                        secondaryButton.setOnClickListener {
                            onSecondaryButtonClick(order)
                        }
                    }

                    "shipping" -> {
                        primaryButton.text = "See Details"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.GONE

                        primaryButton.setOnClickListener {
                            onPrimaryButtonClick(order)
                        }
                    }

                    "completed" -> {
                        primaryButton.text = "Beri Review"
                        primaryButton.visibility = View.VISIBLE
                        secondaryButton.visibility = View.GONE

                        primaryButton.setOnClickListener {
                            onPrimaryButtonClick(order)
                        }
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
