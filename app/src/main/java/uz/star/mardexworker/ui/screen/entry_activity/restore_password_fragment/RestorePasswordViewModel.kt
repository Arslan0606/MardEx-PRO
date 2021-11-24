package uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.R
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Kurganbaev Jasurbek on 22.04.2021 15:57
 **/
@HiltViewModel
class RestorePasswordViewModel @Inject constructor(
    private val model: RestorePasswordRepository
) : ViewModel() {

    private val _responseRestorePassword = MediatorLiveData<LoginData>()
    val responseRestorePassword: LiveData<LoginData> get() = _responseRestorePassword


    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    fun openCodeVerifyScreen(etPhoneNumber: String, etPassword: String, etRepeatPassword: String) {
        when {

            etPhoneNumber.length < 13 -> {
                _message.value =
                    MessageData.resource(R.string.input_phone_number_right)

            }

            etPassword.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.entering_password)
            }

            etRepeatPassword.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.repeat_your_passord)

            }

            etPassword != etRepeatPassword -> {
                _message.value =
                    MessageData.resource(R.string.incompatible_password)

            }

            else -> {
                _loader.value = true
                _message.addSourceDisposable(model.sendVerificationCode(etPhoneNumber)) {
                    _loader.value = false
                    it.onData {
                        val loginData = LoginData(etPhoneNumber, etPassword)
                        _responseRestorePassword.value = loginData
                    }.onMessage { message ->
                        _message.value = MessageData.message(message)
                    }.onMessageData { message ->
                        _message.value = message
                    }
                }
            }
        }
    }

}
