package com.afaryn.kaoslab.presentation.ui_customer.custome.stepThree

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentStepThreeBinding
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.model.CustomProduct
import com.afaryn.kaoslab.domain.model.DesignType
import com.afaryn.kaoslab.domain.model.DesignUplType
import com.afaryn.kaoslab.domain.model.OrderItem
import com.afaryn.kaoslab.domain.model.SizeOption
import com.afaryn.kaoslab.presentation.ui_customer.custome.OrderSummaryActivity
import com.afaryn.kaoslab.presentation.ui_customer.custome.adapter.YourDesignAdapter
import com.afaryn.kaoslab.presentation.ui_customer.custome.viewModel.CustomViewModel
import com.afaryn.kaoslab.utils.UiState
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class StepThreeFragment : Fragment() {

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<CustomViewModel>()
    private var designSelected: Boolean = false
    private var inputTextValid: Boolean = false
    private val yourDesignAdapter by lazy { YourDesignAdapter() }

    private var activeTab: TabType = TabType.YOUR_DESIGN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepThreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabListeners()
        setupTextWatcher()
        setupRecyclerView()
        observeDesignState()
        updateNextButtonState()

        binding.tabYourDesign.performClick()

        viewModel.selectedProduct?.let {
            populateProduct(it)
        }

        binding.btnChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnNext.setOnClickListener {
            navigateToOrderSummary()
        }
    }

    private fun setupRecyclerView() {
        binding.designRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        yourDesignAdapter.onDesignSelected = { design ->
            viewModel.setSelectedYourDesign(design)
            designSelected = true
            inputTextValid = false // Reset text input validity
            binding.imagePreview.visibility = View.GONE // Hide image preview if switching
            binding.customTextInput.text?.clear() // Clear text input

            Glide.with(requireContext())
                .load(design)
                .into(binding.designOverlay)

            updateNextButtonState()
        }
        yourDesignAdapter.onEmptyClick = {
            Toast.makeText(requireContext(), "Arahkan ke halaman pembelian desain", Toast.LENGTH_SHORT).show()
        }
        binding.designRecyclerView.adapter = yourDesignAdapter
    }

    private fun populateProduct(product: CustomProduct) {
        Glide.with(requireContext())
            .load(product.imageUrl)
            .into(binding.tshirtImage)
    }

    private fun setupTabListeners() {
        binding.tabUpload.setOnClickListener {
            resetTabStyles()
            binding.tabUpload.setBackgroundResource(R.drawable.tab_selected)
            binding.tabUpload.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            activeTab = TabType.UPLOAD
            showUploadTab()
        }

        binding.tabYourDesign.setOnClickListener {
            resetTabStyles()
            binding.tabYourDesign.setBackgroundResource(R.drawable.tab_selected)
            binding.tabYourDesign.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            activeTab = TabType.YOUR_DESIGN
            showYourDesignTab()
        }

        binding.tabAddText.setOnClickListener {
            resetTabStyles()
            binding.tabAddText.setBackgroundResource(R.drawable.tab_selected)
            binding.tabAddText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            activeTab = TabType.ADD_TEXT
            showAddTextTab()
        }
    }

    private fun resetTabStyles() {
        listOf(binding.tabUpload, binding.tabYourDesign, binding.tabAddText).forEach {
            it.setBackgroundResource(R.drawable.tab_unselected)
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBlue))
        }
    }

    private fun showUploadTab() {
        binding.customTextInput.visibility = View.GONE
        binding.designRecyclerView.visibility = View.GONE
        binding.uploadSection.visibility = View.VISIBLE
        binding.tvOverlay.text = ""

        viewModel.setCustomDesign(null) // Reset other custom types
        viewModel.setSelectedYourDesign(null)

        // Keep image preview visible if an image was already selected/cropped
        designSelected = binding.imagePreview.isVisible && viewModel.selectedCustomDesignUri != null
        inputTextValid = false
        binding.customTextInput.text?.clear() // Clear text input
        updateNextButtonState()
    }

    private fun showYourDesignTab() {
        binding.customTextInput.visibility = View.GONE
        binding.uploadSection.visibility = View.GONE
        binding.designRecyclerView.visibility = View.VISIBLE
        binding.tvOverlay.text = ""

        viewModel.setCustomDesign(null) // Reset other custom types
        binding.imagePreview.visibility = View.GONE // Hide image preview
        binding.customTextInput.text?.clear() // Clear text input

        designSelected = viewModel.selectedYourDesignUrl != null
        inputTextValid = false
        updateNextButtonState()
        viewModel.fetchUserDesigns()
    }

    private fun showAddTextTab() {
        binding.designRecyclerView.visibility = View.GONE
        binding.uploadSection.visibility = View.GONE
        binding.customTextInput.visibility = View.VISIBLE

        viewModel.setCustomDesign(null) // Reset other custom types
        viewModel.setSelectedYourDesign(null)
        binding.imagePreview.visibility = View.GONE // Hide image preview
        binding.designOverlay.setImageDrawable(null)

        inputTextValid = binding.customTextInput.text.toString().isNotBlank()
        designSelected = false
        updateNextButtonState()
    }

    private fun setupTextWatcher() {
        binding.customTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                inputTextValid = currentText.isNotBlank()
                if (inputTextValid) {
                    viewModel.setCustomDesign(null)
                    viewModel.setSelectedYourDesign(null)
                    binding.tvOverlay.text = currentText
                } else {
                    binding.tvOverlay.text = ""
                }
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateNextButtonState() {
        val isValid = when (activeTab) {
            TabType.UPLOAD -> designSelected
            TabType.YOUR_DESIGN -> designSelected
            TabType.ADD_TEXT -> inputTextValid
        }

        binding.btnNext.isEnabled = isValid
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (isValid) R.color.darkBlue else R.color.textDisable
        )
    }

    private fun observeDesignState() {
        viewModel.designsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    if (state.isLoading == true) {
                        binding.progressBar2.show()
                        binding.designRecyclerView.hide()
                    } else {
                        binding.progressBar2.hide()
                        binding.designRecyclerView.show()
                    }
                }

                is UiState.Success -> {
                    val designs = state.data.orEmpty()
                    Log.d("YourDesignData", "Jumlah desain: ${designs.size}, Data: $designs")

                    if (designs.isEmpty()) {
                        Toast.makeText(requireContext(), "Belum ada desain", Toast.LENGTH_SHORT).show()
                    }

                    yourDesignAdapter.differ.submitList(designs)

                    viewModel.selectedYourDesignUrl?.let { url ->
                        val position = designs.indexOf(url)
                        if (position != RecyclerView.NO_POSITION) {
                            yourDesignAdapter.setSelectedPosition(position)
                            designSelected = true
                            updateNextButtonState()
                        }
                    }

                    binding.designRecyclerView.post {
                        binding.designRecyclerView.invalidate()
                        binding.designRecyclerView.requestLayout()
                    }
                }

                is UiState.Error -> {
                    Log.e("StepThreeFragment", "Gagal memuat desain: ${state.error}")
                    Toast.makeText(requireContext(), "Gagal memuat desain", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
            val uCrop = UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1000, 1000)

            cropImageLauncher.launch(uCrop)
        }
    }

    private val cropImageLauncher = registerForActivityResult(object : ActivityResultContract<UCrop, Uri?>() {
        override fun createIntent(context: Context, input: UCrop): Intent {
            return input.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == AppCompatActivity.RESULT_OK && intent != null) {
                UCrop.getOutput(intent)
            } else {
                null
            }
        }
    }) { resultUri ->
        if (resultUri != null) {
            binding.imagePreview.setImageURI(resultUri)
            binding.imagePreview.visibility = View.VISIBLE
            viewModel.setCustomDesign(resultUri)
            designSelected = true
            inputTextValid = false // Reset text input validity
            binding.customTextInput.text?.clear() // Clear text input

            binding.designOverlay.setImageURI(resultUri)

            updateNextButtonState()
        } else {
            Toast.makeText(requireContext(), "Gagal crop gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToOrderSummary() {
        val selectedProduct = viewModel.selectedProduct
        val selectedSizeLabels = viewModel.selectedSizes.value // Ini adalah Set<String>
        val selectedColor = viewModel.selectedColor.value

        if (selectedProduct == null || selectedSizeLabels.isNullOrEmpty() || selectedColor == null) {
            Toast.makeText(requireContext(), "Mohon lengkapi pilihan produk, ukuran, dan warna.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- Logic: Menghitung total harga dan total unit berdasarkan semua ukuran yang dipilih ---
        val totalPieces = selectedSizeLabels.size // Jumlah total kaos yang dipesan
        var basePricePerUnit = selectedProduct.basePrice // Harga dasar produk

        // Kita akan mengambil harga tambahan dari ukuran PERTAMA yang dipilih sebagai representasi
        // atau Anda bisa menghitung rata-rata atau total dari semua tambahan harga ukuran
        // Untuk kesederhanaan, kita akan menghitung harga per unit dari ukuran pertama
        val firstSizeLabel = selectedSizeLabels.first()
        val sizeOptionDetail: SizeOption? = selectedProduct.sizes.find { it.label == firstSizeLabel }
        sizeOptionDetail?.let {
            basePricePerUnit += it.additionalPrice
        } ?: run {
            Log.e("StepThreeFragment", "Ukuran '$firstSizeLabel' tidak ditemukan di produk ${selectedProduct.name}")
            Toast.makeText(requireContext(), "Ukuran yang dipilih tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        val totalAmount = basePricePerUnit * totalPieces

        // Siapkan detail kustomisasi berdasarkan activeTab
        val designId: String // Digunakan untuk mengidentifikasi jenis kustomisasi
        var designType: DesignType?  // URL untuk gambar desain atau Uri string

        when (activeTab) {
            TabType.UPLOAD -> {
                designId = DesignUplType.Upload.value
                designType = DesignType(
                    type = DesignUplType.Upload.value,
                    overlay = viewModel.selectedCustomDesignUri?.toString(),
                )
            }
            TabType.YOUR_DESIGN -> {
                designId = DesignUplType.URL.value
                designType = DesignType(
                    type = DesignUplType.URL.value,
                    overlay = viewModel.selectedYourDesignUrl,
                )
            }
            TabType.ADD_TEXT -> {
                designId = DesignUplType.Text.value
                designType = DesignType(
                    type = DesignUplType.Text.value,
                    text = binding.customTextInput.text.toString(),
                )
            }
        }

        // Get current authenticated user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not authenticated. Please login first.", Toast.LENGTH_SHORT).show()
            return
        }

        val cartProduct = CartProduct(
            orderItem = OrderItem(
                size = selectedSizeLabels.joinToString(", "),
                designId = designId,
                designType = designType.copy(product = viewModel.selectedProduct)
            ),
            quantity = totalPieces,
            totalAmount = totalAmount.toDouble(),
            selectedColor = selectedColor
        )

        val intent = Intent(requireContext(), OrderSummaryActivity::class.java).apply {
            putExtra("ORDER_DATA", cartProduct)
            putExtra("SELECTED_COLOR", selectedColor)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class TabType {
        UPLOAD, YOUR_DESIGN, ADD_TEXT
    }
}