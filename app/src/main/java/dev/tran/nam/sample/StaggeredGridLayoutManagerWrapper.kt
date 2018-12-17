package dev.tran.nam.sample

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager


class StaggeredGridLayoutManagerWrapper : StaggeredGridLayoutManager {

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}