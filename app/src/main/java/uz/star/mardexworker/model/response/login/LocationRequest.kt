package uz.star.mardexworker.model.response.login

import java.io.Serializable

data class LocationRequest(
    val type: String,
    val coordinates: List<Double>
) : Serializable