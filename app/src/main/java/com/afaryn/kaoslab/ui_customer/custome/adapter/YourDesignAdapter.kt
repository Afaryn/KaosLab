package com.afaryn.kaoslab.ui_customer.custome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.bumptech.glide.Glide

class YourDesignAdapter(
    private var designs: List<String>,
    private val onDesignSelected: () -> Unit,
    private val onEmptyClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_DESIGN = 1
    }

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun getItemViewType(position: Int): Int {
        return if (designs.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_DESIGN
    }

    override fun getItemCount(): Int {
        return if (designs.isEmpty()) 1 else designs.size
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
            holder.bind(designs[position])
        }
    }

    fun updateData(newDesigns: List<String>) {
        this.designs = newDesigns
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    @Suppress("DEPRECATION")
    inner class DesignViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageDesign)

        fun bind(imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.img_add_design)
                .error(R.drawable.img_add_design)
                .into(imageView)

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

                onDesignSelected()
            }
        }
    }

    fun getSelectedDesign(): String? {
        return if (selectedPosition in designs.indices) designs[selectedPosition] else null
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageDesign)

        fun bind() {
            imageView.setImageResource(R.drawable.img_add_design)
            imageView.setOnClickListener {
                onEmptyClick()
            }
        }
    }
}

