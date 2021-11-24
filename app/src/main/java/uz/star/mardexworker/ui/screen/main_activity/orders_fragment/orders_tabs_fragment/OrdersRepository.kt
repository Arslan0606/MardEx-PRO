package uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface OrdersRepository {
    fun patchFastPost(id: String, fastOrderRequestData: FastOrderRequestData): LiveData<ResultData<FastOrderResponseData>>

    fun deleteOrderFromClient(id: String): LiveData<ResultData<Any>>

    fun getOrders(workerId: String): LiveData<ResultData<List<GetOrdersResponse>>>
}