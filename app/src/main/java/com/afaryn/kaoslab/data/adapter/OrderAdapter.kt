package com.afaryn.kaoslab.data.adapter

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemOrderBinding
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.OrderStatus
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.show

class OrderAdapter: RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val diffUtil = object: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ= AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder =
        OrderViewHolder(ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {
        val item = differ.currentList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class OrderViewHolder(val binding: ItemOrderBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(order: Order, position: Int) = binding.run {
            if (order.status == OrderStatus.Shipped.value) {
                tvDeliveryMsg.show()
                icTruck.show()
            }

            val product = order.cartProducts.first()

            product.orderItem?.designType?.overlay?.let {
                designOverlay.glide(it)
            }

            product.orderItem?.designType?.product?.imageUrl?.let {
                imageCartProduct.glide(it)
            }

            product.orderItem?.designType?.text?.let {
                tvOverlay.text = it
            }

            tvSize.text = "Size: ${product.orderItem?.size}"
            tvColor.text = product.selectedColor

            val totalText = "Total: ${product.totalAmount.toInt().formatRupiah()}"
            val spannable = SpannableString(totalText)

            val start = totalText.indexOf(":") + 2
            val end = totalText.length

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.chocoTan)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            tvTotal.text = spannable

            btnContactSeller.text = if (order.status == OrderStatus.Pending.value) "Update Status" else "Contact Seller"
            btnContactSeller.setOnClickListener {
                if (order.status == OrderStatus.Pending.value) onUpdateStatus?.invoke(order, position)
                else onContactSeller?.invoke(order)
            }
        }
    }

    var onUpdateStatus: ((Order, Int) -> Unit)? = null
    var onContactSeller: ((Order) -> Unit)? = null
}