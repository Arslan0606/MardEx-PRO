package uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.request.AddOrRemoveWorker
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class OrdersRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val api: JobApi,
    private val storage: LocalStorage
) : OrdersRepository, SafeApiRequest() {

    override fun patchFastPost(id: String, fastOrderRequestData: FastOrderRequestData): LiveData<ResultData<FastOrderResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<FastOrderResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.patchFastOrder(id, fastOrderRequestData) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(it.data)
                    else
                        resultLiveData.value = ResultData.message("llll bosh")
                }
                data?.onMessage {
                    resultLiveData.value = ResultData.message(it)
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.messageData(MessageData.resource(it))
                    }
                }
            }
        )
        return resultLiveData
    }

    override fun deleteOrderFromClient(id: String): LiveData<ResultData<Any>> {
        val resultLiveData = MutableLiveData<ResultData<Any>>()

        Coroutines.ioThenMain(
            { apiRequest { api.deleteOrderFromClient(AddOrRemoveWorker(id, "remove", storage.id)) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(it.data)
                    else
                        resultLiveData.value = ResultData.message("llll bosh")
                }
                data?.onMessage {
                    resultLiveData.value = ResultData.message(it)
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.messageData(MessageData.resource(it))
                    }
                }
            }
        )
        return resultLiveData
    }

    override fun getOrders(workerId: String): LiveData<ResultData<List<GetOrdersResponse>>> {
        val resultLiveData = MutableLiveData<ResultData<List<GetOrdersResponse>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getOrders(workerId) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(it.data)
                    else
                        resultLiveData.value = ResultData.message("llll bosh")
                }
                data?.onMessage {
                    resultLiveData.value = ResultData.message(it)
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.messageData(MessageData.resource(it))
                    }
                }
            }
        )
        return resultLiveData
    }
}