package uz.star.mardexworker.ui.screen.main_activity.edit_personal_data_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.UpdatePasswordData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData

/**
 * Created by Kurganbaev Jasurbek on 03.05.2021 17:26
 **/

interface EditPersonalDataRepository {

    fun getWorker(workerId: String): LiveData<ResultData<WorkerResponseData>>

    fun updateUser(dataEdit: EditProfileRequestData): LiveData<ResultData<AuthResponseData>>

}