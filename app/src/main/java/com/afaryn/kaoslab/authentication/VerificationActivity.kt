package com.afaryn.kaoslab.authentication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {

    private var _binding : ActivityVerificationBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        action()

    }

    private fun action(){
        binding.apply {
            btnVerify.setOnClickListener {
//                val otp = getEnteredOtp() // kamu bisa buat fungsi ambil 4 editText jadi 1 string
//                if (otp == correctOtp) {
//                    showSuccessDialog()
//                } else {
//                    Toast.makeText(this, "Kode OTP salah", Toast.LENGTH_SHORT).show()
//                }

                showSuccessDialog()
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_verification_success, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
        val dialog = dialogBuilder.create()
        dialog.show()

        // Mulai animasi putar pada spinner
        val spinner = dialogView.findViewById<ImageView>(R.id.iv_spinner)
        val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_spinner)
        spinner.startAnimation(rotateAnim)

        // Jalankan delay 2 detik, lalu pindah ke halaman login
        Handler(mainLooper).postDelayed({
            dialog.dismiss()
            startActivity(Intent(this, LoginActivity::class.java)) // ganti sesuai nama activity login kamu
            finish()
        }, 2000) // 2000 ms = 2 detik
    }
}