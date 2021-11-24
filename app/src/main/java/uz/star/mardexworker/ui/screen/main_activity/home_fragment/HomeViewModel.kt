package uz.star.mardexworker.ui.screen.main_activity.home_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.EditWorkerHomeRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsRepository
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val storage: LocalStorage,
    private val repository2: OwnNotificationsRepository
) : ViewModel() {

    private val _worker = MediatorLiveData<WorkerResponseData?>()
    val worker: LiveData<WorkerResponseData?> get() = _worker

    private val _initEdit = MediatorLiveData<WorkerResponseData?>()
    val initEdit: LiveData<WorkerResponseData?> get() = _initEdit

    private val _checkPaymentStatus = MediatorLiveData<CheckPaymentStatusData?>()
    val checkPaymentStatus: LiveData<CheckPaymentStatusData?> get() = _checkPaymentStatus

    private val _responseUserUpdateData = MediatorLiveData<AuthResponseData>()
    val responseUserUpdateData: LiveData<AuthResponseData> get() = _responseUserUpdateData

    private val _responseOrderPost = MediatorLiveData<FastOrderResponseData>()
    val responseOrderPost: LiveData<FastOrderResponseData> get() = _responseOrderPost

    private val _responseOrderPatch = MediatorLiveData<FastOrderResponseData>()
    val responseOrderPatch: LiveData<FastOrderResponseData> get() = _responseOrderPatch

    private val _orders = MediatorLiveData<List<GetOrdersResponse>>()
    val orders: LiveData<List<GetOrdersResponse>> get() = _orders

    private var _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _unReadNotifications = MediatorLiveData<List<OwnNotificationResponse>>()
    val unReadNotifications: LiveData<List<OwnNotificationResponse>> get() = _unReadNotifications

    fun getUnReadOwnNotificationsCount() {
        val workerId = storage.id
        val unReadOwnNotifications = ArrayList<OwnNotificationResponse>()
        _unReadNotifications.addSourceDisposable(repository2.getAllOwnNotifications(workerId)) {
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
        val workerId = storage.id
        _initEdit.addSourceDisposable(repository.getWorker(workerId)) {
            it.onData { loginResponse ->
                updateAllDynamic(workerResponseData = loginResponse, storage = storage)
                _initEdit.value = loginResponse
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun updateFree(freeState: Boolean) {
        _responseUserUpdateData.value?.isFree = freeState
        _worker.value?.isFree = freeState
        _initEdit.value?.isFree = freeState
    }

    fun getWorker() {
        val workerId = storage.id
        _worker.addSourceDisposable(repository.getWorker(workerId)) {
            it.onData { loginResponse ->
                updateAllDynamic(workerResponseData = loginResponse, storage = storage)
                _worker.value = loginResponse
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun getCheckPaymentStatus() {
        val workerId = storage.id
        _checkPaymentStatus.addSourceDisposable(repository.getCheckPaymentStatus(workerId)) {
            getWorker()
            it.onData { checkPaymentStatusData ->
                _checkPaymentStatus.value = checkPaymentStatusData
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }
        }
    }

    fun update(data: EditWorkerHomeRequestData) {
        _responseUserUpdateData.addSourceDisposable(repository.updateUserFromHome(data)) {
            it.onData { loginResponse ->
                updateAllDynamic(authResponseData = loginResponse, storage = storage)
                _responseUserUpdateData.value = loginResponse
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun postOrder(data: SocketResponseData) {
        val l = data.images?.toMutableList() ?: arrayListOf()
        l.remove("")
        val d = FastOrderRequestData(
            data.jobDescriptionFull,
            data.workerId,
            data.workerCount,
            data.jobId,
            data.jobName,
            data.jobPrice,
            data.clientId,
            data.location.coordinates[0].toString() + "," + data.location.coordinates[1],
            data.jobDescription,
            l
        )
        _responseOrderPost.addSourceDisposable(repository.addFastPost(d)) {
            it.onData { res ->
                _responseOrderPost.value = res
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun patchOrder(data: GetOrdersResponse, isFinish: Boolean = true) {
        val l = arrayListOf<String>()
        try {
            data.images?.forEach {
                l.add(it as String)
            }
        } catch (e: Exception) {
        }
        val d = FastOrderRequestData(
            data.fullDesc,
            data.userId?.id,
            data.workCount,
            data.jobId?.id,
            data.jobId?.title,
            data.price,
            data.clientId?.id,
            (data.location?.coordinates?.get(0)?.toString() ?: 0).toString() + "," + (data.location?.coordinates?.get(1) ?: 0),
            data.desc,
            l,
            isFinish
        )
        _responseOrderPatch.addSourceDisposable(repository.patchFastPost(data.id ?: "", d)) {
            it.onData { res ->
                _responseOrderPatch.value = res
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun getOrders() {
        val workerId = storage.id
        _orders.addSourceDisposable(repository.getOrders(workerId)) {
            it.onData { data ->
                _orders.value = data
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun setGpsStatus(b: Boolean) {
        repository.gpsStatus = b
    }

    fun getGpsStatus(): Boolean {
        return repository.gpsStatus
    }
}