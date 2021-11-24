package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.HomeRepository
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 22.04.2021
 **/


@HiltViewModel
class ProblemsViewModel @Inject constructor(
    private val model: ProblemsRepository,
    private val repo: HomeRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _responseUserData = MediatorLiveData<WorkerResponseData>()
    val responseUserData: LiveData<WorkerResponseData> get() = _responseUserData

    private val _checkPaymentStatus = MediatorLiveData<CheckPaymentStatusData?>()
    val checkPaymentStatus: LiveData<CheckPaymentStatusData?> get() = _checkPaymentStatus

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    fun getWorker() {
        val workerId = storage.id
        _responseUserData.addSourceDisposable(model.getWorker(workerId)) {
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
        _checkPaymentStatus.addSourceDisposable(model.getCheckPaymentStatus(workerId)) {
            it.onData { checkPaymentStatusData ->
                _checkPaymentStatus.value = checkPaymentStatusData
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }
        }
    }

    fun setGpsStatus(b: Boolean) {
        repo.gpsStatus = b
    }

    fun getGpsStatus(): Boolean {
        return repo.gpsStatus
    }
}