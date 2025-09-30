package com.afaryn.kaoslab.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemCartCoBinding
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.orZero

class CheckoutAdapter(): RecyclerView.Adapter<CheckoutAdapter.CartViewHolder>() {

    private val diffUtil = object: DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(
            oldItem: CartProduct,
            newItem: CartProduct
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CartProduct,
            newItem: CartProduct
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder =
        CartViewHolder(
            ItemCartCoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class CartViewHolder(val binding: ItemCartCoBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cartProduct: CartProduct) = binding.run {
            cartProduct.orderItem?.designType?.overlay?.let {
                designOverlay.glide(it)
            }

            cartProduct.orderItem?.designType?.product?.imageUrl?.let {
                imageCartProduct.glide(it)
            }

            cartProduct.orderItem?.designType?.text?.let {
                tvOverlay.text = it
            }

            val size = cartProduct.orderItem?.designType?.product?.sizes?.find { it.label == cartProduct.orderItem.size }?.additionalPrice
            val productPrice = cartProduct.orderItem?.designType?.product?.basePrice

            tvProductName.text = cartProduct.orderItem?.designType?.product?.name
            tvColor.text = "#${cartProduct.selectedColor}"
            tvSize.text = "${cartProduct.orderItem?.size}: ${cartProduct.quantity} x ${(size.orZero() + productPrice.orZero()).formatRupiah()}"
            tvTotal.text = cartProduct.totalAmount.toInt().formatRupiah()
        }
    }
}