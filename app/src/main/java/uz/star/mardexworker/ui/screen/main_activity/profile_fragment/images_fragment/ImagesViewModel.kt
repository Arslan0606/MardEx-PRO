package uz.star.mardexworker.ui.screen.main_activity.profile_fragment.images_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import java.io.File
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val repository: ImagesRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _responseUserUpdateData = MediatorLiveData<AuthResponseData>()
    val responseUserUpdateData: LiveData<AuthResponseData> get() = _responseUserUpdateData

    private val _responseDeleteImages = MediatorLiveData<Unit>()
    val responseDeleteImages: LiveData<Unit> get() = _responseDeleteImages

    private val _addImageLiveData = MediatorLiveData<ImagePath>()
    val addImageLiveData: LiveData<ImagePath> get() = _addImageLiveData

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    fun uploadImage(file: File?) {
        if (file == null) return
        _loader.value = true
        val part = MultipartBody.Part.createFormData(
            "image", "image.jpg", RequestBody.create(
                "image/JPEG".toMediaTypeOrNull(),
                file.readBytes()
            )
        )

        _addImageLiveData.addSourceDisposable(repository.uploadImage(part)) {
            it.onData { imagePath ->
                _addImageLiveData.value = imagePath
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
            _loader.value = false
        }
    }

    fun update(data: EditProfileRequestData, ls: ArrayList<String>) {
        _loader.value = true
        _responseUserUpdateData.addSourceDisposable(repository.updateUser(data)) {
            it.onData { loginResponse ->
                deleteImages(ls)
                updateAllDynamic(authResponseData = loginResponse, storage = storage)
                _responseUserUpdateData.value = loginResponse
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
            _loader.value = false
        }
    }

    private fun deleteImages(ls: List<String>) {
        _responseDeleteImages.addSourceDisposable(repository.deleteImagesFromServer(ls, storage.id)) {
            it.onData { data ->
                _responseDeleteImages.value = data
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }
}