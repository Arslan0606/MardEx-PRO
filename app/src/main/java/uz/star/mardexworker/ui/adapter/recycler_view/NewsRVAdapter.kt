package uz.star.mardexworker.ui.adapter.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_news.view.*
import kotlinx.android.synthetic.main.item_notification.view.*
import uz.star.mardexworker.model.response.news.toLocalString
import uz.star.mardexworker.databinding.RecyclerItemNewsBinding
import uz.star.mardexworker.model.response.news.NewsData
import uz.star.mardexworker.utils.SingleBlock
import uz.star.mardexworker.utils.extensions.bindItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jasurbek Kurganbaev on 27.06.2021 13:47
 **/
class NewsRVAdapter(val lang: String) : ListAdapter<NewsData, NewsRVAdapter.VH>(ITEM_NEWS_CALBACK) {

    private var itemClickListener: SingleBlock<NewsData>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecyclerItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind()


    companion object {
        var ITEM_NEWS_CALBACK = object : DiffUtil.ItemCallback<NewsData>() {
            override fun areItemsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
                return newItem.V == oldItem.V &&
                        newItem.createdAt == oldItem.createdAt &&
                        newItem.title == oldItem.title &&
                        newItem.description == oldItem.description
            }
        }
    }

    fun setOnItemClickListener(block: SingleBlock<NewsData>) {
        itemClickListener = block
    }

    inner class VH(private val binding: RecyclerItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() = bindItem {
            val data = getItem(adapterPosition)
            val f = SimpleDateFormat("dd.MM.yyyy HH:mm")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = data.createdAt!!
            news_date.text = f.format(calendar.time)
            notification_text.text = data.title?.toLocalString()

            binding.imageViewNotificationNext.setOnClickListener {
                itemClickListener?.invoke(getItem(adapterPosition))
            }

            binding.layoutItemNews.setOnClickListener {
                itemClickListener?.invoke(getItem(adapterPosition))
            }
        }
    }


}