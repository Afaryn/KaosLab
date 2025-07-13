package com.afaryn.kaoslab.ui_customer.custome.stepTwo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.gridlayout.widget.GridLayout
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentStepTwoBinding
import com.afaryn.kaoslab.model.CustomProduct
import com.afaryn.kaoslab.ui_customer.custome.CustomeActivity
import com.afaryn.kaoslab.ui_customer.custome.stepThree.StepThreeFragment
import com.afaryn.kaoslab.ui_customer.custome.viewModel.CustomViewModel
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayout.LayoutParams
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StepTwoFragment : Fragment() {

    private var _binding: FragmentStepTwoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<CustomViewModel>()
    private val selectedSizes = mutableSetOf<String>()
    private var selectedColor: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil produk yang dipilih user (hanya satu)
        val selected = viewModel.selectedProduct
        Log.d("STEP_TWO_SELECTION", "Item yang dikirim ke StepTwo: ${selected?.name}")

        if (selected != null) {
            populateProduct(selected)
            validateSelections()
        }

        binding.btnNext.setOnClickListener {
            // Simpan data ke ViewModel sekali lagi untuk jaga-jaga
            viewModel.selectedSizes = selectedSizes
            viewModel.selectedColor = selectedColor

            (activity as? CustomeActivity)?.goToStep(3)
        }
    }

    private fun populateProduct(product: CustomProduct) {
        // Gambar
        Glide.with(requireContext())
            .load(product.imageUrl)
            .into(binding.imageProduct)

        // Nama dan harga
        binding.productName.text = product.name
        binding.productPrice.text = "Rp ${product.basePrice} - ${product.maxPrice}"

        // Ukuran
        binding.sizeOptions.removeAllViews()
        product.sizes.forEach { size ->
            val sizeView = createSizeChip(size.label)
            binding.sizeOptions.addView(sizeView)
        }

        // Warna
        binding.colorOptions.removeAllViews()
        product.colors.forEach { colorHex ->
            val colorCircle = createColorCircle(colorHex)
            binding.colorOptions.addView(colorCircle)
        }
    }

    private fun createSizeChip(size: String): TextView {
        val tv = TextView(requireContext()).apply {
            text = size
            setPadding(35,20,35,20)
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.bg_outline_button)
            layoutParams = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10,10,10,10)
            }

            setOnClickListener {
                if (selectedSizes.contains(size)) {
                    selectedSizes.remove(size)
                } else {
                    selectedSizes.add(size)
                }
                updateSizeSelectionUI()
                validateSelections() // <-- panggil ini
            }

        }
        return tv
    }

    private fun updateSizeSelectionUI() {
        for (i in 0 until binding.sizeOptions.childCount) {
            val view = binding.sizeOptions.getChildAt(i)
            if (view is TextView) {
                val sizeLabel = view.text.toString()
                if (selectedSizes.contains(sizeLabel)) {
                    view.setBackgroundResource(R.drawable.bg_rounded_button)
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    view.setBackgroundResource(R.drawable.bg_outline_button)
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }
    }


    private fun createColorCircle(hexColor: String): View {
        val context = requireContext()
        val circle = ShapeableImageView(context).apply {
            val size = resources.getDimensionPixelSize(R.dimen.color_circle_size) // Ukuran dari dimens.xml

            layoutParams = ViewGroup.LayoutParams(size, size)
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(size / 5f)
                .build()

            background = ContextCompat.getDrawable(context, R.drawable.circle_background) // Optional: ada ripple atau shadow
            setBackgroundColor(Color.parseColor(hexColor))
            setPadding(12,12,12,12)

            strokeWidth = 0f

            setOnClickListener {
                selectedColor = hexColor
                updateColorSelectionUI()
                validateSelections() // <-- panggil ini
            }
        }
        return circle
    }


    private fun updateColorSelectionUI() {
        for (i in 0 until binding.colorOptions.childCount) {
            val circle = binding.colorOptions.getChildAt(i) as ShapeableImageView
            val bgColor = (circle.background as? ColorDrawable)?.color

            val isSelected = try {
                Color.parseColor(selectedColor) == bgColor
            } catch (e: Exception) {
                false
            }

            if (isSelected) {
                circle.strokeWidth = 6f
                circle.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.darkBlue)
            } else {
                circle.strokeWidth = 0f
            }
        }
    }

    private fun validateSelections() {
        val isValid = selectedSizes.isNotEmpty() && selectedColor != null
        binding.btnNext.isEnabled = isValid
        binding.btnNext.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (isValid) R.color.darkBlue else R.color.textDisable
            )
        )
        if (isValid) {
            viewModel.selectedSizes = selectedSizes
            viewModel.selectedColor = selectedColor
        }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
