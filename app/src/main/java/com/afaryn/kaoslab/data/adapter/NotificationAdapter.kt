package com.afaryn.kaoslab.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.data.local.room.entity.NotificationEntity
import com.afaryn.kaoslab.databinding.ItemNotificationBinding
import com.afaryn.kaoslab.utils.formatElapsedTime
import com.afaryn.kaoslab.utils.toDateString

class NotificationAdapter : Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<NotificationEntity>() {
        override fun areItemsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    var items: List<NotificationEntity>
        get() = differ.currentList
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            val sortedList = value.sortedByDescending { it.createdAt?.toDateString() }
            differ.submitList(sortedList) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        ViewHolder(binding.root) {
        fun bind(notification: NotificationEntity) = binding.apply {
            tvTitle.text = notification.title
            tvMessage.text = notification.message
            tvTime.text = notification.createdAt?.formatElapsedTime() ?: "-"

            val message = notification.message.orEmpty()

            val notificationIcon = when {
                message.contains("payment", ignoreCase = true) -> R.drawable.ic_card_pay
                message.contains("shipped", ignoreCase = true) -> R.drawable.ic_shipped
                message.contains("delivered", ignoreCase = true) -> R.drawable.ic_delivered
                else -> R.drawable.ic_notification_fill
            }

            imageIcon.setImageResource(notificationIcon)

            itemView.setOnLongClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.menu_delete)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_delete -> {
                            onItemDelete?.invoke(notification)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
                true
            }
        }
    }

    var onItemDelete: ((NotificationEntity) -> Unit)? = null
}