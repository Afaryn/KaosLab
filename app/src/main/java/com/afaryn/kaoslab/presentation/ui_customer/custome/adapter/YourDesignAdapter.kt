package com.afaryn.kaoslab.presentation.ui_customer.custome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.bumptech.glide.Glide

class YourDesignAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_DESIGN = 1
    }

    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String, newItem: String
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: String, newItem: String
        ): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_DESIGN
    }

    override fun getItemCount(): Int {
        return if (differ.currentList.isEmpty()) 1 else differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_your_design, parent, false)
        return if (viewType == VIEW_TYPE_EMPTY) EmptyViewHolder(view) else DesignViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EmptyViewHolder) {
            holder.bind()
        } else if (holder is DesignViewHolder) {
            holder.bind(differ.currentList[position])
        }
    }

    // --- Fungsi baru untuk mengatur posisi yang dipilih dari luar ---
    fun setSelectedPosition(position: Int) {
        val previousSelected = selectedPosition
        selectedPosition = position
        if (previousSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelected)
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPosition)
        }
    }

    @Suppress("DEPRECATION")
    inner class DesignViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageDesign)

        fun bind(imageUrl: String) {
            Glide.with(imageView.context).load(imageUrl).placeholder(R.drawable.img_add_design)
                .error(R.drawable.img_add_design).into(imageView)

            // Tambahkan visual saat terpilih
            imageView.background = if (adapterPosition == selectedPosition) {
                ContextCompat.getDrawable(imageView.context, R.drawable.bg_design_selected)
            } else {
                null // atau Drawable default
            }

            imageView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)

                onDesignSelected?.invoke(imageUrl) // Panggil callback tanpa parameter
            }
        }
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageDesign)

        fun bind() {
            imageView.setImageResource(R.drawable.img_add_design)
            imageView.setOnClickListener {
                onEmptyClick?.invoke()
            }
        }
    }

    var onDesignSelected: ((String) -> Unit)? = null
    var onEmptyClick: (() -> Unit)? = null
}