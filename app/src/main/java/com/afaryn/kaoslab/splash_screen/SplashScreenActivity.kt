package com.afaryn.kaoslab.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.authentication.AuthViewModel
import com.afaryn.kaoslab.authentication.LoginActivity
import com.afaryn.kaoslab.databinding.ActivitySplashScreenBinding
import com.afaryn.kaoslab.ui_customer.MainActivity
import com.afaryn.kaoslab.ui_owner.OwnerActivity
import com.afaryn.kaoslab.utils.Constants.OWNER
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        prepareLogoAnimate()
    }

    private fun prepareLogoAnimate() {
        binding.logoFlashScreen.alpha = 0f
        binding.logoFlashScreen.animate().apply {
            duration = 1000
            alpha(1f)
        }.withEndAction {
            viewModel.isUserLoggedIn().observe(this) { loggedIn ->
                if (loggedIn) {
                    viewModel.getCurrentUser().observe(this) { resource ->
                        when (resource) {
                            is Response.Success -> {
                                val intent = if (resource.data.role == OWNER) {
                                    Intent(this, OwnerActivity::class.java)
                                } else {
                                    Intent(this, MainActivity::class.java)
                                }
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                            is Response.Error -> {
                                // If there's an error getting user data, logout and stay on login screen
                                viewModel.logout()
                                toast("Error loading user data: ${resource.message}")
                                binding.loadingIndicator.show()
                            }
                            is Response.Loading -> {
                                binding.loadingIndicator.show()
                            }
                        }
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }
}