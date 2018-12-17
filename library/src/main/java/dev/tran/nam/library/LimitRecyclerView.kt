package dev.tran.nam.library

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

open class LimitRecyclerView : RecyclerView, AdapterWrapper.OnAdapterWrapperListener {

    companion object {
        val TAG = LimitRecyclerView::class.qualifiedName
        private const val NUMBER_TO_LOAD = 1
        private const val LIMIT = 100
    }

    private var mNumberToLoad = NUMBER_TO_LOAD
    private var mLimit = LIMIT
    private var isSupportLoadBefore = true

    private var mAdapterWrapper: AdapterWrapper? = null
    private var onLoadListener: OnLoadListener<Any>? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.LimitRecyclerView, defStyle, 0)

        mNumberToLoad = a.getInt(R.styleable.LimitRecyclerView_number_to_load, NUMBER_TO_LOAD)
        mLimit = a.getInt(R.styleable.LimitRecyclerView_limit, LIMIT)
        isSupportLoadBefore = a.getBoolean(R.styleable.LimitRecyclerView_isSupportLoadBefore, true)

        a.recycle()
    }

    override val isStaggeredGridLayoutManager: Boolean
        get() = layoutManager is StaggeredGridLayoutManager

    @Suppress("unused")
    fun setOnLoadListener(onLoadListener: OnLoadListener<Any>) {
        this.onLoadListener = onLoadListener
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAdapter(adapter: Adapter<*>?) {
        adapter?.let {
            mAdapterWrapper = AdapterWrapper(it as BaseAdapterLimit<Any>)
            mAdapterWrapper?.mLimit = mLimit
            mAdapterWrapper?.isSupportLoadBefore = isSupportLoadBefore
            mAdapterWrapper?.setOnAdapterWrapperListener(this)
            val dataObserver = DataObserver(mAdapterWrapper!!)
            super.setAdapter(mAdapterWrapper)
            it.registerDataObserver(dataObserver)
        }
    }

    private var mOnScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager
            layoutManager?.let { it ->
                val totalItemCount = it.itemCount

                val lastVisibleItemPosition = when (layoutManager) {
                    is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                    is StaggeredGridLayoutManager -> {
                        val into = IntArray(layoutManager.spanCount)
                        layoutManager.findLastVisibleItemPositions(into)
                        findMax(into)
                    }
                    else -> (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                }

                val firstVisibleItemPosition = when (layoutManager) {
                    is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                    is StaggeredGridLayoutManager -> {
                        val into = IntArray(layoutManager.spanCount)
                        layoutManager.findFirstVisibleItemPositions(into)
                        findMin(into)
                    }
                    else -> (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                }

                Log.d(TAG, "lastVisibleItemPosition : $lastVisibleItemPosition")
                Log.d(TAG, "firstVisibleItemPosition : $firstVisibleItemPosition")

                val isOver = mAdapterWrapper?.isOver!!
                val loadingType = mAdapterWrapper?.mTypeLoading!!
                val isAfter = mAdapterWrapper?.isAfter!!
                val isBefore = mAdapterWrapper?.isBefore!!

                Log.d(TAG, "isOver: $isOver")
                Log.d(TAG, "mTypeLoading: $loadingType")
                Log.d(TAG, "isAfter: $isAfter")
                Log.d(TAG, "isBefore: $isBefore")

                if (loadingType == TypeLoading.NONE && totalItemCount <= lastVisibleItemPosition + mNumberToLoad && isAfter) {
                    mAdapterWrapper?.mTypeLoad = TypeLoad.AFTER
                    mAdapterWrapper?.updateLoading(TypeLoading.LOADING)
                    onLoadListener?.onLoadMore((mAdapterWrapper?.getItemLasted()))
                    return
                }

                if (loadingType == TypeLoading.NONE && firstVisibleItemPosition == mNumberToLoad && isBefore && isOver) {
                    mAdapterWrapper?.mTypeLoad = TypeLoad.BEFORE
                    mAdapterWrapper?.updateLoading(TypeLoading.LOADING)
                    onLoadListener?.onLoadBefore(mAdapterWrapper?.getItemFirst())
                }
            }
        }
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        if (layout is GridLayoutManager) layout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mAdapterWrapper != null && mAdapterWrapper!!.isTotality(position)) {
                    layout.spanCount
                } else 1
            }
        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        return lastPositions.max() ?: lastPositions[0]
    }

    private fun findMin(firstPositions: IntArray): Int {
        return firstPositions.min() ?: firstPositions[0]
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnScrollListener(mOnScrollListener)
    }

    override fun onDetachedFromWindow() {
        removeOnScrollListener(mOnScrollListener)
        super.onDetachedFromWindow()
    }

    interface OnLoadListener<T> {
        fun onLoadBefore(firstItem: T?)
        fun onLoadMore(lastItem: T?)
    }

}
