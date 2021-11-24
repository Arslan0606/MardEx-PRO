package uz.star.mardexworker.model.response

import com.google.gson.annotations.SerializedName
import uz.star.mardexworker.model.request.LocationRequest
import java.io.Serializable

data class FastOrderResponseData(

    @field:SerializedName("full_desc")
    val fullDesc: String? = null,

    @field:SerializedName("images")
    val images: List<String?>? = null,

    @field:SerializedName("work_count")
    val workCount: Int? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("job_id")
    val jobId: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("location")
    val location: LocationRequest? = null,

    @field:SerializedName("isFinish")
    val isFinish: Boolean? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("client_id")
    val clientId: String? = null,

    @field:SerializedName("desc")
    val desc: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: Long? = null,
) : Serializable
