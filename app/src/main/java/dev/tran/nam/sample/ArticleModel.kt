package dev.tran.nam.sample

data class ArticleModel(
    val id: Int = -1,
    val title: String = "",
    val image: String = "",
    val description: String = "",
    val time_ago: String = "",
    val author: AuthorArticleModel? = null,
    val detailArticle: DetailArticleModel? = null
) {

    fun title(): String {
        return id.toString() + " : " + title
    }
}