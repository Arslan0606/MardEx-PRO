package uz.star.mardexworker.ui.screen.entry_activity.intro_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.intro.IntroData
import uz.star.mardexworker.model.response.local.ResultData


interface IntroRepository {
    fun getIntroData(): LiveData<ResultData<List<IntroData>>>
}