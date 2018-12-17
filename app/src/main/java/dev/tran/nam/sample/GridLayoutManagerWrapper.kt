package dev.tran.nam.sample

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager


class GridLayoutManagerWrapper : GridLayoutManager {

    constructor(context: Context?, spanCount: Int) : super(context, spanCount)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor(context: Context?, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(
        context,
        spanCount,
        orientation,
        reverseLayout
    )


    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}