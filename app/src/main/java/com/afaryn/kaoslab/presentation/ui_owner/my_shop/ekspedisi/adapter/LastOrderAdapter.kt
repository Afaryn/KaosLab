package com.afaryn.kaoslab.presentation.ui_owner.my_shop.ekspedisi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemEkspedisiBinding
import com.afaryn.kaoslab.domain.model.Kurir
import com.afaryn.kaoslab.domain.model.Order
import com.bumptech.glide.Glide

class EkspedisiAdapter(
    private val onItemClick: (Order) -> Unit
) : ListAdapter<Kurir, EkspedisiAdapter.EkspedisiViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EkspedisiViewHolder {
        val binding = ItemEkspedisiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EkspedisiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EkspedisiViewHolder, position: Int) {
        val ekspedisi = getItem(position)
        holder.bind(ekspedisi)
    }

    inner class EkspedisiViewHolder(
        private val binding: ItemEkspedisiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ekpedisi: Kurir) {
            binding.apply {
                // Set order data
                tvEkspedisiTitle.text = ekpedisi.name
                Glide.with(itemView.context)
                    .load(ekpedisi.logo)
                    .placeholder(R.color.line)
                    .into(ivEkspedisiLogo)
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Kurir>() {
        override fun areItemsTheSame(oldItem: Kurir, newItem: Kurir): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Kurir, newItem: Kurir): Boolean {
            return oldItem == newItem
        }
    }
}
