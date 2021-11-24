package uz.star.mardexworker.ui.screen.entry_activity.login_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created Raximjanov Davronbek on 08.04.2021.
 **/

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val model: LoginRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _responseLoginData = MediatorLiveData<ResultData<AuthResponseData>>()
    val responseLoginData: LiveData<ResultData<AuthResponseData>> get() = _responseLoginData

    private val _openSignUpData = MutableLiveData<Unit>()
    val openSignUpData: LiveData<Unit> get() = _openSignUpData

    private val _openResetPasswordData = MutableLiveData<Unit>()
    val openResetPasswordData: LiveData<Unit> get() = _openResetPasswordData

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message


    fun openSignUp() {
        _openSignUpData.value = Unit
    }


    fun openResetPassword() {
        _openResetPasswordData.value = Unit
    }

    fun login(
        phone: String,
        password: String
    ) {
        when {
            phone.length < 13 -> {
                _message.value =
                    MessageData.resource(R.string.enter_your_phone_number)
                return
            }
            password.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.enter_your_password_number)
                return
            }
            else -> {
                _loader.value = true
                val lg = LoginData(phone, password)
                _responseLoginData.addSourceDisposable(model.login(lg)) {
                    _loader.value = false
                    it.onData { loginResponse ->
                        updateAllDynamic(authResponseData = loginResponse, storage = storage)
                        sendNotificationToken(loginResponse)
                    }.onMessage { message ->
                        _loader.value = false
                        _message.value = MessageData.message(message)
                    }.onMessageData { messageData ->
                        _loader.value = false
                        messageData.onResource { stringResources ->
                            _message.value = MessageData.resource(stringResources)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotificationToken(authResponseData: AuthResponseData) {
        _responseLoginData.addSourceDisposable(model.sendNotificationTokenToServer()) {
            it.onData { response ->
                _responseLoginData.value = ResultData.data(response)
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                _message.value = messageData
            }
        }
    }
}