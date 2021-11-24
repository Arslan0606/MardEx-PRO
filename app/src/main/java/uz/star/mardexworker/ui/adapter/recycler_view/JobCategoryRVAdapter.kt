package uz.star.mardexworker.ui.adapter.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.databinding.CategoryItemRecyclerBinding
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.jobs_data.JobData
import uz.star.mardexworker.utils.*
import uz.star.mardexworker.utils.extensions.loadImageUrl
import uz.star.mardexworker.utils.views.goneView
import uz.star.mardexworker.utils.views.showView

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

class JobCategoryRVAdapter(val lang: String) :
    ListAdapter<JobCategory, JobCategoryRVAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {

    private var listenerSelectCategory: DoubleBlock<List<JobData>, String?>? = null
    var lastSelectedIndex = 0
    private var listBackgrounds: ArrayList<View> = arrayListOf()

    companion object {
        var DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<JobCategory>() {
            override fun areItemsTheSame(oldItem: JobCategory, newItem: JobCategory) =
                newItem == oldItem

            override fun areContentsTheSame(
                oldItem: JobCategory,
                newItem: JobCategory
            ) = newItem.categoryTitle == oldItem.categoryTitle
                    && newItem.pic == oldItem.pic
                    && newItem.hashCode() == oldItem.hashCode()
        }
    }

    inner class ViewHolder(private val binding: CategoryItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            listBackgrounds.add(binding.selected)

            binding.category.setOnClickListener {
                if (lastSelectedIndex == adapterPosition)
                    return@setOnClickListener

                val data = getItem(adapterPosition)
                getItem(lastSelectedIndex).selected = false
                data.selected = true

                removeBgLines()
                binding.selected.showView(true)

                lastSelectedIndex = adapterPosition
                val dataTitle = data.categoryTitle
                val title =
                    if (lang == LANG_UZ) dataTitle.uz else if (lang == LANG_KRILL) dataTitle.uz_kr else dataTitle.ru
                listenerSelectCategory?.invoke(data.jobs, title)
            }
        }

        fun bind() {
            if (getItem(adapterPosition).selected) {
                binding.selected.showView(true)
            } else
                binding.selected.goneView()

            val data = getItem(adapterPosition)

            val dataTitle = data.categoryTitle
            val title =
                if (lang == LANG_UZ) dataTitle.uz else if (lang == LANG_KRILL) dataTitle.uz_kr else dataTitle.ru
            binding.name.text = title

            data.pic?.let { pic ->
                binding.image.loadImageUrl(pic)
            }
        }
    }

    private fun removeBgLines() {
        listBackgrounds.forEach {
            it.goneView()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        CategoryItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    fun setOnCategoryItemClickListener(f: DoubleBlock<List<JobData>, String?>) {
        this.listenerSelectCategory = f
    }
}