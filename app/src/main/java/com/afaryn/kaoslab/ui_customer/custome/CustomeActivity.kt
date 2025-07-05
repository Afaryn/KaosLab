package com.afaryn.kaoslab.ui_customer.custome

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityCustomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomeActivity : AppCompatActivity() {

    private var _binding : ActivityCustomeBinding?=null
    private val binding get ()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCustomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StepOneFragment())
            .commit()
    }
}