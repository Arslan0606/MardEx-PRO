package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.model.response.tariff.TariffData
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsRepository
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class TariffsViewModel @Inject constructor(
    private val model: TariffsRepository,
    private val storage: LocalStorage,
    private val repository: OwnNotificationsRepository
) : ViewModel() {

    private val _responseUserData = MediatorLiveData<WorkerResponseData>()
    val responseUserData: LiveData<WorkerResponseData> get() = _responseUserData

    private val _responseTariffData = MediatorLiveData<List<TariffData>>()
    val responseTariffData: LiveData<List<TariffData>> get() = _responseTariffData

    private val _checkPaymentStatus = MediatorLiveData<CheckPaymentStatusData?>()
    val checkPaymentStatus: LiveData<CheckPaymentStatusData?> get() = _checkPaymentStatus

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    private val _unReadNotifications = MediatorLiveData<List<OwnNotificationResponse>>()
    val unReadNotifications: LiveData<List<OwnNotificationResponse>> get() = _unReadNotifications

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

    init {
        getTariff()
        getWorker()
    }

    fun getWorker() {
        val workerId = storage.id

        _loader.value = true
        _responseUserData.addSourceDisposable(model.getWorker(workerId)) {
            it.onData { loginResponse ->
                updateAllDynamic(workerResponseData = loginResponse, storage = storage)
                _responseUserData.value = loginResponse
                getCheckPaymentStatus()
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { data ->
                _message.value = data
            }
            _loader.value = false
        }
    }

    fun getTariff() {
        _loader.value = true
        _responseTariffData.addSourceDisposable(model.getTariffs()) {
            it.onData { loginResponse ->
                _responseTariffData.value = loginResponse
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { data ->
                _message.value = data
            }
            _loader.value = false
        }
    }

    fun getCheckPaymentStatus() {
        val workerId = storage.id
        _loader.value = true
        _checkPaymentStatus.addSourceDisposable(model.getCheckPaymentStatus(workerId)) {
            it.onData { checkPaymentStatusData ->
                _checkPaymentStatus.value = checkPaymentStatusData
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { data ->
                _message.value = data
            }
            _loader.value = false
        }
    }
}