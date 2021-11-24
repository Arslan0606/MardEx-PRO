package uz.star.mardexworker.ui.screen.entry_activity.sms_verification_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.request.VerifyData
import uz.star.mardexworker.model.request.signup.PhoneData
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import javax.inject.Inject

class SmsVerificationRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val jobApi: JobApi,
    private val storage: LocalStorage
) : SmsVerificationRepository, SafeApiRequest() {
    override fun signup(signUpData: SignUpData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()
        Coroutines.ioThenMain(
            { apiRequest { api.signup(signUpData) } },
            { response ->
                response?.onData {
                    if (it.success) {
                        // toDo /////// .....
                        storage.token = it.token!!
                        storage.id = it.data._id
                        storage.logged = true
                        // toDo /////// .....
                        resultLiveData.value = ResultData.data(it.data)
                    } else
                        resultLiveData.value = ResultData.message("list bosh")
                }
                response?.onMessage {
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

    override fun verifyPhone(phone: String, code: String): LiveData<ResultData<Boolean>> {
        val resultLiveData = MutableLiveData<ResultData<Boolean>>()
        Coroutines.ioThenMain(
            { apiRequest { api.checkSmsCode(VerifyData(code, phone)) } },
            { response ->
                response?.onData {
                    if (it.success) {
                        resultLiveData.value = ResultData.data(true)
                    } else
                        resultLiveData.value = ResultData.message("ERROR")
                }
                response?.onMessage { message ->
                    resultLiveData.value = ResultData.message(message)
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.messageData(MessageData.resource(it))
                    }
                }
            }
        )


        return resultLiveData
    }


    override fun sendAgainVerificationCode(phone: String): LiveData<ResultData<Boolean>> {
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

    override fun restorePassword(login: LoginData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()
        Coroutines.ioThenMain(
            { apiRequest { api.resetUserPassword(login) } },
            { response ->
                response?.onData {
                    if (it.success) {
                        resultLiveData.value = ResultData.data(it.data)
                    } else
                        resultLiveData.value = ResultData.message("list bosh")
                }
                response?.onMessage {
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

    override fun sendNotificationTokenToServer(): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()
        Coroutines.ioThenMain(
            { apiRequest { jobApi.updateInfo(EditProfileRequestData(fcm_token = storage.notificationToken)) } },
            { response ->
                response?.onData {
                    if (it.success) {
                        resultLiveData.value = ResultData.data(it.data)
                    } else
                        resultLiveData.value = ResultData.message("list bosh")
                }
                response?.onMessage {
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
