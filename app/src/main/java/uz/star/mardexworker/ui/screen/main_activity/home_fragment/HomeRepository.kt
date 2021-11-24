package uz.star.mardexworker.ui.screen.main_activity.home_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.*
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface HomeRepository {
    var gpsStatus: Boolean
    fun getWorker(workerId: String): LiveData<ResultData<WorkerResponseData>>

    fun updateUser(dataEdit: EditProfileRequestData): LiveData<ResultData<AuthResponseData>>

    fun updateUserFromHome(dataEdit: EditWorkerHomeRequestData): LiveData<ResultData<AuthResponseData>>

    fun getCheckPaymentStatus(workerId: String): LiveData<ResultData<CheckPaymentStatusData>>

    fun addFastPost(fastOrderRequestData: FastOrderRequestData): LiveData<ResultData<FastOrderResponseData>>

    fun patchFastPost(id: String, fastOrderRequestData: FastOrderRequestData): LiveData<ResultData<FastOrderResponseData>>

    fun getOrders(workerId: String): LiveData<ResultData<List<GetOrdersResponse>>>

}