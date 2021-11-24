package uz.star.mardexworker.model.request

import com.google.gson.annotations.SerializedName
import uz.star.mardexworker.model.response.title.Title

data class FastOrderRequestData(

    @field:SerializedName("full_desc")
    val fullDesc: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("work_count")
    val workCount: Int? = null,

    @field:SerializedName("job_id")
    val jobId: String? = null,

    @field:SerializedName("job_title")
    val jobTitle: Title? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("client_id")
    val clientId: String? = null,

    @field:SerializedName("latlng")
    val latlng: String? = null,

    @field:SerializedName("desc")
    val desc: String? = null,

    @field:SerializedName("images")
    val images: List<String>? = null,

    @field:SerializedName("isFinish")
    val isFinish: Boolean? = null,

    @field:SerializedName("status")
    val status: String? = null,
)
