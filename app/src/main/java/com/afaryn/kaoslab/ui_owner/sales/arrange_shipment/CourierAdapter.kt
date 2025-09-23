package com.afaryn.kaoslab.ui_owner.sales.arrange_shipment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemCourierBinding
import com.afaryn.kaoslab.model.Kurir
import com.bumptech.glide.Glide

class CourierAdapter(
    private val onCourierClick: (Kurir) -> Unit
) : ListAdapter<Kurir, CourierAdapter.CourierViewHolder>(CourierDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourierViewHolder {
        val binding = ItemCourierBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CourierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourierViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourierViewHolder(
        private val binding: ItemCourierBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(courier: Kurir) {
            binding.apply {
                courierName.text = courier.name

                Glide.with(itemView.context)
                    .load(courier.logo)
                    .into(courierLogo)

                root.setOnClickListener {
                    onCourierClick(courier)
                }
            }
        }
    }

    private class CourierDiffCallback : DiffUtil.ItemCallback<Kurir>() {
        override fun areItemsTheSame(oldItem: Kurir, newItem: Kurir): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Kurir, newItem: Kurir): Boolean {
            return oldItem == newItem
        }
    }
}
