package uz.star.mardexworker.ui.screen.main_activity.profile_fragment.images_fragment

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData

/**
 * Created by Botirali Kozimov on 10/3/21.
 */

interface ImagesRepository {

    fun uploadImage(part: MultipartBody.Part): LiveData<ResultData<ImagePath>>

    fun updateUser(dataEdit: EditProfileRequestData): LiveData<ResultData<AuthResponseData>>

    fun deleteImagesFromServer(ls: List<String>, id: String): LiveData<ResultData<Unit>>
}