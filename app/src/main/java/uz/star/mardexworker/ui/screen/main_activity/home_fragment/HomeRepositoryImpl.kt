package uz.star.mardexworker.ui.screen.main_activity.home_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.data.remote.api.NotificationsNewsApi
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.EditWorkerHomeRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class HomeRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val api: JobApi,
    private val apiNewsNotifications: NotificationsNewsApi
) : HomeRepository, SafeApiRequest() {

    override var gpsStatus: Boolean = false

    override fun getWorker(workerId: String): LiveData<ResultData<WorkerResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<WorkerResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { authApi.getWorkerData(workerId) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(it.data)
                    else
                        resultLiveData.value = ResultData.message("list bosh")
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

    override fun updateUser(dataEdit: EditProfileRequestData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.updateInfo(dataEdit) } },
            { data ->
                data?.onData {
                    if (it.success) {
                        resultLiveData.value = ResultData.data(it.data)
                    } else
                        resultLiveData.value = ResultData.message("list bosh")
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

    override fun updateUserFromHome(dataEdit: EditWorkerHomeRequestData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.updateInfoHome(dataEdit) } },
            { data ->
                data?.onData {
                    if (it.success) {
                        resultLiveData.value = ResultData.data(it.data)
                    } else
                        resultLiveData.value = ResultData.message("list bosh")
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

    override fun getCheckPaymentStatus(workerId: String): LiveData<ResultData<CheckPaymentStatusData>> {
        val resultLiveData = MutableLiveData<ResultData<CheckPaymentStatusData>>()

        Coroutines.ioThenMain(
            { apiRequest { authApi.getCheckPayment(workerId) } },
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

    override fun addFastPost(fastOrderRequestData: FastOrderRequestData): LiveData<ResultData<FastOrderResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<FastOrderResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.addFastOrder(fastOrderRequestData) } },
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