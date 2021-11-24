package uz.star.mardexworker.ui.screen.entry_activity.job_chooser_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.jobs_data.AddJobRequestData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData

/**
 * Created by Botirali Kozimov on 12/2/20.
 */

interface JobChooserRepository {
    fun getJobs(): LiveData<ResultData<List<JobCategory>>>

    fun addJobs(addJobRequestData: AddJobRequestData): LiveData<ResultData<AuthResponseData>>
}