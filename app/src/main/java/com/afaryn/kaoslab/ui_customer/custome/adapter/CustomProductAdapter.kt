package com.afaryn.kaoslab.ui_customer.custome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemProductCustomBinding
import com.afaryn.kaoslab.model.CustomProduct
import com.bumptech.glide.Glide

class CustomProductAdapter(
    private val onItemSelected: () -> Unit
) : ListAdapter<CustomProduct, CustomProductAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition: Int? = null

    inner class ViewHolder(private val binding: ItemProductCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: CustomProduct, isSelected: Boolean) {
            val context = binding.root.context

            // Set nama & harga
            binding.textProductName.text = product.name
            binding.textProductPrice.text = "Rp ${product.basePrice} – ${product.maxPrice}"

            // Load gambar
            Glide.with(context)
                .load(product.imageUrl ?: R.drawable.img)
                .placeholder(R.drawable.img)
                .into(binding.imageProduct)

            // Ubah tampilan saat dipilih
            if (isSelected) {
                binding.linearProduk.setBackgroundColor(ContextCompat.getColor(context, R.color.darkBlue))
                binding.textProductName.setTextColor(ContextCompat.getColor(context, R.color.cream))
            } else {
                binding.linearProduk.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                binding.textProductName.setTextColor(ContextCompat.getColor(context, R.color.darkBlue))
            }

            // OnClick toggle: hanya satu yang bisa dipilih
            binding.root.setOnClickListener {
                val previousSelected = selectedPosition
                val currentPosition = bindingAdapterPosition

                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                if (previousSelected != currentPosition) {
                    selectedPosition = currentPosition
                    notifyItemChanged(previousSelected ?: -1)
                    notifyItemChanged(currentPosition)
                    onItemSelected()
                }
            }
        }
    }

    fun getSelectedItem(): CustomProduct? {
        return selectedPosition?.let {
            if (it in 0 until itemCount) getItem(it) else null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductCustomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isSelected = position == selectedPosition)
    }

    class DiffCallback : DiffUtil.ItemCallback<CustomProduct>() {
        override fun areItemsTheSame(oldItem: CustomProduct, newItem: CustomProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CustomProduct, newItem: CustomProduct): Boolean {
            return oldItem == newItem
        }
    }
}
