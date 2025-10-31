package com.afaryn.kaoslab.presentation.ui_owner.shop_performance.month_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemCardLastorderBinding
import com.afaryn.kaoslab.domain.model.Order
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class MonthOrderAdapter : ListAdapter<Order, MonthOrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemCardLastorderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        private val binding: ItemCardLastorderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                // Order ID
                tvOrderId.text = "Order #${order.orderId.take(8).uppercase()}"

                // Order Date
                order.createdAt?.let { timestamp ->
                    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    tvOrderDate.text = dateFormat.format(timestamp.toDate())
                }

                // Product Image
                if (order.designImageUrl.isNotEmpty()) {
                    Glide.with(productImage.context)
                        .load(order.designImageUrl)
                        .into(productImage)
                }

                // Design overlay if available
                val firstCartProduct = order.cartProducts.firstOrNull()
                firstCartProduct?.orderItem?.designType?.let { designType ->
                    when (designType.type) {
                        "image_upload", "image_your_design" -> {
                            designOverlay.visibility = View.VISIBLE
                            tvOverlay.visibility = View.GONE
                            designType.overlay?.let { overlayUrl ->
                                Glide.with(designOverlay.context)
                                    .load(overlayUrl)
                                    .into(designOverlay)
                            }
                        }
                        "text" -> {
                            designOverlay.visibility = View.GONE
                            tvOverlay.visibility = View.VISIBLE
                            tvOverlay.text = designType.text ?: ""
                        }
                        else -> {
                            designOverlay.visibility = View.GONE
                            tvOverlay.visibility = View.GONE
                        }
                    }
                } ?: run {
                    designOverlay.visibility = View.GONE
                    tvOverlay.visibility = View.GONE
                }

                // Order Title
                tvOrderTitle.text = order.title.ifEmpty { "Custom Product" }

                // Order Size
                val size = firstCartProduct?.orderItem?.size ?: ""
                tvOrderSize.text = if (size.isNotEmpty()) "Size: $size" else ""

                // Total Amount
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvTotalAmount.text = formatter.format(order.totalAmount)

                // Total Pieces
                tvTotalPieces.text = "x${order.totalPieces}"
            }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}

