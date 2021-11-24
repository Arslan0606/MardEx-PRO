package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.history_payment_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.PaymentApi
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.Payment
import uz.star.mardexworker.model.response.tariff.PaymentResponseData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class HistoryPaymentRepositoryImpl @Inject constructor(
    private val api: PaymentApi
) : HistoryPaymentRepository, SafeApiRequest() {

    override fun getPayments(workerId: String): LiveData<ResultData<PaymentResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<PaymentResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getPayments(workerId) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(it)
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
}