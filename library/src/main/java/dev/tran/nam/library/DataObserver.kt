package dev.tran.nam.library

import androidx.recyclerview.widget.RecyclerView

class DataObserver(val mAdapter : AdapterWrapper) : RecyclerView.AdapterDataObserver(){

    override fun onChanged() {
        mAdapter.notifyDataSetChanged()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (mAdapter.itemCount == 0){
            mAdapter.notifyDataSetChanged()
            return
        }
        mAdapter.notifyItemRangeRemoved(positionStart, itemCount)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        if (mAdapter.itemCount == 0){
            mAdapter.notifyDataSetChanged()
            return
        }
        mAdapter.notifyItemMoved(fromPosition,toPosition)
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (mAdapter.itemCount == 0){
            mAdapter.notifyDataSetChanged()
            return
        }

        mAdapter.notifyItemRangeInserted(positionStart,itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        if (mAdapter.itemCount == 0){
            mAdapter.notifyDataSetChanged()
            return
        }
        mAdapter.notifyItemRangeChanged(positionStart,itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        if (mAdapter.itemCount == 0){
            mAdapter.notifyDataSetChanged()
            return
        }
        mAdapter.notifyItemRangeChanged(positionStart,itemCount,payload)
    }
}