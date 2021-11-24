package uz.star.mardexworker.ui.screen.own_notifications_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.NotificationsNewsApi
import uz.star.mardexworker.model.request.IsRead
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import javax.inject.Inject


/**
 * Created by Davronbek Raximjanov on 27/06/2021
 **/

class OwnNotificationsRepositoryImpl @Inject constructor(
    private val api: NotificationsNewsApi
) : OwnNotificationsRepository, SafeApiRequest() {

    override fun getAllOwnNotifications(userId: String): LiveData<ResultData<List<OwnNotificationResponse>>> {
        val resultLiveData = MutableLiveData<ResultData<List<OwnNotificationResponse>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getAllOwnNotifications(userId) } },
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

    override fun patchToReadNotification(notificationId: String): LiveData<ResultData<OwnNotificationResponse>> {
        val resultLiveData = MutableLiveData<ResultData<OwnNotificationResponse>>()

        Coroutines.ioThenMain(
            {
                apiRequest {
                    api.changeStatusToRead(notificationId, IsRead(true))
                }
            },
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