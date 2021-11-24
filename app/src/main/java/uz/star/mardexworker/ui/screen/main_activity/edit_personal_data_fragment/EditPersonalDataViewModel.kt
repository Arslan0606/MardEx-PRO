package uz.star.mardexworker.ui.screen.main_activity.edit_personal_data_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Kurganbaev Jasurbek on 03.05.2021 17:26
 **/
@HiltViewModel
class EditPersonalDataViewModel @Inject constructor(
    private val repository: EditPersonalDataRepository,
    private val storage: LocalStorage
) : ViewModel() {

/*    private val _worker = MediatorLiveData<WorkerResponseData?>()
    val worker: LiveData<WorkerResponseData?> get() = _worker*/

    private val _initEdit = MediatorLiveData<WorkerResponseData?>()
    val initEdit: LiveData<WorkerResponseData?> get() = _initEdit

    private val _responseUserUpdateData = MediatorLiveData<AuthResponseData>()
    val responseUserUpdateData: LiveData<AuthResponseData> get() = _responseUserUpdateData

    private var _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

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

    fun updateUserPersonalData(editProfileRequestData: EditProfileRequestData, fullName: String, description: String) {
        if (fullName.isEmpty()) {
            _message.value = MessageData.resource(R.string.please_complete_fields)
            return
        }
        editProfileRequestData.fullName = fullName
        editProfileRequestData.description = description

        _responseUserUpdateData.addSourceDisposable(repository.updateUser(editProfileRequestData)) {
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

}