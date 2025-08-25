package com.afaryn.kaoslab.ui_owner.my_shop.product_template

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NoScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        val heightMode = android.view.View.MeasureSpec.getMode(heightSpec)
        val heightSize = android.view.View.MeasureSpec.getSize(heightSpec)

        // Measure the RecyclerView with wrap_content behavior
        var height = 0
        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            height += getDecoratedMeasuredHeight(child)
            removeAndRecycleView(child, recycler)
        }

        val finalHeight = when (heightMode) {
            android.view.View.MeasureSpec.EXACTLY -> heightSize
            android.view.View.MeasureSpec.AT_MOST -> minOf(height, heightSize)
            else -> height
        }

        setMeasuredDimension(
            android.view.View.MeasureSpec.getSize(widthSpec),
            finalHeight
        )
    }
}
