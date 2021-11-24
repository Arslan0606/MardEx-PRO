package uz.star.mardexworker.ui.screen.entry_activity.job_chooser_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.Coroutines
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.model.response.jobs_data.AddJobRequestData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 12/2/20.
 */

class JobChooserRepositoryImpl @Inject constructor(
    private val api: JobApi,
    private val storage: LocalStorage
) : JobChooserRepository, SafeApiRequest() {

    override fun getJobs(): LiveData<ResultData<List<JobCategory>>> {
        val resultLiveData = MutableLiveData<ResultData<List<JobCategory>>>()

        Coroutines.ioThenMain(
            { apiRequest { api.getJobs(storage.language) } },
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
                        resultLiveData.value = ResultData.messageData(MessageData.resource(R.string.try_after_a_while))
                    }
                }
            }
        )
        return resultLiveData
    }

    override fun addJobs(addJobRequestData: AddJobRequestData): LiveData<ResultData<AuthResponseData>> {
        val resultLiveData = MutableLiveData<ResultData<AuthResponseData>>()

        Coroutines.ioThenMain(
            { apiRequest { api.addJobs(addJobRequestData) } },
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
                        resultLiveData.value = ResultData.messageData(MessageData.resource(R.string.try_after_a_while))
                    }
                }
            }
        )
        return resultLiveData
    }
}