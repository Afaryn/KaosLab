package com.afaryn.kaoslab.ui_customer.custome

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityCustomeBinding
import com.afaryn.kaoslab.ui_customer.custome.stepOne.StepOneFragment
import com.afaryn.kaoslab.ui_customer.custome.stepThree.StepThreeFragment
import com.afaryn.kaoslab.ui_customer.custome.stepTwo.StepTwoFragment
import com.afaryn.kaoslab.ui_customer.custome.viewModel.CustomViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CustomeActivity : AppCompatActivity() {

    private var _binding: ActivityCustomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CustomViewModel>()

    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCustomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBar()
        goToStep(1)

        binding.imgStepOne.setOnClickListener {
            if (currentStep > 1) goToStep(1)
        }
        binding.imgStepTwo.setOnClickListener {
            if (currentStep > 2) goToStep(2)
        }
    }

    fun goToStep(step: Int) {
        currentStep = step

        val fragment = when (step) {
            1 -> StepOneFragment()
            2 -> StepTwoFragment()
            3 -> StepThreeFragment()
            else -> StepOneFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateStepIndicator(step)
    }


    private fun updateStepIndicator(step: Int) {
        when (step) {
            1 -> {
                binding.imgStepOne.setImageResource(R.drawable.step_one_fill)
                binding.imgStepTwo.setImageResource(R.drawable.step_two_outline)
                binding.imgStepThree.setImageResource(R.drawable.step_three_outline)
                binding.txtStep.setText("Step 1: Select Merchandise Template")
            }
            2 -> {
                binding.imgStepOne.setImageResource(R.drawable.step_one_outline)
                binding.imgStepTwo.setImageResource(R.drawable.step_two_fill)
                binding.imgStepThree.setImageResource(R.drawable.step_three_outline)
                binding.txtStep.setText("Step 2: Select Merchandise Size and Color")
            }
            3 -> {
                binding.imgStepOne.setImageResource(R.drawable.step_one_outline)
                binding.imgStepTwo.setImageResource(R.drawable.step_two_outline)
                binding.imgStepThree.setImageResource(R.drawable.step_three_fill)
                binding.txtStep.setText("Step 3: Select Merchandise Design")
            }
        }
    }

    private fun appBar() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
