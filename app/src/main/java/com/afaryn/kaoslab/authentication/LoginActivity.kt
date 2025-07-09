package com.afaryn.kaoslab.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityLoginBinding
import com.afaryn.kaoslab.ui_customer.MainActivity

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false

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
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
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
