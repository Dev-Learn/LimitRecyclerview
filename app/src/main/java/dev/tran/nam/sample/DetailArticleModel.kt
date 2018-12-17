package dev.tran.nam.sample

import android.os.Parcel
import android.os.Parcelable

data class DetailArticleModel(val id: Int, val detail: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(detail)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DetailArticleModel> = object : Parcelable.Creator<DetailArticleModel> {
            override fun createFromParcel(source: Parcel): DetailArticleModel = DetailArticleModel(source)
            override fun newArray(size: Int): Array<DetailArticleModel?> = arrayOfNulls(size)
        }
    }
}