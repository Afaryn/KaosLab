package com.afaryn.kaoslab.presentation.ui_owner.sales.order_details

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afaryn.kaoslab.R.drawable
import com.afaryn.kaoslab.databinding.FragmentOrderDetailsBinding
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.dp
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import com.afaryn.kaoslab.utils.toast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderDetailsViewModel by viewModels()
    private val args: OrderDetailsFragmentArgs by navArgs()

    private var currentOrder: Order? = null
    private var customerPhone: String = ""
    private var designImageUrl: String = ""

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            downloadDesignImage()
        } else {
            toast("Permission denied. Cannot download image.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        hideBottomNavOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeViewModel()
        loadOrderDetails()
    }

    private fun setupToolbar() {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.getOrderById(args.orderId).observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }

                is Response.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    currentOrder = response.data
                    populateOrderDetails(response.data)
                    loadCustomerDetails(response.data.customerId)
                }

                is Response.Error -> {
                    binding.progressBar.visibility = View.GONE
                    toast(response.message)
                }

                else -> {}
            }
        }

        lifecycleScope.launch {
            viewModel.updateOrderState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        binding.primaryButton.isEnabled = false
                        binding.primaryButton.text = "Updating..."
                    }

                    is Response.Success -> {
                        if (response.data.isNotEmpty()) {
                            toast("Order updated successfully")
                            findNavController().navigateUp()
                        }
                    }

                    is Response.Error -> {
                        binding.primaryButton.isEnabled = true
                        setupButtonsForStatus(currentOrder?.status ?: "")
                        toast(response.message)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadOrderDetails() {
        // Orders are loaded through LiveData observation
    }

    private fun loadCustomerDetails(customerId: String) {
        viewModel.getUserById(customerId).observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    customerPhone = response.data.phone
                    binding.customerAddress.text = "${response.data.name}\n${response.data.email}"
                }

                is Response.Error -> {
                    binding.customerAddress.text = "Customer information not available"
                }

                else -> {}
            }
        }
    }

    private fun populateOrderDetails(order: Order) {
        binding.apply {
            // Customer info
            customerName.text = order.customerName
            order.customerAvatarUrl.takeIf { it.isNotEmpty() }?.let {
                Glide.with(requireContext())
                    .load(it)
                    .into(customerAvatar)
            }

            // Product info

            val product = order.cartProducts.first()

            product.orderItem?.designType?.overlay?.let {
                designOverlay.glide(it)
            }

            product.orderItem?.designType?.product?.imageUrl?.let {
                imageCustomProduct.glide(it)
            }

            product.orderItem?.designType?.text?.let {
                tvOverlay.text = it
            }

            productTitle.text = product.orderItem?.designType?.product?.name

            // Order details
            colorValue.text = order.cartProducts.joinToString(", ") { it.selectedColor.toString() }
            sizeValue.text = order.cartProducts.joinToString { it.orderItem?.size.toString() }

            // Format total amount
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            totalAmount.text = formatter.format(order.totalAmount)

            // Setup status indicator and buttons
            setupStatusIndicator(order.status)
            setupButtonsForStatus(order.status)

            // Show courier info if available
            if (order.courierInfo != null && order.noResi != null) {
                courierInfoSection.visibility = View.VISIBLE
                trackingNumber.text = order.noResi
                Glide.with(requireContext())
                    .load(order.courierLogo)
                    .into(courierLogo)
            } else {
                courierInfoSection.visibility = View.GONE
            }


        }
    }

    private fun setupStatusIndicator(status: String) {
        binding.apply {
            // Reset all status indicators and progress lines to inactive state
            unpaidStatus.backgroundTintList = ColorStateList.valueOf(
                "#CCCCCC".toColorInt()
            )
            processingStatus.backgroundTintList = ColorStateList.valueOf(
                "#CCCCCC".toColorInt()
            )
            shippedStatus.backgroundTintList = ColorStateList.valueOf(
                "#CCCCCC".toColorInt()
            )
            deliveredStatus.backgroundTintList = ColorStateList.valueOf(
                "#CCCCCC".toColorInt()
            )

            // Set active status and progress based on current status
            when (status) {
                "pending" -> {
                    // Only unpaid is active (blue)
                    unpaidStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#2196F3".toColorInt()
                    )
                    statusText.text = "Unpaid"
                    statusIcon.visibility = View.GONE
                }

                "processing" -> {
                    // Unpaid completed (green), processing active (blue)
                    unpaidStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#4CAF50".toColorInt()
                    )
                    processingStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            "#2196F3".toColorInt()
                        )
                    statusText.text = "To Deliver"
                    statusIcon.visibility = View.GONE
                }

                "shipped" -> {
                    // Unpaid and processing completed (green), shipped active (blue)
                    unpaidStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#4CAF50".toColorInt()
                    )
                    processingStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            "#4CAF50".toColorInt()
                        )
                    shippedStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#2196F3".toColorInt()
                    )
                    statusText.text = "Shipping"
                    statusIcon.visibility = View.GONE
                }

                "delivered" -> {
                    // All statuses completed (green)
                    unpaidStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#4CAF50".toColorInt()
                    )
                    processingStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            "#4CAF50".toColorInt()
                        )
                    shippedStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#4CAF50".toColorInt()
                    )
                    deliveredStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        "#4CAF50".toColorInt()
                    )
                    statusText.text = "Completed"
                    statusIcon.visibility = View.VISIBLE
                }
            }

            // Update progress lines based on status
            updateProgressLines(status)
        }
    }

    private fun updateProgressLines(status: String) {
        binding.apply {
            // Set all progress lines to inactive (gray) initially
            progressLine1.setBackgroundColor("#CCCCCC".toColorInt())
            progressLine2.setBackgroundColor("#CCCCCC".toColorInt())
            progressLine3.setBackgroundColor("#CCCCCC".toColorInt())

            // Update progress lines based on status
            when (status) {
                "pending" -> {
                    // No progress lines are active yet
                }

                "processing" -> {
                    // First progress line is completed (green)
                    progressLine1.setBackgroundColor("#4CAF50".toColorInt())
                }

                "shipped" -> {
                    // First two progress lines are completed (green)
                    progressLine1.setBackgroundColor("#4CAF50".toColorInt())
                    progressLine2.setBackgroundColor("#4CAF50".toColorInt())
                }

                "delivered" -> {
                    // All progress lines are completed (green)
                    progressLine1.setBackgroundColor("#4CAF50".toColorInt())
                    progressLine2.setBackgroundColor("#4CAF50".toColorInt())
                    progressLine3.setBackgroundColor("#4CAF50".toColorInt())
                }
            }
        }
    }

    private fun setupButtonsForStatus(status: String) {
        binding.apply {
            when (status) {
                "pending" -> {
                    primaryButton.text = "Contact Customer"
                    primaryButton.visibility = View.VISIBLE
                    secondaryButton.visibility = View.VISIBLE
                    primaryButton.setOnClickListener { contactCustomer() }
                }

                "processing" -> {
                    primaryButton.text = "Arrange Shipment"
                    secondaryButton.text = "Download"
                    primaryButton.visibility = View.VISIBLE
                    secondaryButton.visibility = View.VISIBLE
                    primaryButton.setOnClickListener { arrangeShipment() }
                    secondaryButton.setOnClickListener { downloadOrder() }
                }

                "shipped" -> {
                    primaryButton.text = "Set to Delivered"
                    primaryButton.visibility = View.VISIBLE
                    secondaryButton.visibility = View.VISIBLE
                    primaryButton.setOnClickListener { setToDelivered() }
                }

                "delivered" -> {
                    primaryButton.visibility = View.GONE
                    secondaryButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun contactCustomer() {
        if (customerPhone.isNotEmpty()) {
            val message =
                "Hello ${currentOrder?.customerName}, regarding your order ${args.orderId}..."
            val uri = Uri.parse("https://wa.me/$customerPhone?text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else {
            toast("Customer phone number not available")
        }
    }

    private fun arrangeShipment() {
        val action = OrderDetailsFragmentDirections
            .actionOrderDetailsFragmentToChooseCourierFragment(args.orderId)
        findNavController().navigate(action)
    }

    private fun downloadOrder() {
        // On Android 10 (API 29) and above, WRITE_EXTERNAL_STORAGE is not needed for DownloadManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadDesignImage()
        } else {
            // Only check permission for Android 9 and below
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloadDesignImage()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun downloadDesignImage() {
        designImageUrl = currentOrder?.cartProducts?.firstOrNull()?.orderItem?.designType?.overlay ?: ""
        if (designImageUrl.isNotEmpty()) {
            try {
                val request = DownloadManager.Request(Uri.parse(designImageUrl))
                    .setTitle("Design Image Download")
                    .setDescription("Downloading design image for order ${args.orderId}")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "design_${args.orderId}.png")
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

                toast("Downloading design image...")
            } catch (e: Exception) {
                toast("Failed to download: ${e.message}")
            }
        } else {
            toast("No design image available")
        }
    }

    private fun setToDelivered() {
        viewModel.setOrderToDelivered(args.orderId)
    }

    private fun giveReview() {
        toast("Review functionality will be implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
