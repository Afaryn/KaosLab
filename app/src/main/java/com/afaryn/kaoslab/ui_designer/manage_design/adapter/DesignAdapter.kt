package com.afaryn.kaoslab.ui_designer.manage_design.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemDesignBinding
import com.afaryn.kaoslab.model.Design
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class DesignAdapter(
    private val onItemClick: (Design) -> Unit,
    private val onMoreClick: (Design, View) -> Unit
) : ListAdapter<Design, DesignAdapter.DesignViewHolder>(DesignDiffCallback()) {

    private var originalList: List<Design> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesignViewHolder {
        val binding = ItemDesignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DesignViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { design ->
                design.title.contains(query, ignoreCase = true) ||
                design.category.contains(query, ignoreCase = true)
            }
        }
        submitList(filteredList)
    }

    override fun submitList(list: List<Design>?) {
        originalList = list ?: emptyList()
        super.submitList(list)
    }

    inner class DesignViewHolder(
        private val binding: ItemDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(design: Design) {
            with(binding) {
                tvDesignTitle.text = design.title
                tvDesignCategory.text = design.category

                // Format price range
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvDesignPrice.text = when {
                    design.minPrice == design.maxPrice && design.minPrice > 0 -> {
                        formatter.format(design.minPrice)
                    }
                    design.minPrice > 0 && design.maxPrice > 0 -> {
                        "${formatter.format(design.minPrice)} - ${formatter.format(design.maxPrice)}"
                    }
                    else -> "Free"
                }

                // Load thumbnail image
                Glide.with(ivDesignThumbnail.context)
                    .load(design.thumbnailUrl)
                    .placeholder(com.afaryn.kaoslab.R.drawable.ic_image_placeholder)
                    .error(com.afaryn.kaoslab.R.drawable.ic_image_placeholder)
                    .into(ivDesignThumbnail)

                // Set click listeners
                root.setOnClickListener { onItemClick(design) }
                // Pass the ivMore view as anchor for the popup menu
                ivMore.setOnClickListener { onMoreClick(design, ivMore) }
            }
        }
    }

    private class DesignDiffCallback : DiffUtil.ItemCallback<Design>() {
        override fun areItemsTheSame(oldItem: Design, newItem: Design): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Design, newItem: Design): Boolean {
            return oldItem == newItem
        }
    }
}
