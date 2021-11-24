package uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.model.request.signup.PhoneData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import javax.inject.Inject

/**
 * Created by Kurganbaev Jasurbek on 28.04.2021 11:08
 **/
class RestorePasswordRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : RestorePasswordRepository, SafeApiRequest() {


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


}