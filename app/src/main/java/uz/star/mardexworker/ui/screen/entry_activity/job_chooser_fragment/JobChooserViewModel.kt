package uz.star.mardexworker.ui.screen.entry_activity.job_chooser_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.jobs_data.AddJobRequestData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.utils.extensions.updateAllDynamic
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class JobChooserViewModel @Inject constructor(
    private val repository: JobChooserRepository,
    private val storage: LocalStorage
) :
    ViewModel() {
    private val _jobs = MediatorLiveData<List<JobCategory>>()
    val jobs: LiveData<List<JobCategory>> get() = _jobs

    private val _openHomeLiveData = MediatorLiveData<AuthResponseData>()
    val openHomeLiveData: LiveData<AuthResponseData> get() = _openHomeLiveData

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    fun getJobs() {
        _loader.value = true
        _jobs.addSourceDisposable(repository.getJobs()) {
            _loader.value = false
            it.onData { list ->
                _jobs.value = list
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }

    fun addJobs(ls: ArrayList<String>) {
        _loader.value = true
        _openHomeLiveData.addSourceDisposable(repository.addJobs(AddJobRequestData(ls))) {
            _loader.value = false
            it.onData { data ->
                updateAllDynamic(authResponseData = data, storage = storage)
                _openHomeLiveData.value = data
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }
}