package uz.star.mardexworker.model.response

import uz.star.mardexworker.model.response.login.LocationRequest
import java.io.Serializable

data class EditProfileRequestData(
    var avatar: String? = null,
    var description: String? = null,
    var fullName: String? = null,
    var images: ArrayList<String>? = null,
    var jobs: ArrayList<String>? = null,
    var location: LocationRequest? = null,
    var phone: String? = null,
    var fcm_token: String? = null
) : Serializable