package com.afaryn.kaoslab.presentation.ui_customer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityMainBinding
import com.afaryn.kaoslab.presentation.ui_customer.account.design.MyDesignActivity
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.OrdersActivity
import com.afaryn.kaoslab.presentation.ui_customer.custome.CustomeActivity
import com.afaryn.kaoslab.presentation.ui_customer.search.SearchResultActivity
import com.afaryn.kaoslab.utils.SearchListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SearchListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    companion object {
        private const val POST_NOTIFICATIONS_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
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

        createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        action()
        checkForIntent()
    }

    private fun checkForIntent() {
        intent.getBooleanExtra("order", false).takeIf { it }?.let {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        intent.getBooleanExtra("design", false).takeIf { it }?.let {
            startActivity(Intent(this, MyDesignActivity::class.java))
        }
    }

    private fun action() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.feedFragment -> {
                    navController.navigate(R.id.feedFragment)
                    true
                }
                R.id.customFragment -> {
                    startActivity(Intent(this, CustomeActivity::class.java))
                    false
                }
                R.id.NotificationFragment -> {
                    navController.navigate(R.id.NotificationFragment)
                    true
                }
                R.id.AccountFragment -> {
                    navController.navigate(R.id.AccountFragment)
                    true
                }
                else -> false
            }
        }
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    POST_NOTIFICATIONS_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == POST_NOTIFICATIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Izin notifikasi diberikan")
            } else {
                showToast("Izin notifikasi ditolak")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reservation_notifications",
                "Reservation Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk reservasi"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onSearch(query: String) {

    }

    override fun triggerSearchView(isOpen: Boolean) {
        with(binding.searchView) {
            if (isOpen) {
                show()
                editText.requestFocus()
                val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            } else {
                hide()
                clearFocus()
            }

            editText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startActivity(Intent(this@MainActivity, SearchResultActivity::class.java).apply {
                        putExtra("query", v.text.toString())
                    })
                    hide()
                    clearFocus()
                    true
                } else false
            }
        }
    }
}