package com.afaryn.kaoslab.ui_customer.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemProductBinding
import com.afaryn.kaoslab.model.Product
import com.bumptech.glide.Glide

class ProductAdapter (val items: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.Viewholder>() {

        class Viewholder(val binding: ItemProductBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProductAdapter.Viewholder {
            val binding =
                ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return Viewholder(binding)
        }

        override fun onBindViewHolder(holder: ProductAdapter.Viewholder, position: Int) {
            val item = items[position]

            with(holder.binding) {
                titleText.text = item.name
                authorText.text = "by "+item.createdBy


                val imageUrl =
                    item.imageUrl

                Glide.with(root.context)
                    .load(imageUrl)
                    .into(imagePost)



                root.setOnClickListener {
//                    val intent = Intent(holder.itemView.context, DetailTreatmentActivity::class.java).apply {
//                        putExtra("treatment_data", item)
//                    }
//                    ContextCompat.startActivity(holder.itemView.context,intent,null)
                }
            }
        }

        override fun getItemCount(): Int = items.size
}