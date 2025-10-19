package com.afaryn.kaoslab.presentation.ui_customer.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemProductBinding
import com.afaryn.kaoslab.domain.model.Design
import com.bumptech.glide.Glide

class ProductAdapter() :
    RecyclerView.Adapter<ProductAdapter.Viewholder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Design>() {
        override fun areItemsTheSame(
            oldItem: Design,
            newItem: Design
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Design,
            newItem: Design
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class Viewholder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onItemClick: ((Design) -> Unit)? = null
}