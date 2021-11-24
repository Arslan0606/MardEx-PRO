package uz.star.mardexworker.ui.screen.main_activity.edit_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.response.UpdatePasswordData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.screen.main_activity.edit_password.EditPasswordRepository
import javax.inject.Inject

class EditPasswordRepositoryImpl @Inject constructor(
    private val profileDataApi: JobApi
) : EditPasswordRepository, SafeApiRequest() {

    override fun updatePassword(userId: String, updatePasswordData: UpdatePasswordData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()
        Coroutines.ioThenMain(
            { apiRequest { profileDataApi.updatePassword(userId, updatePasswordData) } },
            { data ->
                data?.onData {
                    if (it.success)
                        resultLiveData.value = ResultData.data(it.data)
                    else
                        resultLiveData.value = ResultData.message("password bo'sh")
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