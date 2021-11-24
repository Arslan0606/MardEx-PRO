package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.PaymentApi
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.tariff.TariffData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class TariffsRepositoryImpl @Inject constructor(
    private val api: PaymentApi,
    private val authApi: AuthApi
) : TariffsRepository, SafeApiRequest() {

    override fun getTariffs(): LiveData<ResultData<List<TariffData>>> {
        val resultLiveData = MutableLiveData<ResultData<List<TariffData>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getTariff() } },
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
}