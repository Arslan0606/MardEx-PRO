package uz.star.mardexworker.ui.screen.main_activity.promocode

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.request.promocode.PromocodeReequestData
import uz.star.mardexworker.model.response.local.ResultData

/**
 * Created by Farhod Tohirov on 27-Jun-21
 **/

interface PromocodeRepository {
    fun sendPromocode(promocodeRequestData: PromocodeReequestData): LiveData<ResultData<Boolean>>
}