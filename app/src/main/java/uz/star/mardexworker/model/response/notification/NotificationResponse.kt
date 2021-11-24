package uz.star.mardexworker.model.response.notification

import com.google.gson.annotations.SerializedName
import uz.star.mardexworker.model.response.login.LocationRequest
import uz.star.mardexworker.model.response.title.Title

data class NotificationResponse(

    @field:SerializedName("workerId")
    val workerId: WorkerId? = null,

    @field:SerializedName("images")
    val images: List<String>? = null,

    @field:SerializedName("clientId")
    val clientId: ClientId? = null,

    @field:SerializedName("jobPrice")
    val jobPrice: String? = null,

    @field:SerializedName("jobId")
    val jobId: JobId? = null,

    @field:SerializedName("workerCount")
    val workerCount: Int? = null,

    @field:SerializedName("timeUser")
    val timeUser: Long = 0,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("jobDescription")
    val jobDescription: String? = null,

    @field:SerializedName("location")
    val location: LocationRequest? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("jobDescriptionFull")
    val jobDescriptionFull: String? = null,

    @field:SerializedName("timeClient")
    val timeClient: Long = 0
)

data class ClientId(

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val id: String? = null
)

data class WorkerId(

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("_id")
    val id: String? = null
)

data class JobId(

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: Title? = null
)
