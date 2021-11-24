package uz.star.mardexworker.model.request


import com.google.gson.annotations.SerializedName

data class VerifyData(
    @SerializedName("check_string")
    val checkString: String,
    @SerializedName("phone")
    val phone: String
)