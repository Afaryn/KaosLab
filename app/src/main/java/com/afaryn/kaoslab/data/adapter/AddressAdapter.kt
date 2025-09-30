package com.afaryn.kaoslab.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemAddressBinding
import com.afaryn.kaoslab.domain.model.Address

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.ItemViewHolder>() {

    val differ = AsyncListDiffer(this, MyDiffUtilCallback())

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class ItemViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Address) = binding.run {
            tvAddressName.text = item.name
            tvAddress.text = item.location
            tvAddressInfo.text = item.detail

            btnOptions.setOnClickListener {
                val popup = PopupMenu(it.context, it)
                popup.inflate(R.menu.option_menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_delete -> {
                            onItemDelete?.invoke(item)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
                true
            }
            itemView.setOnClickListener { onItemClick?.invoke(item) }
        }
    }

    class MyDiffUtilCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    var onItemDelete: ((Address) -> Unit)? = null
    var onItemClick: ((Address) -> Unit)? = null
}