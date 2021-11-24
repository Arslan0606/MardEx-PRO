package uz.star.mardexworker.ui.screen.entry_activity.login_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import javax.inject.Inject


class LoginRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val jobApi: JobApi,
    private val storage: LocalStorage
) : LoginRepository, SafeApiRequest() {
    override fun login(loginData: LoginData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.login(loginData) } },
            { data ->
                data?.onData {
                    if (it.success) {
                        storage.token = it.token!!
                        storage.id = it.data._id
                        storage.logged = true
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