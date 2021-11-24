package uz.star.mardexworker.model.response.news

import com.google.gson.annotations.SerializedName
import uz.star.mardexworker.data.local.storage.LocalStorage
import java.io.Serializable

data class NewsResponse(

    @field:SerializedName("data")
    val data: List<NewsData>,

    @field:SerializedName("success")
    val success: Boolean? = null
)

data class Title(

    @field:SerializedName("ru")
    val ru: String? = null,

    @field:SerializedName("uz")
    val uz: String? = null,

    @field:SerializedName("uz_kr")
    val uzKr: String? = null,

    @field:SerializedName("en")
    val en: String? = null
)

data class Description(

    @field:SerializedName("ru")
    val ru: String? = null,

    @field:SerializedName("uz")
    val uz: String? = null,

    @field:SerializedName("uz_kr")
    val uzKr: String? = null,

    @field:SerializedName("en")
    val en: String? = null
)

data class NewsData(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("description")
    val description: Description? = null,

    @field:SerializedName("created_at")
    val createdAt: Long? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: Title? = null
) : Serializable

fun Title.toLocalString(): String? {
    return when (LocalStorage.instance.langLocal) {
        "uz" -> {
            this.uz
        }
        "ru" -> {
            this.ru
        }
        else -> {
            this.uzKr
        }
    }
}

fun Description.toLocalString(): String? {
    return when (LocalStorage.instance.langLocal) {
        "uz" -> {
            this.uz
        }
        "ru" -> {
            this.ru
        }
        else -> {
            this.uzKr
        }
    }
}
