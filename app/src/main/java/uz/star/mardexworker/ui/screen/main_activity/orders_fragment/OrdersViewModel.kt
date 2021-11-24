package uz.star.mardexworker.ui.screen.main_activity.orders_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsRepository
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OwnNotificationsRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    private val _unReadNotifications = MediatorLiveData<List<OwnNotificationResponse>>()
    val unReadNotifications: LiveData<List<OwnNotificationResponse>> get() = _unReadNotifications

    private var _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message


    fun getUnReadOwnNotificationsCount() {
        val workerId = storage.id

        val unReadOwnNotifications = ArrayList<OwnNotificationResponse>()
        _unReadNotifications.addSourceDisposable(repository.getAllOwnNotifications(workerId)) {
            it.onData { data ->
                for (a in data) {
                    if (!a.isRead) {
                        unReadOwnNotifications.add(a)
                    }
                }
                _unReadNotifications.value = unReadOwnNotifications

            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }
}