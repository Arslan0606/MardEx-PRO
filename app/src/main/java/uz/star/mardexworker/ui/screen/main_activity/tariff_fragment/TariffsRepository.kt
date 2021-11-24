package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.tariff.TariffData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface TariffsRepository {
    fun getTariffs(): LiveData<ResultData<List<TariffData>>>

    fun getWorker(workerId: String): LiveData<ResultData<WorkerResponseData>>

    fun getCheckPaymentStatus(workerId: String): LiveData<ResultData<CheckPaymentStatusData>>

}