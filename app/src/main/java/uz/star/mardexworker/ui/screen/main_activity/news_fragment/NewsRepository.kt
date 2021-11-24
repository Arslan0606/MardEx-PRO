package uz.star.mardexworker.ui.screen.main_activity.news_fragment

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.news.NewsData

/**
 * Created by Jasurbek Kurganbaev on 27.06.2021 14:45
 **/
interface NewsRepository {
    fun getNews(): LiveData<ResultData<List<NewsData>>>
}