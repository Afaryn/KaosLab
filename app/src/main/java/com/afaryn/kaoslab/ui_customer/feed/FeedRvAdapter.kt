package com.afaryn.kaoslab.ui_customer.feed

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan

class FeedRvAdapter {

    val username = "octagonsdesign"
    val caption = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec semper sapien."

    val spannable = SpannableString("$username $caption").apply {
        setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            username.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

}