package com.afaryn.kaoslab.presentation.ui_customer.feed.post

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afaryn.kaoslab.databinding.ActivityPostFeedBinding
import com.afaryn.kaoslab.domain.model.Feed
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.setLoading
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFeedActivity : AppCompatActivity() {

    private var _binding: ActivityPostFeedBinding? = null
    private val binding get() = _binding!!
    private var imgUri: Uri? = null
    private val vm by viewModels<PostFeedViewModel>()

    private val requestAllPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val mediaGranted =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
                else permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true

            if (!mediaGranted) {
                toast("Harap beri semua izin untuk melanjutkan")
                finish()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imgUri = uri
                binding.ivContent.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPostFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        setActions()
    }

    private fun checkPermissions() {
        val permisssions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE
        ) else arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        requestAllPermissions.launch(permisssions)
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }

        btnPickImg.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnPost.setOnClickListener {
            if (imgUri == null || etCaption.text.toString().isEmpty()) {
                toast("Please fill all the fields...")
                return@setOnClickListener
            }

            postFeed(Feed(caption = etCaption.text.toString()))
        }
    }

    private fun postFeed(feed: Feed) = lifecycleScope.launch {
//        vm.postFeed(feed, imgUri!!).collect {
//            when(it) {
//                is Resource.Loading -> binding.btnPost.setLoading(true, "Post")
//                is Resource.Error -> {
//                    binding.btnPost.setLoading(false, "Post")
//                    toast(it.error)
//                }
//                is Resource.Success -> {
//                    binding.btnPost.setLoading(false, "Post")
//                    toast("Posted successfully")
//                    finish()
//                }
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}