package com.afaryn.kaoslab.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private var _binding : ActivityRegistrationBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        action()
    }

    private fun action(){
        binding.apply {
            txtSignIn.setOnClickListener{
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}