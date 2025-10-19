package com.afaryn.kaoslab.presentation.ui_customer.desain

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityDetailDesainBinding
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.License
import com.afaryn.kaoslab.presentation.ui_customer.desain.checkout.DesignCheckOutActivity
import com.afaryn.kaoslab.utils.CustomBottomDialog
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.formatRupiah
import com.afaryn.kaoslab.utils.getParcelable
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailDesainActivity : AppCompatActivity() {

    private var _binding: ActivityDetailDesainBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<DetailDesignViewModel>()
    private var design: Design? = null
    private var licenses: List<License>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailDesainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        setActions()
    }

    @SuppressLint("SetTextI18n")
    private fun getData() = binding.run {
        val data = intent.getParcelable<Design>("desain") ?: return@run
        design = data
        licenses = data.licenses

        data.thumbnailUrl.takeIf { it.isNotEmpty() }?.let { ivDesign.glide(it) }
        tvDesigner.text = data.designerName
        tvPrice.text = "from ${data.minPrice.toInt().formatRupiah()}"
        tvTitle.text = data.title
        tvDescription.text = data.description

        checkFavorite(data)
    }

    private fun checkFavorite(design: Design) = lifecycleScope.launch {
        vm.checkFavorite(design.id).collect {
            when(it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> setupFav(it.data ?: false)
                else -> {}
            }
        }
    }

    private fun setupFav(isFav: Boolean) = binding.run {
        val drawable = if (isFav) R.drawable.ic_like_fill
        else R.drawable.ic_like_line

        btnFavorite.setImageResource(drawable)
    }

    private fun modifyFavorite(design: Design) = lifecycleScope.launch {
        vm.modifyFavorite(design).collect {
            when(it) {
                is Resource.Error -> toast(it.error)
                is Resource.Success -> checkFavorite(design)
                else -> {}
            }
        }
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnBuyNow.setOnClickListener {
            if (licenses == null || design == null) return@setOnClickListener
            CustomBottomDialog(this@DetailDesainActivity).show(licenses!!) { l ->
                startActivity(
                    Intent(this@DetailDesainActivity, DesignCheckOutActivity::class.java).apply {
                        putExtra("desain", design!!.copy(selectedLicense = l))
                    }
                )
            }
        }

        btnFavorite.setOnClickListener {
            design?.let { it1 -> modifyFavorite(it1) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}