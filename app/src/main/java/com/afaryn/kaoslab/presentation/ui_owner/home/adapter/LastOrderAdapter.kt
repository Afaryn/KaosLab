package com.afaryn.kaoslab.presentation.ui_owner.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemCardLastorderBinding
import com.afaryn.kaoslab.domain.model.Order
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class LastOrderAdapter(
    private val onItemClick: (Order) -> Unit
) : ListAdapter<Order, LastOrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemCardLastorderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class OrderViewHolder(
        private val binding: ItemCardLastorderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            binding.apply {
                // Set order data
                tvOrderId.text = "Order #${order.orderId.take(8)}"
                tvOrderTitle.text = order.title
                tvOrderSize.text = "Size: ${order.cartProducts.joinToString { it.orderItem?.size.toString() }}"
                tvTotalPieces.text = "${order.totalPieces} pcs"
                tvTotalAmount.text = currencyFormat.format(order.totalAmount).replace("IDR", "Rp")

                // Format date
                order.createdAt?.let { timestamp ->
                    tvOrderDate.text = dateFormat.format(timestamp.toDate())
                } ?: run {
                    tvOrderDate.text = "No date"
                }

                // Load design image
                if (order.designImageUrl.isNotEmpty()) {
                    Glide.with(binding.root.context)
                        .load(order.designImageUrl)
                        .centerCrop()
                        .into(ivDesignImage)
                } else {
                    // Set placeholder image
                    ivDesignImage.setImageResource(com.afaryn.kaoslab.R.drawable.ic_image_placeholder)
                }

//                // Set status styling
//                tvOrderStatus.text = order.status.replaceFirstChar {
//                    if (it.isLowerCase()) it.titlecase() else it.toString()
//                }

                // Set status color based on order status
                val statusColor = when (order.status.lowercase()) {
                    "pending" -> android.graphics.Color.parseColor("#FFA500")
                    "processing", "paid" -> android.graphics.Color.parseColor("#2196F3")
                    "shipped" -> android.graphics.Color.parseColor("#9C27B0")
                    "delivered", "completed" -> android.graphics.Color.parseColor("#4CAF50")
                    "cancelled" -> android.graphics.Color.parseColor("#F44336")
                    "returned" -> android.graphics.Color.parseColor("#FF5722")
                    else -> android.graphics.Color.parseColor("#757575")
                }
//                tvOrderStatus.setTextColor(statusColor)

                // Set click listener
                root.setOnClickListener {
                    onItemClick(order)
                }
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
