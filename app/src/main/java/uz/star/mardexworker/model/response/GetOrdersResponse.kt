package uz.star.mardexworker.model.response

import com.google.gson.annotations.SerializedName
import uz.star.mardexworker.model.response.title.Title
import java.io.Serializable

data class GetOrdersResponse(

    @field:SerializedName("full_desc")
    val fullDesc: String? = null,

    @field:SerializedName("images")
    val images: List<Any?>? = null,

    @field:SerializedName("work_count")
    val workCount: Int? = null,

    @field:SerializedName("user_id")
    val userId: UserId? = null,

    @field:SerializedName("job_id")
    val jobId: JobId? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("isFinish")
    var isFinish: Boolean? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("client_id")
    val clientId: ClientId? = null,

    @field:SerializedName("desc")
    val desc: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: Long? = null,
) : Serializable

data class ClientId(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null
) : Serializable

data class UserId(

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("_id")
    val id: String? = null
) : Serializable

data class Location(

    @field:SerializedName("coordinates")
    val coordinates: List<Double?>? = null,

    @field:SerializedName("type")
    val type: String? = null
) : Serializable

data class JobId(

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("pic")
    val pic: String? = null,

    @field:SerializedName("title")
    val title: Title? = null
) : Serializable
