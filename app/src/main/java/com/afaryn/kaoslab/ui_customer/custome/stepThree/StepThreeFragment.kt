package com.afaryn.kaoslab.ui_customer.custome.stepThree

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentStepThreeBinding
import com.afaryn.kaoslab.model.CustomProduct
import com.afaryn.kaoslab.ui_customer.custome.adapter.YourDesignAdapter
import com.afaryn.kaoslab.ui_customer.custome.viewModel.CustomViewModel
import com.afaryn.kaoslab.utils.UiState
import com.afaryn.kaoslab.utils.hide
import com.afaryn.kaoslab.utils.show
import com.bumptech.glide.Glide
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
    private var yourDesignAdapter: YourDesignAdapter? = null

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

        showYourDesignTab() // Tampilkan tab desain langsung

        viewModel.selectedProduct?.let {
            populateProduct(it)
        }

        binding.btnChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun setupRecyclerView() {
        binding.designRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        yourDesignAdapter = YourDesignAdapter(
            designs = listOf(),
            onDesignSelected = {
                designSelected = true
                updateNextButtonState()
            },
            onEmptyClick = {
                Toast.makeText(requireContext(), "Arahkan ke halaman pembelian desain", Toast.LENGTH_SHORT).show()
            }
        )
        binding.designRecyclerView.adapter = yourDesignAdapter
    }

    private fun populateProduct(product: CustomProduct) {
        Glide.with(requireContext())
            .load(product.imageUrl)
            .into(binding.tshirtImage)
    }

    private fun setupTabListeners() {
        val tabs = listOf(
            binding.tabUpload to ::showUploadTab,
            binding.tabYourDesign to ::showYourDesignTab,
            binding.tabAddText to ::showAddTextTab
        )

        tabs.forEach { (tabView, action) ->
            tabView.setOnClickListener {
                resetTabStyles()
                tabView.setBackgroundResource(R.drawable.tab_selected)
                tabView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                action()
            }
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
        binding.uploadSection.visibility = View.VISIBLE // tampilkan upload

        designSelected = false
        inputTextValid = false
        updateNextButtonState()
    }

    private fun showYourDesignTab() {
        binding.customTextInput.visibility = View.GONE
        binding.uploadSection.visibility = View.GONE
        binding.designRecyclerView.visibility = View.VISIBLE

        designSelected = false
        updateNextButtonState()
        viewModel.fetchUserDesigns()
    }

    private fun showAddTextTab() {
        binding.designRecyclerView.visibility = View.GONE
        binding.uploadSection.visibility = View.GONE
        binding.customTextInput.visibility = View.VISIBLE

        inputTextValid = binding.customTextInput.text.toString().isNotBlank()
        updateNextButtonState()
    }

    private fun setupTextWatcher() {
        binding.customTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                inputTextValid = s?.isNotBlank() == true
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateNextButtonState() {
        val isValid = designSelected || inputTextValid
        binding.btnNext.isEnabled = isValid
        binding.btnNext.setBackgroundTintList(
            ContextCompat.getColorStateList(
                requireContext(),
                if (isValid) R.color.darkBlue else R.color.textDisable
            )
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

                    yourDesignAdapter?.updateData(designs)

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
            designSelected = true
            updateNextButtonState()
            // TODO: Upload gambar ke Firestore / Storage jika perlu
        } else {
            Toast.makeText(requireContext(), "Gagal crop gambar", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

