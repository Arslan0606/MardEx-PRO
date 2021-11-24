package uz.star.mardexworker.ui.screen.main_activity.profile_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.enum.OrderStatusEnum
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.HomeRepository
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsRepository
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: HomeRepository,
    private val repository: ProfileRepository,
    private val storage: LocalStorage,
    private val repository2: OwnNotificationsRepository
) : ViewModel() {

    private val _responseUserData = MediatorLiveData<WorkerResponseData>()
    val responseUserData: LiveData<WorkerResponseData> get() = _responseUserData

    private val _responseUserUpdateData = MediatorLiveData<AuthResponseData>()
    val responseUserUpdateData: LiveData<AuthResponseData> get() = _responseUserUpdateData

    private val _editAvatarLiveData = MediatorLiveData<ImagePath>()
    val editAvatarLiveData: LiveData<ImagePath> get() = _editAvatarLiveData

    private val _successOrdersLiveData = MediatorLiveData<List<FastOrderResponseData>>()
    val successOrdersLiveData: LiveData<List<FastOrderResponseData>> get() = _successOrdersLiveData

    private val _cancelOrdersLiveData = MediatorLiveData<List<FastOrderResponseData>>()
    val cancelOrdersLiveData: LiveData<List<FastOrderResponseData>> get() = _cancelOrdersLiveData

    private val _addImageLiveData = MediatorLiveData<ImagePath>()
    val addImageLiveData: LiveData<ImagePath> get() = _addImageLiveData

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    private val _jobs = MediatorLiveData<List<JobCategory>>()
    val jobs: LiveData<List<JobCategory>> get() = _jobs

    private val _checkPaymentStatus = MediatorLiveData<CheckPaymentStatusData?>()
    val checkPaymentStatus: LiveData<CheckPaymentStatusData?> get() = _checkPaymentStatus

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
        getCheckPaymentStatus()
        _successOrdersLiveData.addSourceDisposable(repository.getOrdersCount(OrderStatusEnum.SUCCESS.text)) {
            it.onData { loginResponse ->
                _successOrdersLiveData.value = loginResponse
            }
        }
        _cancelOrdersLiveData.addSourceDisposable(repository.getOrdersCount(OrderStatusEnum.CANCEL_ALL.text)) {
            it.onData { loginResponse ->
                _cancelOrdersLiveData.value = loginResponse
            }
        }
    }

    fun updateFree(freeState: Boolean) {
        _responseUserUpdateData.value?.isFree = freeState
        _responseUserData.value?.isFree = freeState
    }

    fun getWorker() {
        val workerId = storage.id

        _loader.value = true
        _responseUserData.addSourceDisposable(repository.getWorker(workerId)) {
            _loader.value = false
            it.onData { loginResponse ->
                updateAllDynamic(workerResponseData = loginResponse, storage = storage)
                _responseUserData.value = loginResponse
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
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }

    fun getJobs() {
        _loader.value = true
        _jobs.addSourceDisposable(repository.getJobs()) {
            it.onData { list ->
                _jobs.value = list
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
            _loader.value = false
        }
    }

    fun uploadImage(file: File?, toAvatar: Boolean = false) {
        if (file == null) return
        _loader.value = true
        val liveData = if (toAvatar) _editAvatarLiveData else _addImageLiveData
        val part = MultipartBody.Part.createFormData(
            "image", "image.jpg", RequestBody.create(
                "image/JPEG".toMediaTypeOrNull(),
                file.readBytes()
            )
        )

        liveData.addSourceDisposable(repository.uploadImage(part)) {
            it.onData { imagePath ->
                if (toAvatar) {
                    val d = _responseUserData.value
                    d?.let {
                        it.avatar = imagePath.path
                        _responseUserData.postValue(it)
                    }
                }
                liveData.value = imagePath
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
            _loader.value = false
        }
    }

    fun update(data: EditProfileRequestData) {
        _loader.value = true
        _responseUserUpdateData.addSourceDisposable(repository.updateUser(data)) {
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
            _loader.value = false
        }
    }

    fun setGpsStatus(b: Boolean) {
        repo.gpsStatus = b
    }

    fun getGpsStatus(): Boolean {
        return repo.gpsStatus
    }
}