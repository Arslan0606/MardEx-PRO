package uz.star.mardexworker.ui.screen.main_activity.promocode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.R
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.model.request.promocode.PromocodeReequestData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.utils.PARTICIPATED
import javax.inject.Inject

/**
 * Created by Farhod Tohirov on 27-Jun-21
 **/

class PromocodeRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : PromocodeRepository, SafeApiRequest() {

    override fun sendPromocode(promocodeRequestData: PromocodeReequestData): LiveData<ResultData<Boolean>> {
        val resultLiveData = MutableLiveData<ResultData<Boolean>>()

        Coroutines.ioThenMain(
            { apiRequest { authApi.sendPromocode(promocodeRequestData) } },
            { data ->
                data?.onData {
                    if (it.success) {
                        if (it.data == PARTICIPATED) {
                            resultLiveData.value = ResultData.resource(R.string.promocode_used)
                        } else {
                            resultLiveData.value = ResultData.data(true)
                        }
                    } else
                        resultLiveData.value = ResultData.messageData(MessageData.resource(R.string.error_promocode))
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