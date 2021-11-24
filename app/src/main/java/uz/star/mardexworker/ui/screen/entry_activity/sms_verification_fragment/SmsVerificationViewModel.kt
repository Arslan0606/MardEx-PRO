package uz.star.mardexworker.ui.screen.entry_activity.sms_verification_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment.RestorePasswordRepository
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject


@HiltViewModel
class SmsVerificationViewModel @Inject constructor(
    private val model: SmsVerificationRepository,
    private val restoreModel: RestorePasswordRepository,

    private val storage: LocalStorage
) : ViewModel() {

    private val _loginData = MediatorLiveData<AuthResponseData>()
    val loginData: LiveData<AuthResponseData> get() = _loginData

    private val _sendAgainCodeData = MutableLiveData<Boolean>()
    val sendAgainCodeData: LiveData<Boolean> get() = _sendAgainCodeData

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _timerLiveData = MutableLiveData<Unit>()
    val timerLiveData: LiveData<Unit> get() = _timerLiveData

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    init {
        _timerLiveData.value = Unit
    }

    fun verifyPhone(signUpData: SignUpData, code: String, newPassword: String, phone: String, screenName: String) {
        _loader.value = true
        _loginData.addSourceDisposable(model.verifyPhone(phone, code)) {
            _loader.value = false
            it.onData { _ ->
                if (screenName == "signup") {
                    signup(signUpData)
                } else {
                    resetUserPassword(LoginData(phone, newPassword))
                }
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                _message.value = messageData
            }
        }
    }

    private fun signup(signUpData: SignUpData) {
        _loader.value = true
        _loginData.addSourceDisposable(model.signup(signUpData)) {
            _loader.value = false
            it.onData { signUpResponse ->
                updateAllDynamic(authResponseData = signUpResponse, storage = storage)
                sendNotificationToken()
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                _message.value = messageData
            }
        }
    }

    private fun resetUserPassword(login: LoginData) {
        _loader.value = true
        _loginData.addSourceDisposable(model.restorePassword(login)) {
            _loader.value = false
            it.onData { restorePasswordResponse ->
                _loginData.value = restorePasswordResponse
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                _message.value = messageData
            }
        }
    }

    fun sendAgain(phoneNumber: String) {
        _loader.value = true
        _message.addSourceDisposable(restoreModel.sendVerificationCode(phoneNumber)) {
            _loader.value = false
            it.onData {
                _sendAgainCodeData.value = true
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                _message.value = messageData
            }
        }
    }

    private fun sendNotificationToken() {
        _loginData.addSourceDisposable(model.sendNotificationTokenToServer()) {
            _loader.value = false
            it.onData { response ->
                _loginData.value = response
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                _message.value = messageData
            }
        }
    }

}
