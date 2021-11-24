package uz.star.mardexworker.ui.screen.main_activity.profile_fragment.images_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.response.DeleteImagesData
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

class ImagesRepositoryImpl @Inject constructor(
    private val api: JobApi
) : ImagesRepository, SafeApiRequest() {

    override fun uploadImage(part: MultipartBody.Part): LiveData<ResultData<ImagePath>> {
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

    override fun updateUser(dataEdit: EditProfileRequestData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.updateInfo(dataEdit) } },
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

    override fun deleteImagesFromServer(ls: List<String>, id: String): LiveData<ResultData<Unit>> {
        val resultLiveData = MutableLiveData<ResultData<Unit>>()
        Coroutines.ioThenMain(
            { apiRequest { api.deleteImages(id, DeleteImagesData(ls)) } },
            { data ->
                data?.onData {
                    resultLiveData.value = ResultData.data(Unit)
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