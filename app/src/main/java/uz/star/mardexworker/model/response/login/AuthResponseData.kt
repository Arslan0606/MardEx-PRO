package uz.star.mardexworker.model.response.login

import uz.star.mardexworker.model.request.LocationRequest
import uz.star.mardexworker.model.request.signup.PassportData
import java.io.Serializable

data class AuthResponseData(
    val location: LocationRequest?,
    val balance: Long,
    val post: Int,
    val call: Int,
    val top: TopData,
    val packet: Int,
    var isFree: Boolean,
    val jobs: ArrayList<String>?,
    val images: ArrayList<String>?,
    val _id: String,
    val fullName: String,
    var payment_id: String = "",
    val avatar: String?,
//    val description: String?,
    val description: String = "",
    val phone: String,
    val createdAt: String,
    val __v: Int,
    val payments: List<Payment>? = null,
    var passport: PassportData? = null,
    var fcm_token: String? = null
) : Serializable