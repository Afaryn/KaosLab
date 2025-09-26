package com.afaryn.kaoslab.presentation.ui_designer.manage_portofolio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemPortfolioBinding
import com.afaryn.kaoslab.domain.model.Portfolio
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class PortfolioAdapter(
    private val onItemClick: (Portfolio) -> Unit,
    private val onMoreClick: (Portfolio, View) -> Unit
) : ListAdapter<Portfolio, PortfolioAdapter.PortfolioViewHolder>(PortfolioDiffCallback()) {

    private var originalList: List<Portfolio> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val binding = ItemPortfolioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PortfolioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Portfolio>?) {
        originalList = list ?: emptyList()
        super.submitList(list)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        super.submitList(filteredList)
    }

    inner class PortfolioViewHolder(
        private val binding: ItemPortfolioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(portfolio: Portfolio) {
            binding.apply {
                tvTitle.text = portfolio.title
                tvDescription.text = portfolio.description

                // Format and display created date
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                tvCreatedDate.text = dateFormat.format(portfolio.createdAt.toDate())

                // Load image with Glide
                if (portfolio.imageUrl.isNotEmpty()) {
                    Glide.with(ivPortfolio.context)
                        .load(portfolio.imageUrl)
                        .centerCrop()
                        .into(ivPortfolio)
                    ivPortfolio.visibility = View.VISIBLE
                } else {
                    ivPortfolio.visibility = View.GONE
                }

                // Set click listeners
                root.setOnClickListener {
                    onItemClick(portfolio)
                }

                btnMore.setOnClickListener {
                    onMoreClick(portfolio, it)
                }
            }
        }
    }
}

class PortfolioDiffCallback : DiffUtil.ItemCallback<Portfolio>() {
    override fun areItemsTheSame(oldItem: Portfolio, newItem: Portfolio): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Portfolio, newItem: Portfolio): Boolean {
        return oldItem == newItem
    }
}
