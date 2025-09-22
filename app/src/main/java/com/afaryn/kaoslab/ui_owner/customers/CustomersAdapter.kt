package com.afaryn.kaoslab.ui_owner.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemCustomerBinding
import com.afaryn.kaoslab.model.User
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class CustomersAdapter : ListAdapter<User, CustomersAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CustomerViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: User) {
            binding.apply {
                tvCustomerName.text = customer.name ?: "Unknown"
                tvCustomerEmail.text = customer.email ?: "No email"
                tvCustomerPhone.text = if (customer.phone.isNotEmpty()) customer.phone else "No phone"

                // Format joined date
                val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                tvJoinedDate.text = customer.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Unknown"

                // Load profile picture
                if (customer.profilePicture.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(customer.profilePicture)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfilePicture)
                } else {
                    ivProfilePicture.setImageResource(R.drawable.ic_person)
                }
            }
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
