package com.afaryn.kaoslab.presentation.ui_designer.manage_design.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.databinding.ItemLicenseBinding
import com.afaryn.kaoslab.domain.model.License

class LicenseAdapter(
    private val onLicenseToggle: (License, Boolean) -> Unit,
    private val onDeleteLicense: (License) -> Unit
) : ListAdapter<License, LicenseAdapter.LicenseViewHolder>(LicenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenseViewHolder {
        val binding = ItemLicenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LicenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LicenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LicenseViewHolder(
        private val binding: ItemLicenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(license: License) {
            with(binding) {
                tvLicenseName.text = license.name

                if (license.description.isNotEmpty()) {
                    tvLicenseDescription.text = license.description
                    tvLicenseDescription.visibility = View.VISIBLE
                } else {
                    tvLicenseDescription.visibility = View.GONE
                }

                cbLicense.setOnCheckedChangeListener(null)
                cbLicense.isChecked = true // Default selected
                cbLicense.setOnCheckedChangeListener { _, isChecked ->
                    onLicenseToggle(license, isChecked)
                }

                // Show delete button only for custom licenses
                if (license.isDefault) {
                    ivDelete.visibility = View.GONE
                } else {
                    ivDelete.visibility = View.VISIBLE
                    ivDelete.setOnClickListener {
                        onDeleteLicense(license)
                    }
                }
            }
        }
    }

    private class LicenseDiffCallback : DiffUtil.ItemCallback<License>() {
        override fun areItemsTheSame(oldItem: License, newItem: License): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: License, newItem: License): Boolean {
            return oldItem == newItem
        }
    }
}
