package uz.star.mardexworker.ui.screen.main_activity.profile_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.request.OrdersByStatusRequest
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class ProfileRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val api: JobApi,
    private val storage: LocalStorage
) : ProfileRepository, SafeApiRequest() {

    override fun getJobs(): LiveData<ResultData<List<JobCategory>>> {
        val resultLiveData = MutableLiveData<ResultData<List<JobCategory>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getJobs(storage.language) } },
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

    override fun uploadImage(part: MultipartBody.Part): LiveData<ResultData<ImagePath>> {
        val resultLiveData = MutableLiveData<ResultData<ImagePath>>()

        Coroutines.ioThenMain(
            { apiRequest { api.uploadPicture(part) } },
            { data ->
                data?.onData {
                    resultLiveData.value = ResultData.data(it)
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

    override fun getOrdersCount(status: String): LiveData<ResultData<List<FastOrderResponseData>>> {
        val resultLiveData = MutableLiveData<ResultData<List<FastOrderResponseData>>>()

        Coroutines.ioThenMain(
            { apiRequest { authApi.getOrdersByStatus(storage.id, OrdersByStatusRequest(status)) } },
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