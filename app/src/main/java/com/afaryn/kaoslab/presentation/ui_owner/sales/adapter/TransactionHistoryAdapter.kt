package com.afaryn.kaoslab.presentation.ui_owner.sales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemTransactionHistoryBinding
import com.afaryn.kaoslab.domain.model.Transaction
import com.afaryn.kaoslab.domain.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryAdapter : ListAdapter<Transaction, TransactionHistoryAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            with(binding) {
                // Set transaction description
                transactionDescription.text = transaction.description

                // Format and set date
                transaction.createdAt?.let { timestamp ->
                    val sdf = SimpleDateFormat("dd - MM - yyyy", Locale.getDefault())
                    transactionDate.text = sdf.format(timestamp.toDate())
                } ?: run {
                    transactionDate.text = "Unknown Date"
                }

                // Format and set amount with color based on transaction type
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                formatter.currency = Currency.getInstance("IDR")

                when (transaction.type) {
                    TransactionType.PAYMENT -> {
                        transactionAmount.text = "+${formatter.format(transaction.amount).replace("IDR", "Rp")}"
                        transactionAmount.setTextColor(itemView.context.getColor(R.color.chocoTan))
                        // Use a simple drawable or color for the icon background
                        transactionIcon.setBackgroundResource(R.drawable.circle_background_gray)
                        transactionIcon.setImageResource(android.R.drawable.ic_menu_add)
                    }
                    TransactionType.WITHDRAWAL -> {
                        transactionAmount.text = "-${formatter.format(transaction.amount).replace("IDR", "Rp")}"
                        transactionAmount.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                        // Use a simple drawable or color for the icon background
                        transactionIcon.setBackgroundResource(R.drawable.circle_background_gray)
                        transactionIcon.setImageResource(android.R.drawable.ic_menu_delete)
                    }
                }
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
