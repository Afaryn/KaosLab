package com.afaryn.kaoslab.ui_owner.my_shop.product_template

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemProductTemplateBinding
import com.afaryn.kaoslab.model.ProductTemplate
import com.bumptech.glide.Glide

class ProductTemplateAdapter(
    private val onItemClick: (ProductTemplate) -> Unit
) : ListAdapter<ProductTemplate, ProductTemplateAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductTemplateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemProductTemplateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(template: ProductTemplate) {
            binding.apply {
                tvProductName.text = template.name

                // Load image with Glide
                Glide.with(itemView.context)
                    .load(template.imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(ivProductImage)

                // Set click listener
                root.setOnClickListener {
                    onItemClick(template)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ProductTemplate>() {
        override fun areItemsTheSame(oldItem: ProductTemplate, newItem: ProductTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductTemplate, newItem: ProductTemplate): Boolean {
            return oldItem == newItem
        }
    }
}
