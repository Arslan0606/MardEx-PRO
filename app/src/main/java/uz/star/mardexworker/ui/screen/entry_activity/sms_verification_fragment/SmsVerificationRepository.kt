package uz.star.mardexworker.ui.screen.entry_activity.sms_verification_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData

interface SmsVerificationRepository {
    fun signup(signUpData: SignUpData): LiveData<ResultData<AuthResponseData>>
    fun verifyPhone(phone: String, code: String): LiveData<ResultData<Boolean>>
    fun sendAgainVerificationCode(phone: String): LiveData<ResultData<Boolean>>
    fun restorePassword(login: LoginData): LiveData<ResultData<AuthResponseData>>
    fun sendNotificationTokenToServer(): LiveData<ResultData<AuthResponseData>>
}