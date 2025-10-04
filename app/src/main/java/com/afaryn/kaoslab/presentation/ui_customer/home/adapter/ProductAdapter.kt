package com.afaryn.kaoslab.presentation.ui_customer.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemProductBinding
import com.afaryn.kaoslab.domain.model.Product
import com.bumptech.glide.Glide

class ProductAdapter(val items: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.Viewholder>() {

    inner class Viewholder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Product) = with(binding) {
            titleText.text = item.name
            authorText.text = "by " + item.createdBy


            val imageUrl =
                item.imageUrl

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

    var onItemClick: ((Product) -> Unit)? = null
}