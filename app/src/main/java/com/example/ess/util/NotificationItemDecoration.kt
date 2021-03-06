package com.example.ess.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NotificationItemDecoration() : RecyclerView.ItemDecoration() {
    val space = 20
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space
        }
    }
}