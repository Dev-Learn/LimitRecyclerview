package dev.tran.nam.sample

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.tran.nam.library.BaseAdapterLimit

class ArticleAdapter : BaseAdapterLimit<ArticleModel>() {

    companion object {
        const val HEADER = 4
    }

    private val listDay = mutableListOf<String>()

    override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
        return oldItem.title == newItem.title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER) {
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article_header, parent, false))
        } else {
            ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> {
                if (holder is HeaderViewHolder) {
                    holder.bind(getItem(position - additional()).headerValue)
                }
            }
            else -> {
                if (holder is ArticleViewHolder)
                    holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position - additional()).isHeader) HEADER else super.getItemViewType(position)
    }

    fun updateData(isInitial: Boolean, data: List<ArticleModel>) {
        if (isInitial) {
            val listItem = mutableListOf<ArticleModel>()
            for (item in data) {
                val date = item.day()
                if (!listDay.contains(date)) {
                    listItem.add(ArticleModel.header(item.id, date))
                    listDay.add(date)
                }

                listItem.add(item)
            }

            add(data = listItem, isInitial = isInitial)
        } else {
            add(data)
        }
    }

    override fun addHeaderAfter(
        dataUpdate: MutableList<ArticleModel>,
        item: ArticleModel
    ) {
        if (!item.isHeader){
            val date = item.day()
            if (!listDay.contains(date)) {
                dataUpdate.add(
                    ArticleModel.header(item.id, date)
                )
                listDay.add(date)
            }
        }
    }

    override fun addHeaderFirst(listData: MutableList<ArticleModel>, item: ArticleModel) {
        item.let {
            Log.d("ArticleAdapter", "addHeaderFirst - isHeader : " + it.isHeader)
            if (!it.isHeader) {
                val date = it.day()
                Log.d("ArticleAdapter", "addHeaderFirst - date : " + date)
                if (!listDay.contains(date)) {
                    listData.add(
                        0,
                        ArticleModel.header(item.id, date)
                    )

                    Log.d("ArticleAdapter", "addHeaderFirst - Insert")
                    listDay.add(date)
                }
            }
        }
    }

    override fun addHeaderBefore(
        dataUpdate: MutableList<ArticleModel>,
        index: Int,
        item: ArticleModel,
        indexHeader: Int
    ): Int {
        var position = indexHeader
        val date = item.day()
        if (!listDay.contains(date)) {
            dataUpdate.add(
                index + indexHeader,
                ArticleModel.header(item.id, date)
            )
            position += 1
            listDay.add(date)
        }
        return position
    }

    override fun removeHeader(
        firstItem: ArticleModel,
        firstItemResponse: ArticleModel,
        listItem: MutableList<ArticleModel>
    ) {
        Log.d("ArticleAdapter", "removeHeader - firstItem.isHeader" + firstItem.isHeader)
        if (firstItem.isHeader) {
            Log.d("ArticleAdapter", "removeHeader - firstItem.headerValue : " + firstItem.headerValue)
            firstItem.headerValue?.let {
                Log.d("ArticleAdapter", "removeHeader - firstItemResponse.day() : " + firstItemResponse.day())
                if (it == firstItemResponse.day()) {
                    listDay.remove(it)
                    listItem.remove(firstItem)
                }
            }
        }
    }

    override fun removeKeyHeader(it: ArticleModel) {
        if (it.isHeader) {
            listDay.remove(it.headerValue)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val date: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_date)
        }

        fun bind(day: String?) {
            Log.d("HeaderViewHolder", "Header " + "${day}")
            date.text = day
        }
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_title)
        }

        private val author: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_author)
        }

        private val time: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_time)
        }

        private val decription: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_description)
        }

        private val image: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.iv_article)
        }

        fun bind(item: ArticleModel) {
            title.text = item.title()
            author.text = item.author?.author_name
            time.text = item.time_ago
            decription.text = item.description
            Picasso.get().load(item.image).placeholder(R.drawable.progress_animation).error(R.drawable.image_error)
                .into(image)
        }

    }

}