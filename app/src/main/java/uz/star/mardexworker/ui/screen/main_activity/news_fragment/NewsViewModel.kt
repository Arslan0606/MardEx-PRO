package uz.star.mardexworker.ui.screen.main_activity.news_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.news.NewsData
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileRepository
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 27.06.2021 14:42
 **/

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _news = MediatorLiveData<List<NewsData>>()
    val news: LiveData<List<NewsData>> get() = _news


    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message


    fun getNews() {
        _loader.value = true
        _news.addSourceDisposable(newsRepository.getNews()) {
            it.onData { list ->
                _news.value = list
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

}