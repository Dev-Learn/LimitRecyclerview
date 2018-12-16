package dev.tran.nam.sample

import android.annotation.SuppressLint
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
) {

    fun title(): String {
        return id.toString() + " : " + title
    }

    @SuppressLint("SimpleDateFormat")
    fun day(): String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_ago)
        return SimpleDateFormat("dd/MM/yyyy").format(date)
    }

    companion object {
        fun header(id: Int, headerValue: String): ArticleModel {
            return ArticleModel(id = id, isHeader = true, headerValue = headerValue)
        }
    }
}