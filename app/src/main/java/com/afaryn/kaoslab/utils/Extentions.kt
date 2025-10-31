package com.afaryn.kaoslab.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.DialogRateBinding
import com.afaryn.kaoslab.presentation.ui_designer.DesignerActivity
import com.afaryn.kaoslab.presentation.ui_owner.OwnerActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun validateEmail(email: String): Validation {
    if (email.isEmpty()) {
        return Validation.Failed("Email tidak boleh kosong")
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return Validation.Failed("Harap isi dengan email yang valid")
    }

    return Validation.Success
}

fun validatePassword(password: String, passwordConfirmation: String?): Validation {
    if (password.isEmpty()) {
        return Validation.Failed("Password tidak boleh kosong")
    }

    if (passwordConfirmation != null) {
        if (password != passwordConfirmation) {
            return Validation.Failed("Password tidak sesuai")
        }
    }

    if (password.length < 6) {
        return Validation.Failed("Password harus terdiri dari 6 huruf atau lebih")
    }

    return Validation.Success
}

fun Fragment.toast(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun confirmDialog(
    context: Context,
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String,
    positiveAction: () -> Unit = { },
) {
    AlertDialog.Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButton) { _, _ ->
            positiveAction()
        }
        setNegativeButton(negativeButton) { dialog, _ ->
            dialog.dismiss()
        }
        create()
        show()
    }
}

fun emailVerificationDialog(
    context: Context,
    title: String,
    message: String,
    openEmailAction: () -> Unit,
    goToLoginAction: () -> Unit
) {
    AlertDialog.Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setCancelable(false)
        setPositiveButton("Open Email App") { _, _ ->
            openEmailAction()
        }
        setNegativeButton("Go to Login") { _, _ ->
            goToLoginAction()
        }
        create()
        show()
    }
}

fun emailNotVerifiedDialog(
    context: Context,
    email: String,
    password: String,
    onResendVerification: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog.Builder(context).apply {
        setTitle("Email Not Verified")
        setMessage("Your email address has not been verified yet. Please check your email and verify your account, or resend the verification email.")
        setCancelable(false)
        setPositiveButton("Resend Verification") { _, _ ->
            onResendVerification(email, password)
        }
        setNegativeButton("Cancel") { _, _ ->
            onCancel()
        }
        setNeutralButton("Open Email App") { _, _ ->
            val emailIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(emailIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }
        create()
        show()
    }
}

fun forgotPasswordDialog(
    context: Context,
    onSendReset: (String) -> Unit
) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_forgot_password, null)
    val editTextEmail = dialogView.findViewById<android.widget.EditText>(R.id.editTextEmail)

    AlertDialog.Builder(context).apply {
        setTitle("Reset Password")
        setView(dialogView)
        setPositiveButton("Send Reset Email") { _, _ ->
            val email = editTextEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                onSendReset(email)
            } else {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
        setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        create()
        show()
    }
}

fun Fragment.hideBottomNavOwner() {
    val appBar: BottomAppBar = (activity as OwnerActivity).findViewById(R.id.menuBottom)
    val bottomNavView: BottomNavigationView = (activity as OwnerActivity).findViewById(R.id.bottom_navigation)

    appBar.visibility = View.GONE
    bottomNavView.visibility = View.GONE
}

fun Fragment.showBottomNavOwner() {
    val appBar: BottomAppBar = (activity as OwnerActivity).findViewById(R.id.menuBottom)
    val bottomNavView: BottomNavigationView = (activity as OwnerActivity).findViewById(R.id.bottom_navigation)

    appBar.visibility = View.VISIBLE
    bottomNavView.visibility = View.VISIBLE
}

fun Fragment.hideBottomNavDesigner() {
    val appBar: BottomAppBar = (activity as DesignerActivity).findViewById(R.id.menuBottom)
    val bottomNavView: BottomNavigationView = (activity as DesignerActivity).findViewById(R.id.bottom_navigation)

    appBar.visibility = View.GONE
    bottomNavView.visibility = View.GONE
}

fun Fragment.hideBottomNav() {
    val activity = requireActivity()
    val appBar: BottomAppBar? = activity.findViewById(R.id.menuBottom)
    val bottomNavView: BottomNavigationView? = activity.findViewById(R.id.bottom_navigation)

    appBar?.visibility = View.GONE
    bottomNavView?.visibility = View.GONE
}

fun Fragment.showBottomNavDesigner() {
    val appBar: BottomAppBar = (activity as DesignerActivity).findViewById(R.id.menuBottom)
    val bottomNavView: BottomNavigationView = (activity as DesignerActivity).findViewById(R.id.bottom_navigation)

    appBar.visibility = View.VISIBLE
    bottomNavView.visibility = View.VISIBLE
}

