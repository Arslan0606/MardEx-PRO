package uz.star.mardexworker.model.response.jobs_data

import java.io.Serializable

data class EditJobsDeliverData(
    var ls: List<JobCategory>
) : Serializable