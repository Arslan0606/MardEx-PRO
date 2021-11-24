package uz.star.mardexworker.ui.screen.main_activity.activity_components

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.request.OpportunityEnum
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification.NotificationResponse
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.HomeRepository
import uz.star.mardexworker.ui.screen.notification_activity.NotificationRepository
import uz.star.mardexworker.utils.livedata.Event
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 1/9/21.
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val socketRepository: SocketRepository,
    private val notificationRepository: NotificationRepository,
    private val mainModel: MainRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _notificationData = MediatorLiveData<NotificationResponse?>()
    val notificationData: LiveData<NotificationResponse?> get() = _notificationData

    private val _worker = MediatorLiveData<WorkerResponseData?>()
    val worker: LiveData<WorkerResponseData?> get() = _worker

    private val _freeState = MediatorLiveData<Boolean>()
    val freeState: LiveData<Boolean> get() = _freeState

    private val _callResLiveData = MediatorLiveData<Event<SocketResponseData?>>()
    val callResLiveData: LiveData<Event<SocketResponseData?>> get() = _callResLiveData

    private val _closeDialogLiveData = MediatorLiveData<Unit>()
    val closeDialogLiveData: LiveData<Unit> get() = _closeDialogLiveData

    private val _minusOpportunityLiveData = MediatorLiveData<OpportunityResponse>()
    val minusOpportunityLiveData: LiveData<OpportunityResponse> get() = _minusOpportunityLiveData

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private var _changeFreeButtonConfiguration = MutableLiveData<Boolean>()
    val changeFreeButtonConfiguration: LiveData<Boolean> get() = _changeFreeButtonConfiguration

    private val _responseOrderPost = MediatorLiveData<FastOrderResponseData>()
    val responseOrderPost: LiveData<FastOrderResponseData> get() = _responseOrderPost

    init {
        _callResLiveData.addSource(socketRepository.resultCallLiveData) {
            _callResLiveData.value = Event(it)
        }

        _closeDialogLiveData.addSource(socketRepository.resultCloseDialogLiveData) {
            Log.d("TEST_SOCKET","inViewModel")
            _closeDialogLiveData.value = it
        }
    }

    fun deleteValue() {
        _callResLiveData.value = Event(null)
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
                _message.value = message
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
//                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun getNotification() {
        _notificationData.addSourceDisposable(notificationRepository.getNotificationData()) {
            it.onData { workerData ->
                _notificationData.value = workerData
            }.onMessage { message -> _message.value = message }
        }
    }

    fun setWorkerFree() {
        _worker.addSourceDisposable(mainModel.setWorkerFree()) {
            it.onData { workerData ->
                socketRepository.userConnect {
                    _changeFreeButtonConfiguration.value = workerData.isFree
                }
                connectSocket()
                _freeState.value = true
//                storage.lastConnectedUserJson = ""
            }.onMessage { message -> _message.value = message }
        }
    }

    fun setWorkerUnFree() {
        _worker.addSourceDisposable(mainModel.setWorkerUnfree()) {
            it.onData { workerData ->
                socketRepository.userDisconnect {
                    _changeFreeButtonConfiguration.value = workerData.isFree
                }
                disconnectSocket()
                _freeState.value = false
            }.onMessage { message -> _message.value = message }
        }
    }

    fun disconnectSocket() {
        socketRepository.disconnectSocket()
    }

    fun connectSocket() {
        socketRepository.connectSocket()
    }

    fun getJob(it: String) {
        socketRepository.onGetJob(it)
    }

    fun cancelJob(it: String) {
        socketRepository.onCancelJob(it)
    }

    fun showNotification(socketResponseData: SocketResponseData) {
        _callResLiveData.value = Event(socketResponseData)
    }

    fun minusOpportunity(enum: OpportunityEnum) {
        _minusOpportunityLiveData.addSourceDisposable(mainModel.minusOpportunity(enum)) {
            it.onData { data ->
                _minusOpportunityLiveData.value = data
            }.onMessage { message -> _message.value = message }
        }
    }
}