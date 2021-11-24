package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.show_capture_photo_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.request.UpdatePassportData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileRepository
import javax.inject.Inject

/**
 * Created by Kurganbaev Jasurbek on 20.04.2021 13:29
 **/

class ShowCameraCaptureRepositoryImpl @Inject constructor(
    private val api: JobApi,
    private val storage: LocalStorage
) : ShowCameraCaptureRepository, SafeApiRequest() {


    override fun uploadCameraCapture(part: MultipartBody.Part): LiveData<ResultData<ImagePath>> {
        val resultLiveData = MutableLiveData<ResultData<ImagePath>>()

        Coroutines.ioThenMain(
            { apiRequest { api.uploadPicture(part) } },
            { data ->
                data?.onData {
                    resultLiveData.value = ResultData.data(it)
                }
                data?.onMessage {
                    resultLiveData.value = ResultData.message(it)
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.messageData(MessageData.resource(it))
                    }
                }
            }
        )
        return resultLiveData
    }

    override fun updatePassport(passportData: UpdatePassportData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.updatePassport(passportData) } },
            { data ->
                data?.onData {
                    if (it.success) {
                        resultLiveData.value = ResultData.data(it.data)
                    } else
                        resultLiveData.value = ResultData.message("list bosh")
                }
                data?.onMessage {
                    resultLiveData.value = ResultData.message(it)
                }?.onMessageData { messageData ->
                    messageData.onResource {
                        resultLiveData.value = ResultData.messageData(MessageData.resource(it))
                    }
                }
            }
        )
        return resultLiveData
    }
}