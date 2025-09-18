package com.afaryn.kaoslab.ui_designer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityDesignerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DesignerActivity : AppCompatActivity() {

    private var _binding: ActivityDesignerBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDesignerBinding.inflate(layoutInflater)
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
                R.id.homeDesignerFragment -> {
                    navController.navigate(R.id.homeDesignerFragment)
                    true
                }
                R.id.feedDesignerFragment -> {
                    navController.navigate(R.id.feedDesignerFragment)
                    true
                }
                R.id.customFragment -> {
                    navController.navigate(R.id.customFragment)
                    true
                }
                R.id.accountDesignerFragment -> {
                    navController.navigate(R.id.accountDesignerFragment)
                    true
                }
                else -> false
            }
        }
    }
}