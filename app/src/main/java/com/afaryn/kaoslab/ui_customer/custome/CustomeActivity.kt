package com.afaryn.kaoslab.ui_customer.custome

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.ActivityCustomeBinding
import com.afaryn.kaoslab.model.CustomProduct
import com.afaryn.kaoslab.model.SizeOption
import com.afaryn.kaoslab.ui_customer.custome.viewModel.CustomViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CustomeActivity : AppCompatActivity() {

    private var _binding : ActivityCustomeBinding?=null
    private val binding get ()=_binding!!
    private val viewModel by viewModels<CustomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCustomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appBar()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StepOneFragment())
            .commit()

//        val products = listOf(
//            CustomProduct(
//                name = "Short sleeve t-shirt",
//                type = "0",
//                basePrice = 55000,
//                maxPrice = 65000,
//                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kaaoslab.firebasestorage.app/o/custom_product%2FRectangle%2010-5.png?alt=media&token=266f8002-0bbf-4737-b361-c52eb3a4956d",
//                sizes = listOf(
//                    SizeOption("S", 0),
//                    SizeOption("M", 0),
//                    SizeOption("L", 0),
//                    SizeOption("XL", 0),
//                    SizeOption("XXL", 5000),
//                    SizeOption("XXXL", 10000)
//                ),
//                colors = listOf("#FF0000", "#00FF00", "#0000FF"),
//                createdAt = Timestamp.now()
//            ),CustomProduct(
//                name = "Long sleeve t-shirt",
//                type = "0",
//                basePrice = 60000,
//                maxPrice = 75000,
//                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kaaoslab.firebasestorage.app/o/custom_product%2FRectangle%2010-2.png?alt=media&token=f214599a-66b9-4db5-85c2-71a667f6960a",
//                sizes = listOf(
//                    SizeOption("S", 0),
//                    SizeOption("M", 0),
//                    SizeOption("L", 0),
//                    SizeOption("XL", 0),
//                    SizeOption("XXL", 5000),
//                    SizeOption("XXXL", 10000)
//                ),
//                colors = listOf("#FF0000", "#00FF00", "#0000FF"),
//                createdAt = Timestamp.now()
//            ),CustomProduct(
//                name = "Hoodie",
//                type = "0",
//                basePrice = 110000,
//                maxPrice = 130000,
//                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kaaoslab.firebasestorage.app/o/custom_product%2FRectangle%2010-3.png?alt=media&token=efdb8da1-712b-4e29-9e25-95aaf623fe75",
//                sizes = listOf(
//                    SizeOption("S", 0),
//                    SizeOption("M", 0),
//                    SizeOption("L", 0),
//                    SizeOption("XL", 0),
//                    SizeOption("XXL", 10000),
//                    SizeOption("XXXL", 10000)
//                ),
//                colors = listOf("#FF0000", "#00FF00", "#0000FF"),
//                createdAt = Timestamp.now()
//            ),
//            CustomProduct(
//                name = "Women’s t-shirt",
//                type = "0",
//                basePrice = 55000,
//                maxPrice = 65000,
//                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kaaoslab.firebasestorage.app/o/custom_product%2FRectangle%2010-3.png?alt=media&token=efdb8da1-712b-4e29-9e25-95aaf623fe75",
//                sizes = listOf(
//                    SizeOption("S", 0),
//                    SizeOption("M", 0),
//                    SizeOption("L", 0),
//                    SizeOption("XL", 0),
//                    SizeOption("XXL", 5000),
//                    SizeOption("XXXL", 5000)
//                ),
//                colors = listOf("#FF0000", "#00FF00", "#0000FF"),
//                createdAt = Timestamp.now()
//            ),
//            CustomProduct(
//                name = "Jacket",
//                type = "0",
//                basePrice = 110000,
//                maxPrice = 130000,
//                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kaaoslab.firebasestorage.app/o/custom_product%2FRectangle%2010.png?alt=media&token=31594fee-01ba-4f63-a0f0-7428704b14c6",
//                sizes = listOf(
//                    SizeOption("S", 0),
//                    SizeOption("M", 0),
//                    SizeOption("L", 0),
//                    SizeOption("XL", 0),
//                    SizeOption("XXL", 10000),
//                    SizeOption("XXXL", 10000)
//                ),
//                colors = listOf("#FF0000", "#00FF00", "#0000FF"),
//                createdAt = Timestamp.now()
//            )
//        )
//        viewModel.submitMultipleProducts(products)
    }



    private fun appBar() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }
}
