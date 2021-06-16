package com.example.ess.ui.common.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FeedItemDecoration : RecyclerView.ItemDecoration() {
    private val space = 20
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space
        }
        outRect.bottom = space
    }
}