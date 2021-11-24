package uz.star.mardexworker.model.socket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocationSocketRequest(
    @SerializedName("coordinates")
    val coordinates: List<Double> // lat, long
) : Serializable