package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.history_payment_fragment

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.Payment
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.tariff.PaymentResponseData
import uz.star.mardexworker.model.response.tariff.TariffData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface HistoryPaymentRepository {
    fun getPayments(workerId: String): LiveData<ResultData<PaymentResponseData>>
}