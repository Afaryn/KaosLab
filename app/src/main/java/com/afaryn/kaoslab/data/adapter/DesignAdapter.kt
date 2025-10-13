package com.afaryn.kaoslab.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemMyDesignBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.utils.toDateString

class DesignAdapter(val isPending: Boolean = true): RecyclerView.Adapter<DesignAdapter.DesignViewHolder>() {

    private val diffUtil = object: DiffUtil.ItemCallback<Design>() {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DesignViewHolder =
        DesignViewHolder(
            ItemMyDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(
        holder: DesignViewHolder,
        position: Int
    ) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class DesignViewHolder(val binding: ItemMyDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(design: Design) = binding.run {
            tvName.text = design.title
            tvLicense.text = design.selectedLicense?.name
            tvPurchasedAt.text = "purchased at ${design.updatedAt.toDateString()}"

            btnUse.isVisible = !isPending
        }
    }
}