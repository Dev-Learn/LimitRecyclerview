package dev.tran.nam.sample

import android.os.Parcel
import android.os.Parcelable

data class AuthorArticleModel(val id: Int, val author_name: String, val avarta: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(author_name)
        writeString(avarta)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AuthorArticleModel> = object : Parcelable.Creator<AuthorArticleModel> {
            override fun createFromParcel(source: Parcel): AuthorArticleModel = AuthorArticleModel(source)
            override fun newArray(size: Int): Array<AuthorArticleModel?> = arrayOfNulls(size)
        }
    }
}