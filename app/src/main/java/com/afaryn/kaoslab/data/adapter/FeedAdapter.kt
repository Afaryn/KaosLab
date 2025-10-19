package com.afaryn.kaoslab.data.adapter

import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ItemFeedBinding
import com.afaryn.kaoslab.domain.model.Portfolio
import com.afaryn.kaoslab.utils.CustomTypefaceSpan
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.orZero
import com.afaryn.kaoslab.utils.toMonthDay

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    var currentUserId: String = ""

    fun setUserId(id: String) {
        currentUserId = id
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Portfolio>() {
        override fun areItemsTheSame(
            oldItem: Portfolio,
            newItem: Portfolio
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Portfolio,
            newItem: Portfolio
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedViewHolder =
        FeedViewHolder(ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int
    ) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class FeedViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feed: Portfolio) = with(binding) {
            val usernameFont = ResourcesCompat.getFont(itemView.context, R.font.pjs_bold)
            val captionFont = ResourcesCompat.getFont(itemView.context, R.font.pjs_regular)

            val name = feed.user?.name ?: "Designer"

            val spannable = SpannableString("$name ${feed.description}").apply {
                setSpan(
                    CustomTypefaceSpan(usernameFont!!),
                    0,
                    name.length.orZero(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                setSpan(
                    CustomTypefaceSpan(captionFont!!),
                    name.length.orZero() + 1,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            tvUsernameCaption.text = spannable
            tvCreatedAt.text = feed.createdAt.toMonthDay()
            feed.imageUrl.takeIf { it.isNotEmpty() }?.let { imagePost.glide(it) }

            val drawable = if (feed.isLikedBy(currentUserId)) R.drawable.ic_like_fill
            else R.drawable.ic_like_line

            btnLike.setImageResource(drawable)

            btnLike.setOnClickListener { onLike?.invoke(feed, !feed.isLikedBy(currentUserId)) }
            btnComment.setOnClickListener { onComment?.invoke(feed) }
            btnShare.setOnClickListener { onShare?.invoke(feed) }
        }
    }

    var onLike: ((Portfolio, Boolean) -> Unit)? = null
    var onComment: ((Portfolio) -> Unit)? = null
    var onShare: ((Portfolio) -> Unit)? = null
}