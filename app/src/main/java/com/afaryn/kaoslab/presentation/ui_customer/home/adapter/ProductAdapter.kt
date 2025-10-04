package com.afaryn.kaoslab.presentation.ui_customer.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemProductBinding
import com.afaryn.kaoslab.domain.model.Design
import com.bumptech.glide.Glide

class ProductAdapter(val items: List<Design>) :
    RecyclerView.Adapter<ProductAdapter.Viewholder>() {

    inner class Viewholder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Design) = with(binding) {
            titleText.text = item.title
            authorText.text = "by " + item.designerName


            val imageUrl = item.thumbnailUrl

            Glide.with(root.context)
                .load(imageUrl)
                .into(imagePost)

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    var onItemClick: ((Design) -> Unit)? = null
}