package uz.star.mardexworker.model.request.signup

import uz.star.mardexworker.model.request.LocationRequest
import java.io.Serializable

/**
 * Created by Raximjanov Davronbek  08.04.2021
 */

data class SignUpData(
    var fullName: String = "",
    var phone: String = "",
    var password: String = "",
    var payment_id: String = "",
    var description: String = "",
    var location: LocationRequest? = null,
    var passport: PassportData? = null,
    var promocode: Int = 0
) : Serializable