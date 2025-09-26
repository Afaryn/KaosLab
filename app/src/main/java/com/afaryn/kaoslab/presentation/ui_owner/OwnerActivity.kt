package com.afaryn.kaoslab.presentation.ui_owner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityOwnerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OwnerActivity : AppCompatActivity() {

    private var _binding: ActivityOwnerBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optional: untuk padding sistem
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        action()
    }


    private fun action() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeOwnerFragment -> {
                    navController.navigate(R.id.homeOwnerFragment)
                    true
                }
                R.id.chatOwnerFragment -> {
                    navController.navigate(R.id.chatOwnerFragment)
                    true
                }
                R.id.businessReportFragment -> {
                    navController.navigate(R.id.businessReportFragment)
                    true
                }
                R.id.myShopOwnerFragment -> {
                    navController.navigate(R.id.myShopOwnerFragment)
                    true
                }
                else -> false
            }
        }
    }
}