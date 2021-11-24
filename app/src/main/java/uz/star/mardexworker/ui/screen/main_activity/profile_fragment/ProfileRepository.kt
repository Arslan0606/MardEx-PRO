package uz.star.mardexworker.ui.screen.main_activity.profile_fragment

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface ProfileRepository {
    fun getJobs(): LiveData<ResultData<List<JobCategory>>>

    fun getWorker(workerId: String): LiveData<ResultData<WorkerResponseData>>

    fun uploadImage(part: MultipartBody.Part): LiveData<ResultData<ImagePath>>

    fun updateUser(dataEdit: EditProfileRequestData): LiveData<ResultData<AuthResponseData>>

    fun getCheckPaymentStatus(workerId: String): LiveData<ResultData<CheckPaymentStatusData>>

    fun getOrdersCount(status: String): LiveData<ResultData<List<FastOrderResponseData>>>
}