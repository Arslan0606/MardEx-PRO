package uz.star.mardexworker.model.request.signup

import java.io.Serializable

data class PassportData(
    val backScan: String = "",
    val number: Int = 0,
    val scan: String = "",
    val scanWithFace: String = "",
    val seria: String = "",
    var isActive: Boolean = false,
    var city: String = "",
    var region: String = "",
    var gender: Boolean = false
) : Serializable