package uz.star.mardexworker.ui.screen.main_activity.promocode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.promocode.PromocodeReequestData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Farhod Tohirov on 27-Jun-21
 **/
@HiltViewModel
class PromocodeViewModel @Inject constructor(private val repository: PromocodeRepository, private val storage: LocalStorage) : ViewModel() {

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _successData = MutableLiveData<Boolean>()
    val successData: LiveData<Boolean> get() = _successData


    fun sendPromocode(promoCode: String) {
        _message.addSourceDisposable(repository.sendPromocode(PromocodeReequestData(promoCode.toInt(), "users", storage.id))) {
            it.onData { status ->
                _successData.value = status
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }

}