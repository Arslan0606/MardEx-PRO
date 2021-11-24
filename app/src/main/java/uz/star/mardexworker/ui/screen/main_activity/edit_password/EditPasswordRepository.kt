package uz.star.mardexworker.ui.screen.main_activity.edit_password

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.UpdatePasswordData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData

interface EditPasswordRepository {
    fun updatePassword(userId: String, updatePasswordData: UpdatePasswordData): LiveData<ResultData<AuthResponseData>>
}