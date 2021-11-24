package uz.star.mardexworker.ui.screen.main_activity.news_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.data.remote.api.NewsApi
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.news.NewsData
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 27.06.2021 14:45
 **/
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val storage: LocalStorage
) : NewsRepository, SafeApiRequest() {

    override fun getNews(): LiveData<ResultData<List<NewsData>>> {
        val resultLiveData = MutableLiveData<ResultData<List<NewsData>>>()

        Coroutines.ioThenMain({
            apiRequest { newsApi.getNews() }
        },
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

            })
        return resultLiveData
    }


}