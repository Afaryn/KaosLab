package com.afaryn.kaoslab.presentation.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.databinding.ActivityRegistrationBinding
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.presentation.ui_customer.MainActivity
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private var _binding : ActivityRegistrationBinding?=null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

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
            btnRegis.setOnClickListener{
                val name = binding.edtName.text.toString()
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPass.text.toString()

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    toast("Please fill all fields")
                } else {
                    val userNew = User(
                        name = name,
                        email = email,
                    )
                    viewModel.register(email, password, userNew).observe(this@RegistrationActivity) { resource ->
                        when (resource) {
                            is Response.Loading -> {
                                binding.progressBar.show()
                                binding.btnRegis.isEnabled = false
                            }
                            is Response.Success -> {
                                binding.progressBar.hide()
                                val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                toast("Registration successful")
                                finish()
                            }
                            is Response.Error -> {
                                binding.progressBar.hide()
                                binding.btnRegis.isEnabled = true
                                toast(resource.message ?: "Registration failed")
                            }
                            else -> {
                                binding.progressBar.hide()
                                binding.btnRegis.isEnabled = true
                                toast("Unknown error occurred")
                            }
                        }
                    }
                }
            }
        }
    }
}
