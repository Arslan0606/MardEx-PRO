package uz.star.mardexworker.ui.adapter.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.databinding.JobsItemRecyclerBinding
import uz.star.mardexworker.model.response.jobs_data.JobData
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.SingleBlock
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.loadImageUrl
import kotlin.collections.ArrayList

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

class JobItemRVAdapter(
    val lang: String,
    val type: Boolean = true
) :
    ListAdapter<JobData, JobItemRVAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {

    private var listenerUncheck: SingleBlock<JobData>? = null

    companion object {
        var DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<JobData>() {
            override fun areItemsTheSame(oldItem: JobData, newItem: JobData) =
                newItem._id == oldItem._id

            override fun areContentsTheSame(oldItem: JobData, newItem: JobData) =
                newItem.title == oldItem.title && newItem.category_job == oldItem.category_job &&
                        newItem.createdAt == oldItem.createdAt
        }
    }

    fun setOnClickListener(block: SingleBlock<JobData>) {
        listenerUncheck = block
    }

    inner class ViewHolder(private val binding: JobsItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                val data = getItem(adapterPosition)

                switchCompat.setOnCheckedChangeListener(null)
                switchCompat.isChecked = data.isSelected
                switchCompat.setOnCheckedChangeListener { _, isChecked ->
                    data.isSelected = isChecked
                }

                jobImage.loadImageUrl(data.pic)

                val dataTitle = data.title
                val title =
                    if (lang == LANG_UZ) dataTitle.uz else if (lang == LANG_KRILL) dataTitle.uz_kr else dataTitle.ru
                jobName.text = title
                if (type) {
                    jobLayout.setOnClickListener {
                        if (!data.isSelected) {
                            data.isSelected = true
                            switchCompat.setOnCheckedChangeListener(null)
                            switchCompat.isChecked = data.isSelected

                            setCheckedChangedListener(data)
                        } else {
                            data.isSelected = false
                            switchCompat.setOnCheckedChangeListener(null)
                            switchCompat.isChecked = data.isSelected
                            listenerUncheck?.invoke(data)

                            setCheckedChangedListener(data)
                        }
                    }
                    setCheckedChangedListener(data)
                } else {
                    switchCompat.hide()
                    jobLayout.setOnClickListener { }
                }
            }
        }

        private fun setCheckedChangedListener(data: JobData) {
            binding.apply {
                switchCompat.setOnCheckedChangeListener { _, isChecked ->
                    data.isSelected = isChecked
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        JobsItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    fun submitListForUpdate(jobs: List<JobData>, userJobsUpdate: ArrayList<JobData>) {
        jobs.forEach { allJobs ->
            userJobsUpdate.forEach { usersJob ->
                if (allJobs._id == usersJob._id && usersJob.isSelected)
                    allJobs.isSelected = true
            }
        }
        submitList(jobs)
    }
}