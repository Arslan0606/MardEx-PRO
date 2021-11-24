package uz.star.mardexworker.ui.screen.main_activity.edit_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.R
import uz.star.mardexworker.app.App
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.UpdatePasswordData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

@HiltViewModel
class EditPasswordViewModel @Inject constructor(
    private val repository: EditPasswordRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _updateUserPasswordData = MediatorLiveData<AuthResponseData>()
    val updateUserPasswordData: LiveData<AuthResponseData> get() = _updateUserPasswordData

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _toastMessageData = MutableLiveData<String>()
    val toastMessageData: LiveData<String> get() = _toastMessageData

    fun updateUserPassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        when {
            (oldPassword == "" || newPassword == "" || confirmPassword == "") -> {
                _toastMessageData.value = App.instance.getString(R.string.error_change_password)
            }
            newPassword != confirmPassword -> {
                _toastMessageData.value = App.instance.getString(R.string.passwords_error)
            }
            else -> {
                val updatePasswordData = UpdatePasswordData(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
                _updateUserPasswordData.addSourceDisposable(
                    repository.updatePassword(
                        userId = storage.id,
                        updatePasswordData = updatePasswordData
                    )
                ) {
                    it.onData { responseUpdateUserData ->
                        _updateUserPasswordData.value = responseUpdateUserData
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
    }


}