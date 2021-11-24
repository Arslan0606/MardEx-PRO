package uz.star.mardexworker.ui.screen.entry_activity.login_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData


/**
 * Created by Raximjanov Davronbek  08.04.2021
 */

interface LoginRepository {
    fun login(loginData: LoginData): LiveData<ResultData<AuthResponseData>>
    fun sendNotificationTokenToServer(): LiveData<ResultData<AuthResponseData>>

}