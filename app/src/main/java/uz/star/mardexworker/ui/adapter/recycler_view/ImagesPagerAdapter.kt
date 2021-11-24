package uz.star.mardexworker.ui.adapter.recycler_view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.databinding.FragmentPhotoPagerBinding
import uz.star.mardexworker.utils.SingleBlock
import uz.star.mardexworker.utils.extensions.loadPictureUrl

/**
 * Created by Botirali Kozimov on 29.03.2021
 */

class ImagesPagerAdapter :
    ListAdapter<String, ImagesPagerAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {
    private var listenerImageClick: SingleBlock<String>? = null

    companion object {
        val DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return newItem.hashCode() == oldItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(FragmentPhotoPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    fun setOnItemClickListener(function: SingleBlock<String>?) {
        listenerImageClick = function
    }

    inner class ViewHolder(private val binding: FragmentPhotoPagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = getItem(adapterPosition)
            Log.d("TTTT", "item = $item")
            binding.imagePhoto.loadPictureUrl(item)
            binding.imagePhoto.setOnClickListener {
                listenerImageClick?.invoke(item)
            }
        }
    }


}