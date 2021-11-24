package uz.star.mardexworker.model.request

import java.io.Serializable

data class LocationRequest(
    val type: String,
    val coordinates: List<Double>
) : Serializable