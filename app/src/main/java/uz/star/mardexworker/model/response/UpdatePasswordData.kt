package uz.star.mardexworker.model.response

import com.google.gson.annotations.SerializedName

data class UpdatePasswordData(
    @SerializedName("oldPassword")
    val oldPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)