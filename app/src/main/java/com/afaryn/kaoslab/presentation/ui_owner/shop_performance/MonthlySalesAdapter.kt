package com.afaryn.kaoslab.presentation.ui_owner.shop_performance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemMonthlySalesBinding
import com.afaryn.kaoslab.domain.model.MonthlySales
import com.afaryn.kaoslab.utils.toCurrencyFormat
import java.text.NumberFormat
import java.util.Locale

class MonthlySalesAdapter(
    private val onItemClick: (MonthlySales) -> Unit = {}
) : ListAdapter<MonthlySales, MonthlySalesAdapter.MonthlySalesViewHolder>(
    MonthlySalesDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlySalesViewHolder {
        val binding = ItemMonthlySalesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MonthlySalesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthlySalesViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class MonthlySalesViewHolder(
        private val binding: ItemMonthlySalesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(monthlySales: MonthlySales, onItemClick: (MonthlySales) -> Unit) {
            binding.apply {
                tvMonthYear.text = "${monthlySales.month} ${monthlySales.year}"

                tvTotalSales.text = monthlySales.totalSales.toInt().toCurrencyFormat()

                tvOrderCount.text = "${monthlySales.orderCount} orders"

                root.setOnClickListener {
                    onItemClick(monthlySales)
                }
            }
        }
    }

    private class MonthlySalesDiffCallback : DiffUtil.ItemCallback<MonthlySales>() {
        override fun areItemsTheSame(oldItem: MonthlySales, newItem: MonthlySales): Boolean {
            return oldItem.month == newItem.month && oldItem.year == newItem.year
        }

        override fun areContentsTheSame(oldItem: MonthlySales, newItem: MonthlySales): Boolean {
            return oldItem == newItem
        }
    }
}
