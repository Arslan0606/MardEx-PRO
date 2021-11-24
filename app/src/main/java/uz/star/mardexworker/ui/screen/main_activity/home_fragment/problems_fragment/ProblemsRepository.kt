package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface ProblemsRepository {
    fun getWorker(workerId: String): LiveData<ResultData<WorkerResponseData>>

    fun getCheckPaymentStatus(workerId: String): LiveData<ResultData<CheckPaymentStatusData>>
}