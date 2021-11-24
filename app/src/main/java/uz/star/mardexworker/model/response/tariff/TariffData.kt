package uz.star.mardexworker.model.response.tariff

import com.google.gson.annotations.Expose
import uz.star.mardexworker.model.response.title.Title
import java.io.Serializable

data class TariffData(
    val __v: Int,
    val _id: String,
    val call: Int,
    val createdAt: String,
    @Expose
    var type: Boolean = false,
    val deadline: Int,
    val description: Title,
    val post: Int,
    val price: Long,
    val title: Title,
    val top: Int
) : Serializable