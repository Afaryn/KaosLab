package com.afaryn.kaoslab.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemMyDesignBinding
import com.afaryn.kaoslab.domain.model.DesignOrder
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.domain.model.OrderStatus
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.toDateString

class DesignAdapter(): RecyclerView.Adapter<DesignAdapter.DesignViewHolder>() {

    private val diffUtil = object: DiffUtil.ItemCallback<DesignOrder>() {
        override fun areItemsTheSame(
            oldItem: DesignOrder,
            newItem: DesignOrder
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DesignOrder,
            newItem: DesignOrder
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
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class DesignViewHolder(val binding: ItemMyDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(order: DesignOrder, position: Int) = binding.run {
            tvName.text = order.design.title
            tvLicense.text = order.design.selectedLicense?.name
            tvPurchasedAt.text = "purchased at ${order.createdAt.toDateString()}"

            order.design.fileUrl.takeIf { it.isNotEmpty() }?.let {
                ivDesign.glide(it)
            }

            btnUse.text = if (order.status == DesignOrderStatus.Pending.value) "Update Status" else "Download"
            btnUse.setOnClickListener {
                when {
                    order.status == OrderStatus.Pending.value -> onUpdateStatus?.invoke(order, position)
                    order.downloaded > 0 && order.design.selectedLicense?.type == "standard" -> onLimit?.invoke()
                    else -> onUseDesign?.invoke(order, position)
                }
            }
        }
    }

    var onUpdateStatus: ((DesignOrder, Int) -> Unit)? = null
    var onUseDesign: ((DesignOrder, Int) -> Unit)? = null
    var onLimit: (() -> Unit)? = null
}