package com.afaryn.kaoslab.presentation.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityLoginBinding
import com.afaryn.kaoslab.presentation.ui_customer.MainActivity
import com.afaryn.kaoslab.presentation.ui_designer.DesignerActivity
import com.afaryn.kaoslab.presentation.ui_owner.OwnerActivity
import com.afaryn.kaoslab.utils.Constants.DESIGNER
import com.afaryn.kaoslab.utils.Constants.OWNER
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        action()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun action() {
        binding.edtPass.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val drawable = binding.edtPass.compoundDrawables[drawableEnd]
                val touchAreaStart = binding.edtPass.right - drawable.bounds.width() - binding.edtPass.paddingEnd

                if (event.rawX >= touchAreaStart) {
                    v.performClick()

                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        binding.edtPass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.edtPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)
                    } else {
                        binding.edtPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.edtPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }

                    binding.edtPass.setSelection(binding.edtPass.text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }
        binding.btnLogin.setOnClickListener{
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password).observe(this) { resource ->
                    when (resource) {
                        is Response.Loading -> {
                            binding.progressBar.show()
                            binding.btnLogin.isEnabled = false
                        }
                        is Response.Success -> {
                            binding.progressBar.hide()
                            val intent = if (resource.data.role == OWNER) {
                                Intent(this, OwnerActivity::class.java)
                            } else if ( resource.data.role == DESIGNER ) {
                                Intent(this, DesignerActivity::class.java)
                            } else {
                                Intent(this, MainActivity::class.java)
                            }
                            startActivity(intent)
                            finish()
                        }
                        is Response.Error -> {
                            binding.progressBar.hide()
                            binding.btnLogin.isEnabled = true
                            toast(resource.message)
                        }
                        else -> {
                            binding.progressBar.hide()
                            binding.btnLogin.isEnabled = true
                        }
                    }
                }
            } else {
                toast("Email and password cannot be empty")
            }
        }
        binding.txtRegis.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