fun Fragment.showBottomNav() {
    val activity = activity ?: return
    val appBar: BottomAppBar? = activity.findViewById(R.id.menuBottom)
    val bottomNavView: BottomNavigationView? = activity.findViewById(R.id.bottom_navigation)

    appBar?.visibility = View.VISIBLE
    bottomNavView?.visibility = View.VISIBLE
}

fun Int.toCurrencyFormat(): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return numberFormat.format(this)
}

fun Date.toToday(): String {
    val currentDate = Calendar.getInstance().time
    val diffInMillis = currentDate.time - this.time

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = diffInMillis

    val years = calendar.get(Calendar.YEAR) - 1970
    val months = calendar.get(Calendar.MONTH)
    val days = calendar.get(Calendar.DAY_OF_MONTH) - 1

    return "$years Tahun $months Bulan $days Hari"
}


fun parseDateString(dateString: Date): String {
    return try {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        "Tidak ada jadwal"
    }
}

fun parseDate(dateString: Date): String {
    return try {
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        "Tidak ada jadwal"
    }
}

fun stringToDate(date: Date): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

@SuppressLint("InflateParams", "MissingInflatedId")
//fun Activity.setupDeleteDialog(
//    title: String,
//    message: String,
//    btnActionText: String,
//    onYesClick: () -> Unit
//) {
//    val dialog = Dialog(this, android.R.style.Theme_Dialog)
//    val view = layoutInflater.inflate(R.layout.delete_pasien_dialog, null)
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.setContentView(view)
//    dialog.window?.setGravity(Gravity.CENTER)
//    dialog.window?.setLayout(
//        WindowManager.LayoutParams.MATCH_PARENT,
//        WindowManager.LayoutParams.WRAP_CONTENT
//    )
//    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.show()
//
//    val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
//    val tvMessage = view.findViewById<TextView>(R.id.tv_dialog_message)
//    tvTitle.text = title
//    tvMessage.text = message
//
//    val btnDismiss = view.findViewById<Button>(R.id.btn_dialog_dismiss)
//    val btnYes = view.findViewById<Button>(R.id.btn_dialog_yes)
//    btnYes.text = btnActionText
//
//    btnDismiss.setOnClickListener {
//        dialog.dismiss()
//    }
//    btnYes.setOnClickListener {
//        onYesClick()
//        dialog.dismiss()
//    }
//}

fun translateDateToIndonesian(inputString: Date): String {
    return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(inputString)
}

fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    return dateFormat.format(date)
}

fun isSameDay(date: Date): Boolean {
    val today = Date()

    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()

    cal1.time = date
    cal2.time = today

    return cal1.time == cal2.time
}

//fun getClosestDate(listReservasi: List<Reservation>): Reservation? {
//    val currentDate = Date()
//    val currentDateMidnight = Calendar.getInstance().apply {
//        time = currentDate
//        set(Calendar.HOUR_OF_DAY, 0)
//        set(Calendar.MINUTE, 0)
//        set(Calendar.SECOND, 0)
//        set(Calendar.MILLISECOND, 0)
//    }.time
//
//    var closestReservation: Reservation? = null
//    var minDifference = Long.MAX_VALUE
//    var closestReservationsOnSameDay = mutableListOf<Reservation>()
//
//    Log.d("getClosestDate", "Current Date (midnight): ${currentDateMidnight.toString()}")
//
//    for (reservasi in listReservasi) {
//        reservasi.date?.let { reservationDate ->
//            val reservationDateMidnight = Calendar.getInstance().apply {
//                time = reservationDate
//                set(Calendar.HOUR_OF_DAY, 0)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//                set(Calendar.MILLISECOND, 0)
//            }.time
//
//            val difference = abs(currentDateMidnight.time - reservationDateMidnight.time)
//            Log.d("getClosestDate", "Reservation Date (midnight): ${reservationDateMidnight.toString()}, Difference: $difference")
//            if (difference < minDifference) {
//                minDifference = difference
//                closestReservationsOnSameDay.clear()
//                closestReservationsOnSameDay.add(reservasi)
//                Log.d("getClosestDate", "New closest reservation: ${reservasi.toString()} with difference $difference")
//            } else if (difference == minDifference) {
//                closestReservationsOnSameDay.add(reservasi)
//            } else {
//
//            }
//        }
//    }
//    closestReservation = closestReservationsOnSameDay.minByOrNull { it.queueNumber!! }
//    Log.d("getClosestDate", "Closest reservation: ${closestReservation?.toString() ?: "None"}")
//
//    return closestReservation
//}


fun ImageView.glide(url: String) {
    Glide.with(this.context).load(url)
        .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}

fun ImageView.glide(uri: Uri) {
    Glide.with(this.context).load(uri)
        .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
private const val MAXIMAL_SIZE = 1000000

fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTemporaryFile(context)

    contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    val correctlyOrientedBitmap = getCorrectlyOrientedImage(myFile)
    val reducedFile = reduceFileImage(correctlyOrientedBitmap, myFile)

    return reducedFile
}

