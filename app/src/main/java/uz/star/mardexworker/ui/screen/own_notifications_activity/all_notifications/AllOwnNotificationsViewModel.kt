package uz.star.mardexworker.ui.screen.own_notifications_activity.all_notifications

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
class AllOwnNotificationsViewModel @Inject constructor(
    private val repository: OwnNotificationsRepository,
    private val storage: LocalStorage
) : ViewModel() {



    private var _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _allNotifications = MediatorLiveData<List<OwnNotificationResponse>>()
    val allNotifications: LiveData<List<OwnNotificationResponse>> get() = _allNotifications

    fun getAllOwnNotifications() {

        val workerId = storage.id
        _allNotifications.addSourceDisposable(repository.getAllOwnNotifications(workerId)) {
            it.onData { data ->
                _allNotifications.value = data
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