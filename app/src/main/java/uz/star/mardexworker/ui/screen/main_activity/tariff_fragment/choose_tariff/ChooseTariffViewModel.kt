package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.choose_tariff

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.Payment
import uz.star.mardexworker.model.response.tariff.TariffData
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class ChooseTariffViewModel @Inject constructor(
    private val model: ChooseTariffRepository,
    private val storage: LocalStorage
) : ViewModel() {
    private val _chooseServiceData = MediatorLiveData<AuthResponseData>()
    val chooseServiceData: LiveData<AuthResponseData> get() = _chooseServiceData

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    fun chooseService(tariffData: TariffData, balance: Long) {
        if (balance >= tariffData.price) {
            _loader.value = true

            val payment = Payment(
                price = tariffData.price,
                top = tariffData.top,
                call_count = tariffData.call,
                post = tariffData.post,
                service_id = tariffData._id,
                deadline = tariffData.deadline,
                service = tariffData.title
            )
            _chooseServiceData.addSourceDisposable(model.chooseService(storage.id, payment)) {
                it.onData { loginResponse ->
                    _chooseServiceData.value = loginResponse
                }.onMessage { message ->
                    _message.value = MessageData.message(message)
                }.onMessageData { data ->
                    _message.value = data
                }
                _loader.value = false
            }
        } else {
            _message.value = MessageData.resource(R.string.dont_enough_money)
        }
    }
}