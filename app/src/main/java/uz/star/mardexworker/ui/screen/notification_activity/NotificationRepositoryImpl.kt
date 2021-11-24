package uz.star.mardexworker.ui.screen.notification_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.notification.NotificationResponse
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class NotificationRepositoryImpl @Inject constructor(
    private val api: JobApi,
    private val storage: LocalStorage
) : NotificationRepository, SafeApiRequest() {

    override fun getNotificationData(): LiveData<ResultData<NotificationResponse>> {
        val resultLiveData = MutableLiveData<ResultData<NotificationResponse>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getNotification(storage.id) } },
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