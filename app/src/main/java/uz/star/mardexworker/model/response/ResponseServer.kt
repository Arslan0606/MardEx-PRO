package uz.star.mardexworker.model.response

import com.google.gson.annotations.SerializedName

data class ResponseServer<T>(
    @SerializedName("data")
    val data: T,
    @SerializedName("success")
    val success: Boolean,
    var token: String? = null
)