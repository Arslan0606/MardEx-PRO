package uz.star.mardexworker.ui.adapter.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.databinding.FragmentPhotoPagerBinding
import uz.star.mardexworker.utils.SingleBlock
import uz.star.mardexworker.utils.extensions.loadImageUri
import uz.star.mardexworker.utils.extensions.loadPictureUrl

/**
 * Created by Kurganbaev Jasurbek on 20.04.2021 12:04
 **/
class CameraPicturesAdapter :
    ListAdapter<String, CameraPicturesAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {

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


    inner class ViewHolder(private val binding: FragmentPhotoPagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = getItem(adapterPosition)
            binding.imagePhoto.loadImageUri(item.toUri())

        }
    }
}