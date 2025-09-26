package com.afaryn.kaoslab.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemCartBinding
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.glide

class CartAdapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

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
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
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

            tvProductCartName.text = cartProduct.orderItem?.designType?.product?.name
            tvProductCartPrice.text = cartProduct.totalAmount.toInt().formatRupiah()

            cartProduct.selectedColor?.toColorInt()?.let {
                imageCartProductColor.circleBackgroundColor = it
            }

            tvCartProductSize.text = cartProduct.orderItem?.size
            tvCartProductQuantity.text = cartProduct.quantity.toString()

            btnDelete.setOnClickListener {
                onItemDelete?.invoke(cartProduct)
            }
        }
    }

    var onItemDelete: ((CartProduct) -> Unit)? = null
}