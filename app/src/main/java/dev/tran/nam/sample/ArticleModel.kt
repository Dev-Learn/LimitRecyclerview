package dev.tran.nam.sample

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat

data class ArticleModel(
    val id: Int = -1,
    val title: String = "",
    val image: String = "",
    val description: String = "",
    val time_ago: String? = null,
    val author: AuthorArticleModel? = null,
    val detailArticle: DetailArticleModel? = null,
    val isHeader: Boolean = false, var headerValue: String?
) : Parcelable {

    fun title(): String {
        return id.toString() + " : " + title
    }

    @SuppressLint("SimpleDateFormat")
    fun day(): String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_ago)
        return SimpleDateFormat("dd/MM/yyyy").format(date)
    }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<AuthorArticleModel>(AuthorArticleModel::class.java.classLoader),
        source.readParcelable<DetailArticleModel>(DetailArticleModel::class.java.classLoader),
        1 == source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeString(image)
        writeString(description)
        writeString(time_ago)
        writeParcelable(author, 0)
        writeParcelable(detailArticle, 0)
        writeInt((if (isHeader) 1 else 0))
        writeString(headerValue)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArticleModel> = object : Parcelable.Creator<ArticleModel> {
            override fun createFromParcel(source: Parcel): ArticleModel = ArticleModel(source)
            override fun newArray(size: Int): Array<ArticleModel?> = arrayOfNulls(size)
        }

        fun header(id: Int, headerValue: String): ArticleModel {
            return ArticleModel(id = id, isHeader = true, headerValue = headerValue)
        }
    }
}