package uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.local.ResultData

/**
 * Created by Kurganbaev Jasurbek on 28.04.2021 11:09
 **/
interface RestorePasswordRepository {
    fun sendVerificationCode(phone: String): LiveData<ResultData<Boolean>>

}