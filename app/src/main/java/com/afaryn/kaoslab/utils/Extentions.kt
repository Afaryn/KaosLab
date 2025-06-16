package com.afaryn.kaoslab.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
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
    Glide.with(this.context).load(url).into(this)
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
