package com.afaryn.kaoslab.ui_owner.sales.my_sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemSalesBinding
import com.afaryn.kaoslab.ui_owner.sales.my_sales.data.SalesItem

class SalesAdapter(private val salesList: List<SalesItem>) : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    inner class SalesViewHolder(private val binding: ItemSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(salesItem: SalesItem) {
            binding.userName.text = salesItem.userName
            binding.salesStatusTag.text = salesItem.status
            binding.productName.text = salesItem.productName
            binding.productDetails.text = salesItem.productDetails
            binding.productQuantity.text = "x${salesItem.quantity}"
            binding.totalAmount.text = salesItem.totalAmount

            // Set status tag background and text color based on status
            when (salesItem.status.lowercase()) {
                "unpaid" -> {
                    binding.salesStatusTag.setTextColor(binding.root.context.getColor(R.color.dimgray))
                }
                // Add more cases for other statuses (e.g., "to deliver", "shipping", "completed")
                // For example:
                // "completed" -> {
                //     binding.salesStatusTag.setBackgroundResource(R.drawable.completed_status_bg_rounded)
                //     binding.salesStatusTag.setTextColor(binding.root.context.getColor(R.color.completed_status_text))
                // }
                else -> { // Default style for other statuses
                    binding.salesStatusTag.setTextColor(binding.root.context.getColor(R.color.black))
                }
            }


            // Load user avatar (Optional: Use Glide/Coil)
            // if (!salesItem.userAvatarUrl.isNullOrEmpty()) {
            //     Glide.with(binding.userAvatar.context)
            //         .load(salesItem.userAvatarUrl)
            //         .apply(RequestOptions.circleCropTransform()) // For circular avatars
            //         .placeholder(R.drawable.circle_placeholder)
            //         .error(R.drawable.circle_placeholder)
            //         .into(binding.userAvatar)
            // } else {
            //     binding.userAvatar.setImageResource(R.drawable.circle_placeholder)
            // }

            // Load product image (Optional: Use Glide/Coil)
            // if (!salesItem.productImageUrl.isNullOrEmpty()) {
            //     Glide.with(binding.productImage.context)
            //         .load(salesItem.productImageUrl)
            //         .placeholder(R.drawable.rounded_image_background)
            //         .error(R.drawable.rounded_image_background)
            //         .into(binding.productImage)
            // } else {
            //     binding.productImage.setImageResource(R.drawable.rounded_image_background)
            // }

            // Handle button click
            binding.contactCustomerButton.setOnClickListener {
                // Implement contact customer logic here (e.g., open dialer, chat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val binding = ItemSalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.bind(salesList[position])
    }

    override fun getItemCount(): Int = salesList.size
}