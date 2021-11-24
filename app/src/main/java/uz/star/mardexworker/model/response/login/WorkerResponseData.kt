package uz.star.mardexworker.model.response.login

import uz.star.mardexworker.model.request.signup.PassportData
import uz.star.mardexworker.model.response.jobs_data.JobData
import java.io.Serializable

data class WorkerResponseData(
    val location: LocationRequest?,
    val balance: Long,
    val post: Int,
    val call: Int,
    val top: TopData,
    val packet: Int,
    var isFree: Boolean,
    val sum_mark: SumRating?,
    val jobs: ArrayList<JobData>?,
    val images: ArrayList<String>?,
    val _id: String,
    val fullName: String,
    var payment_id: String = "",
    var avatar: String?,
//    val description: String?,
    val description: String ="",
    val phone: String,
    val createdAt: String,
    val __v: Int,
    val payments: List<Payment>? = null,
    var passport: PassportData? = null
) : Serializable

data class SumRating(
    val sum_all: Float = 0f,
    val sum_clients: Int = 0,
)

fun SumRating?.rating(): Float {
    return if (this != null) {
        if (sum_all == 0f)
            5f
        else
            sum_all / sum_clients
    } else
        0f
}
