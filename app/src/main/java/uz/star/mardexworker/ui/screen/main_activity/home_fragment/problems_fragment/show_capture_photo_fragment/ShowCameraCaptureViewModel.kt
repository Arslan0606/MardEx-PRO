package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.show_capture_photo_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.UpdatePassportData
import uz.star.mardexworker.model.request.signup.PassportData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import java.io.File
import javax.inject.Inject

/**
 * Created by Kurganbaev Jasurbek on 19.04.2021 13:17
 **/
@HiltViewModel
class ShowCameraCaptureViewModel @Inject constructor(
    private val profileApi: ShowCameraCaptureRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _uploadCameraCaptureLiveData = MediatorLiveData<ImagePath>()
    val uploadCameraCaptureLiveData: LiveData<ImagePath> get() = _uploadCameraCaptureLiveData

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    private val _passportWithFaceLiveData = MediatorLiveData<AuthResponseData>()
    val passportWithFaceLiveData: LiveData<AuthResponseData> get() = _passportWithFaceLiveData

    private var photosList = ArrayList<String>()

    fun uploadPassportPicture(file: File?) {
        if (file == null) return

        _loader.value = true
        val part = MultipartBody.Part.createFormData(
            "image", "image.jpg", RequestBody.create(
                "image/JPEG".toMediaTypeOrNull(),
                file.readBytes()
            )
        )

        _uploadCameraCaptureLiveData.addSourceDisposable(profileApi.uploadCameraCapture(part)) {
            _loader.value = false
            it.onData { imagePath ->
                photosList.add(imagePath.path)
                _uploadCameraCaptureLiveData.value = imagePath

                if (photosList.size == 3) pathUserData()

            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResource ->
                    _message.value = MessageData.resource(stringResource)
                }
            }
        }
    }

    private fun pathUserData() {
        _loader.value = true
        _passportWithFaceLiveData.addSourceDisposable(
            profileApi.updatePassport(
                UpdatePassportData(
                    PassportData(
                        scan = photosList[0],
                        number = 0,
                        backScan = photosList[1],
                        scanWithFace = photosList[2],
                        seria = "",
//                        isActive = true
                        isActive = false
                    )
                )
            )
        ) {
            it.onData {
                updateAllDynamic(authResponseData = it, storage = storage)
                _passportWithFaceLiveData.value = it
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResource ->
                    _message.value = MessageData.resource(stringResource)
                }
            }
            _loader.value = false
        }
    }
}