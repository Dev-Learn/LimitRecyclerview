package dev.tran.nam.library

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.tran.nam.library.TypeLoad.AFTER
import dev.tran.nam.library.TypeLoad.BEFORE

abstract class BaseAdapterLimit<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = ArrayList<T>()
    private lateinit var iLimitAdapter: ILimitAdapter

    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean
    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    private var mAdapterObserver: RecyclerView.AdapterDataObserver? = null

    override fun getItemCount(): Int {
        return items.size
    }

    fun additional(): Int {
        return if (iLimitAdapter.mTypeLoading == TypeLoading.LOADING && iLimitAdapter.mTypeLoad == BEFORE) 1 else 0
    }

    fun registerDataObserver(adapterObserver: RecyclerView.AdapterDataObserver) {
        if (mAdapterObserver != null) {
            unregisterAdapterDataObserver(mAdapterObserver!!)
        }
        this.mAdapterObserver = adapterObserver
        registerAdapterDataObserver(mAdapterObserver!!)
    }

    open fun unRegisterDataObserver() {
        if (mAdapterObserver != null) {
            unregisterAdapterDataObserver(mAdapterObserver!!)
        }

        mAdapterObserver = null
    }

    fun add(data: List<T>, isInitial: Boolean = false) {
        val limit = iLimitAdapter.mLimit
        if (isInitial) {
            items.addAll(data)
            if (iLimitAdapter.isOver){
                iLimitAdapter.isBefore = true
            }
            notifyDataSetChanged()
            return
        }

        if (data.isEmpty()) {
            if (iLimitAdapter.mTypeLoad == BEFORE) {
                iLimitAdapter.isBefore = false
            } else {
                iLimitAdapter.isAfter = false
            }
            iLimitAdapter.updateLoading(TypeLoading.NONE)
            return
        }

        val dataUpdate = items.toMutableList()

        if (iLimitAdapter.mTypeLoad == AFTER) {
            data.forEachIndexed { _, item ->
                addHeaderAfter(dataUpdate, item)
                dataUpdate.add(item)
            }
            if (dataUpdate.size > limit && iLimitAdapter.isSupportLoadBefore) {
                iLimitAdapter.isBefore = true
                val surplus = dataUpdate.size - limit
                val listItemRemove = ArrayList<T>()
                for (i in 0 until surplus) {
                    dataUpdate[i].let {
                        removeKeyHeader(it)
                        listItemRemove.add(it)
                    }
                }
                dataUpdate.removeAll(listItemRemove)

                addHeaderFirst(dataUpdate,dataUpdate[0])
            }
        } else {
            removeHeader(getItem(0), data[data.size - 1],dataUpdate)
            var indexHeader = 0
            data.forEachIndexed { index, item ->
                indexHeader = addHeaderBefore(dataUpdate, index, item, indexHeader)
                dataUpdate.add(index + indexHeader, item)
            }

            if (dataUpdate.size > limit) {
                iLimitAdapter.isAfter = true
                val surplus = dataUpdate.size - limit
                val size = dataUpdate.size - 1
                val listItemRemove = ArrayList<T>()
                for (i in size downTo size - surplus + 1) {
                    dataUpdate[i].let {
                        removeKeyHeader(it)
                        listItemRemove.add(it)
                    }
                }
                dataUpdate.removeAll(listItemRemove)
            }
        }
        iLimitAdapter.updateLoading(TypeLoading.SUCCESS)
        updateData(dataUpdate)
    }

    @Suppress("UNUSED_PARAMETER")
    open fun addHeaderFirst(listData: MutableList<T>, item: T) {}

    @Suppress("UNUSED_PARAMETER")
    open fun removeHeader(firstItem: T, firstItemResponse: T, listItem: MutableList<T>) {}

    @Suppress("UNUSED_PARAMETER")
    open fun addHeaderAfter(dataUpdate: MutableList<T>, item: T) {}

    @Suppress("UNUSED_PARAMETER")
    open fun addHeaderBefore(
        dataUpdate: MutableList<T>,
        index: Int,
        item: T,
        indexHeader: Int
    ): Int {
        return 0
    }

    @Suppress("UNUSED_PARAMETER")
    open fun removeKeyHeader(it: T) {}

    fun getItem(position: Int): T {
        return items[position]
    }

    fun updateError(errorMessage: String?, retry: () -> Unit) {
        iLimitAdapter.errorMessage = errorMessage
        iLimitAdapter.retry = retry
        iLimitAdapter.updateLoading(TypeLoading.ERROR)
    }

    fun setILimitAdapter(iLimitAdapter: ILimitAdapter) {
        this.iLimitAdapter = iLimitAdapter
    }

    fun getData() : ArrayList<T>{
        return items
    }

    @SuppressLint("StaticFieldLeak")
    private fun updateData(update: List<T>) {
        val oldItems = items
        Log.d(AdapterWrapper.TAG, "Debug DiffUtil : items - " + items.size)
        object : AsyncTask<Void, Void, DiffUtil.DiffResult>() {
            override fun doInBackground(vararg voids: Void): DiffUtil.DiffResult {
                return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return oldItems.size
                    }

                    override fun getNewListSize(): Int {
                        return update.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = oldItems[oldItemPosition]
                        val newItem = update[newItemPosition]
                        return areItemsTheSame(oldItem, newItem)
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = oldItems[oldItemPosition]
                        val newItem = update[newItemPosition]
                        return areContentsTheSame(oldItem, newItem)
                    }
                })
            }

            override fun onPostExecute(diffResult: DiffUtil.DiffResult) {
                items = ArrayList(update)
                Log.d(AdapterWrapper.TAG, "Debug DiffUtil : items - " + items.size)
                diffResult.dispatchUpdatesTo(this@BaseAdapterLimit)
                iLimitAdapter.mTypeLoading = TypeLoading.NONE
            }
        }.execute()
    }


}