package uz.star.mardexworker.model.response.notification_for_user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OwnNotificationResponse(

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("isRead")
    val isRead: Boolean = false,

    @field:SerializedName("created_at")
    val createdAt: Long? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: Title? = null,

    @field:SerializedName("body")
    val body: Body? = null
):Serializable

data class Title(

    @field:SerializedName("ru")
    val ru: String? = null,

    @field:SerializedName("uz")
    val uz: String? = null,

    @field:SerializedName("uz_kr")
    val uzKr: String? = null,

    @field:SerializedName("en")
    val en: String? = null
):Serializable

data class Body(

    @field:SerializedName("ru")
    val ru: String? = null,

    @field:SerializedName("uz")
    val uz: String? = null,

    @field:SerializedName("uz_kr")
    val uzKr: String? = null,

    @field:SerializedName("en")
    val en: String? = null
):Serializable
