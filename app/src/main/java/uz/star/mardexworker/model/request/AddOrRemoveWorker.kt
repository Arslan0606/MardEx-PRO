package uz.star.mardexworker.model.request


import com.google.gson.annotations.SerializedName

data class AddOrRemoveWorker(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("user_id")
    val userId: String
)