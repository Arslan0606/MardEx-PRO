package uz.star.mardexworker.ui.screen.notification_activity

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.request.OpportunityEnum
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification.NotificationResponse

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface NotificationRepository {
    fun getNotificationData(): LiveData<ResultData<NotificationResponse>>
}