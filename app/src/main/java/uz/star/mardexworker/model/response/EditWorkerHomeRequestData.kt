package uz.star.mardexworker.model.response

import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.login.LocationRequest
import java.io.Serializable

data class EditWorkerHomeRequestData(
    var avatar: String? = null,
    var location: LocationRequest? = null,
    var phone: String? = null,
    var lang: String? = LocalStorage.instance.language
) : Serializable