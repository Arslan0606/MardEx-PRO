package uz.star.mardexworker.model.response.jobs_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JobCategory(
    @SerializedName("category_title")
    val categoryTitle: TitleData,
    @SerializedName("pic")
    val pic: String?,
    @SerializedName("jobs")
    val jobs: List<JobData>,
    @Expose
    var selected: Boolean = false
) : Serializable