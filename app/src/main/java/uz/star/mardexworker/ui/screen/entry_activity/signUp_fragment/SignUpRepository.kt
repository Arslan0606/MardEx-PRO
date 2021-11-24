package uz.star.mardexworker.ui.screen.entry_activity.signUp_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.request.CheckData
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.ResponseServer
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.regions.CityResponse
import uz.star.mardexworker.model.response.regions.RegionResponse

/**
 * Created by Raximjanov Davronbek  08.04.2021
 */

interface SignUpRepository {
    fun sendVerificationCode(phone: String): LiveData<ResultData<Boolean>>

    fun checkExsist(data: CheckData): LiveData<ResultData<OpportunityResponse>>

    fun getCities(): LiveData<ResultData<ResponseServer<List<CityResponse>>>>

    fun getRegions(): LiveData<ResultData<ResponseServer<List<RegionResponse>>>>
}