fun getCorrectlyOrientedImage(file: File): Bitmap {
    val bitmap = BitmapFactory.decodeFile(file.path)
    val exif = ExifInterface(file.path)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun reduceFileImage(bitmap: Bitmap, file: File): File {
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE && compressQuality > 0)

    // Simpan gambar yang sudah dikompresi ke file
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
    }
    return file
}

fun successDialog(
    context: Context,
    title: String,
    message: String,
    positiveAction: () -> Unit = { },
) {
    val dialog = AlertDialog.Builder(context).create()
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_success, null)
    dialog.setView(view)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(false)

    val tvTitle = view.findViewById<TextView>(R.id.tv_success_title)
    val tvMessage = view.findViewById<TextView>(R.id.tv_success_message)
    val btnOk = view.findViewById<Button>(R.id.btn_ok)
    val ivSpinner = view.findViewById<ImageView>(R.id.iv_spinner)
    val ivCheckmark = view.findViewById<ImageView>(R.id.iv_checkmark)

    tvTitle.text = title
    tvMessage.text = message

    // Start spinner animation
    val rotateAnimation = android.view.animation.RotateAnimation(
        0f, 360f,
        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        duration = 1000
        repeatCount = android.view.animation.Animation.INFINITE
        interpolator = android.view.animation.LinearInterpolator()
    }
    ivSpinner.startAnimation(rotateAnimation)

    // Show checkmark after animation completes and enable button
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        ivSpinner.clearAnimation()
        ivSpinner.visibility = View.GONE
        ivCheckmark.visibility = View.VISIBLE

        // Animate checkmark scale
        val scaleAnimation = android.view.animation.ScaleAnimation(
            0f, 1f, 0f, 1f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            interpolator = android.view.animation.OvershootInterpolator()
        }
        ivCheckmark.startAnimation(scaleAnimation)

        btnOk.isEnabled = true
        btnOk.alpha = 1f
    }, 1500)

    btnOk.setOnClickListener {
        positiveAction()
        dialog.dismiss()
    }

    dialog.show()
}

inline fun <reified T : Parcelable> Intent?.getParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this?.getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        (this?.getParcelableExtra(key))
    }
}

fun Int.formatRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(toDouble())
}

fun Int?.orZero(): Int = this ?: 0

fun Long.toDateString(): String {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.format(Date(this))
}

fun Date.toDateString(): String {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.format(this)
}

fun Timestamp.toMonthDay(): String {
    val format = SimpleDateFormat("MMM d", Locale.getDefault())
    return format.format(toDate())
}

fun MaterialButton.setLoading(isLoading: Boolean, placeholder: String) {
    isEnabled = !isLoading
    text = if (!isLoading) placeholder else "Loading..."
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.downloadDesign(designUrl: String, onFinished: () -> Unit, onError: (String?) -> Unit) {
    try {
        val appName = getString(R.string.app_name)
        val fileName = designUrl.substringAfterLast("/")

        val request = DownloadManager.Request(designUrl.toUri())
            .setTitle(fileName)
            .setDescription("Downloading design...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$appName/$fileName"
            )

        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)

        onFinished.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        onError.invoke(e.message)
    }
}

fun Double?.toIdrFormat(): String {
    if (this == null) return "Rp 0,-"
    val localeID = Locale("in", "ID") // Create a Locale for Indonesia
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.maximumFractionDigits = 0 // Remove decimal part, e.g., ",00"
    return numberFormat.format(this).replace("Rp", "Rp ") // Add a space after Rp for better readability
}

fun Long.formatElapsedTime(): String {
    val diffMillis = System.currentTimeMillis() - this

    val minutes = diffMillis / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> {
            val remainingHours = hours % 24
            if (remainingHours > 0) "${days}d ${remainingHours}h" else "${days}d"
        }
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "Just now"
    }
}

fun Long.isToday(): Boolean {
    val currentCal = Calendar.getInstance()
    val targetCal = Calendar.getInstance().apply { timeInMillis = this@isToday }

    return currentCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) &&
            currentCal.get(Calendar.DAY_OF_YEAR) == targetCal.get(Calendar.DAY_OF_YEAR)
}

fun Activity.showRatingDialog(onRate: (Float) -> Unit) {
    val dialogBinding = DialogRateBinding.inflate(layoutInflater)

    val dialog = MaterialAlertDialogBuilder(this)
        .setView(dialogBinding.root)
        .setCancelable(true)
        .create()

    dialogBinding.btnRate.setOnClickListener {
        val rating = dialogBinding.ratingBar.rating
        onRate(rating)
        dialog.dismiss()
    }

    dialog.show()
}