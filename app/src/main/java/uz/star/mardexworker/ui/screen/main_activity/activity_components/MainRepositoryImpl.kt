package uz.star.mardexworker.ui.screen.main_activity.activity_components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.request.OpportunityEnum
import uz.star.mardexworker.model.request.OpportunityRequest
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class MainRepositoryImpl @Inject constructor(
    private val api: JobApi
) : MainRepository, SafeApiRequest() {

    override fun setWorkerFree(): LiveData<ResultData<WorkerResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<WorkerResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.setWorkerFree() } },
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

    override fun setWorkerUnfree(): LiveData<ResultData<WorkerResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<WorkerResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.setWorkerUnfree() } },
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

    override fun minusOpportunity(type: OpportunityEnum): LiveData<ResultData<OpportunityResponse>> {
        val resultLiveData = MutableLiveData<ResultData<OpportunityResponse>>()

        Coroutines.ioThenMain(
            { apiRequest { api.minusOpportunity(LocalStorage.instance.id, OpportunityRequest(type = type.text)) } },
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
}