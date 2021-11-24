package uz.star.mardexworker.model.response.jobs_data

import java.io.Serializable

data class UserJobsDeliver(
    var ls: ArrayList<JobData>
) : Serializable