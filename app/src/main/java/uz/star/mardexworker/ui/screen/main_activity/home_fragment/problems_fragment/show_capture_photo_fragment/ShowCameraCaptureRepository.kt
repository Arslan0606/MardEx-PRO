package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.show_capture_photo_fragment

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.request.UpdatePassportData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData

/**
 * Created by Kurganbaev Jasurbek on 20.04.2021 13:29
 **/
interface ShowCameraCaptureRepository {

    fun uploadCameraCapture(part: MultipartBody.Part): LiveData<ResultData<ImagePath>>

    fun updatePassport(passportData: UpdatePassportData): LiveData<ResultData<AuthResponseData>>

}