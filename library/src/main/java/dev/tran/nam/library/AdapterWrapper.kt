package dev.tran.nam.library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import dev.tran.nam.library.TypeLoad.AFTER
import dev.tran.nam.library.TypeLoading.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager



class AdapterWrapper(private val mAdapter: BaseAdapterLimit<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ILimitAdapter {

    companion object {
        val TAG = AdapterWrapper::class.qualifiedName
        private const val ITEM_LOADING = 3
    }

    init {
        mAdapter.setILimitAdapter(this)
    }

    private var onAdapterWrapperListener : OnAdapterWrapperListener? = null

    override var mLimit = 0

    override var isSupportLoadBefore: Boolean = true

    override var mTypeLoad: TypeLoad = AFTER

    override var mTypeLoading: TypeLoading = NONE

    override var isBefore: Boolean = false

    override var isAfter: Boolean = true

    override val isOver: Boolean
        get() = isSupportLoadBefore && mAdapter.itemCount == mLimit || mAdapter.itemCount == mLimit + 1

    override var errorMessage: String? = null

    override var retry: (() -> Unit)? = null

    override val isStaggeredGridLayoutManager: Boolean
        get() = onAdapterWrapperListener?.isStaggeredGridLayoutManager ?: false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_LOADING) {
            return LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
        }
        return mAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        if (mAdapter.itemCount > 0) {
            val additional = if (isLoading()) 1 else 0
            return mAdapter.itemCount + additional
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "getItemViewType - onBindViewHolder : position : $position")
        Log.d(
            TAG,
            "getItemViewType - onBindViewHolder : getItemViewType(position) == ITEM_LOADING : ${getItemViewType(position) == ITEM_LOADING}"
        )
        if (position == NO_POSITION)
            return
        if (getItemViewType(position) == ITEM_LOADING) {
            if (holder is LoadingViewHolder) {
                if (isStaggeredGridLayoutManager){
                    val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                    layoutParams.isFullSpan = true
                }
                holder.bind(mTypeLoading, errorMessage, retry)
            }
        } else {
            mAdapter.onBindViewHolder(holder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "getItemViewType : position : $position")
        Log.d(TAG, "getItemViewType : isLoading() : " + isLoading())
        Log.d(TAG, "getItemViewType : itemCount : ${mAdapter.itemCount}")
        return if (isLoading() && (position == 0 || position == mAdapter.itemCount))
            ITEM_LOADING
        else
            mAdapter.getItemViewType(position)
    }

    fun setOnAdapterWrapperListener(onAdapterWrapperListener : OnAdapterWrapperListener){
        this.onAdapterWrapperListener = onAdapterWrapperListener
    }

    fun isTotality(position: Int): Boolean {
        return getItemViewType(position) == ITEM_LOADING || mAdapter.checkTotality(position)
    }

    private fun isLoading(): Boolean {
        return mTypeLoading == LOADING || mTypeLoading == ERROR
    }

    override fun updateLoading(type: TypeLoading) {
        mTypeLoading = type
        when (mTypeLoading) {
            LOADING -> {
                if (mTypeLoad == AFTER) {
                    notifyItemInserted(itemCount)
                } else {
                    notifyItemInserted(0)
                }
            }
            SUCCESS -> {
                if (mTypeLoad != AFTER) {
                    notifyItemRemoved(0)
                }
            }
            ERROR -> {
                if (mTypeLoad == AFTER) {
                    notifyItemChanged(itemCount - 1)
                } else {
                    notifyItemChanged(0)
                }
            }

            NONE -> {
                if (mTypeLoad == AFTER) {
                    notifyItemRemoved(itemCount - 1)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }
        Log.d(TAG, "updateLoading - $mTypeLoading")
    }

    fun getItemLasted(): Any? {
        return if (mAdapter.itemCount > 0) mAdapter.getItem(mAdapter.itemCount - 1) else null
    }

    fun getItemFirst(): Any? {
        return if (mAdapter.itemCount > 0) mAdapter.getItem(0) else null
    }

    private class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mProgress: ProgressBar by lazy {
            itemView.findViewById<ProgressBar>(R.id.progress)
        }

        val tvError: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_error)
        }
        val btRetry: Button by lazy {
            itemView.findViewById<Button>(R.id.bt_error)
        }

        fun bind(type: TypeLoading, message: String? = null, retry: (() -> Unit)?) {
            when (type) {
                LOADING -> {
                    mProgress.visibility = View.VISIBLE
                    tvError.visibility = View.GONE
                    btRetry.visibility = View.GONE
                }
                SUCCESS, NONE -> {
                    mProgress.visibility = View.GONE
                    tvError.visibility = View.GONE
                    btRetry.visibility = View.GONE
                }
                ERROR -> {
                    mProgress.visibility = View.GONE
                    tvError.visibility = View.VISIBLE
                    btRetry.visibility = View.VISIBLE

                    message?.let {
                        tvError.text = it
                    }

                    btRetry.setOnClickListener {
                        retry?.invoke()
                    }
                }
            }
        }
    }

    interface OnAdapterWrapperListener{
        val isStaggeredGridLayoutManager : Boolean
    }

}