package uz.star.mardexworker.model.socket

import uz.star.mardexworker.model.response.login.LocationRequest
import uz.star.mardexworker.model.response.title.Title
import java.io.Serializable

/**
 * Created by Farhod Tohirov on 28-Jan-21
 **/

data class SocketResponseData(
    var workerId: String,
    var clientId: String,
    var jobName: Title,
    var jobDescription: String,
    var jobPrice: String,
    var jobDescriptionFull: String,
    var workerCount: Int,
    var location: LocationRequest,
    var userName: String,
    var userPhone: String,
    var images: List<String>? = null,
    var jobId: String
) : Serializable
