package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.choose_tariff

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.PaymentApi
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.Payment
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class ChooseTariffRepositoryImpl @Inject constructor(
    private val api: PaymentApi
) : ChooseTariffRepository, SafeApiRequest() {

    override fun chooseService(workerId: String, payment: Payment): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.chooseService(workerId, payment) } },
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
}