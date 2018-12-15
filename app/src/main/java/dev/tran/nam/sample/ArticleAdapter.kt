package dev.tran.nam.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.squareup.picasso.Picasso
import dev.tran.nam.library.BaseAdapterLimit

class ArticleAdapter : BaseAdapterLimit<ArticleModel>(){

    override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
        return oldItem.title == newItem.title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != NO_POSITION && holder is ArticleViewHolder)
            holder.bind(items[position])
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

        fun bind(item : ArticleModel){
            title.text = item.title()
            author.text = item.author.toString()
            time.text = item.time_ago
            decription.text = item.description
            Picasso.get().load(item.image).placeholder(R.drawable.progress_animation).error(R.drawable.image_error).into(image)
        }

    }

}