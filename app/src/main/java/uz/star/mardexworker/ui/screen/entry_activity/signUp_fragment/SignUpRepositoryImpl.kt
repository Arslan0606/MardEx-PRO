package uz.star.mardexworker.ui.screen.entry_activity.signUp_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.R
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.model.request.CheckData
import uz.star.mardexworker.model.request.signup.PhoneData
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.ResponseServer
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.regions.CityResponse
import uz.star.mardexworker.model.response.regions.RegionResponse
import javax.inject.Inject

/**
 * Created by Raximjanov Davronbek  08.04.2021
 */

class SignUpRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : SignUpRepository, SafeApiRequest() {

    override fun sendVerificationCode(phone: String): LiveData<ResultData<Boolean>> {
        val resultLiveData = MutableLiveData<ResultData<Boolean>>()

        Coroutines.ioThenMain(
            { apiRequest { api.sendSmsCode(PhoneData(phone)) } },
            { response ->
                response?.onData { data ->
                    if (data.success)
                        resultLiveData.value = ResultData.data(true)
                    else
                        resultLiveData.value =
                            ResultData.messageData(MessageData.resource(R.string.failure))
                }?.onMessageData { messageData ->
                    resultLiveData.value = ResultData.messageData(messageData)
                }
            }
        )
        return resultLiveData
    }

    override fun checkExsist(data: CheckData): LiveData<ResultData<OpportunityResponse>> {
        val resultLiveData = MutableLiveData<ResultData<OpportunityResponse>>()

        Coroutines.ioThenMain(
            { apiRequest { api.checkExsist(data) } },
            { response ->
                response?.onData { data ->
                    resultLiveData.value = ResultData.data(data)
                }?.onMessageData { messageData ->
                    resultLiveData.value = ResultData.messageData(messageData)
                }
            }
        )
        return resultLiveData
    }

    override fun getCities(): LiveData<ResultData<ResponseServer<List<CityResponse>>>> {
        val resultLiveData = MutableLiveData<ResultData<ResponseServer<List<CityResponse>>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getCities() } },
            { response ->
                response?.onData { data ->
                    resultLiveData.value = ResultData.data(data)
                }?.onMessageData { messageData ->
                    resultLiveData.value = ResultData.messageData(messageData)
                }
            }
        )
        return resultLiveData
    }

    override fun getRegions(): LiveData<ResultData<ResponseServer<List<RegionResponse>>>> {
        val resultLiveData = MutableLiveData<ResultData<ResponseServer<List<RegionResponse>>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getRegions() } },
            { response ->
                response?.onData { data ->
                    resultLiveData.value = ResultData.data(data)
                }?.onMessageData { messageData ->
                    resultLiveData.value = ResultData.messageData(messageData)
                }
            }
        )
        return resultLiveData
    }
}