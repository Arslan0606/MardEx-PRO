package uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _responseOrderPatch = MediatorLiveData<FastOrderResponseData>()
    val responseOrderPatch: LiveData<FastOrderResponseData> get() = _responseOrderPatch

    private val _responseDelete = MediatorLiveData<Any>()
    val responseDelete: LiveData<Any> get() = _responseDelete

    private val _orders = MediatorLiveData<List<GetOrdersResponse>>()
    val orders: LiveData<List<GetOrdersResponse>> get() = _orders

    private var _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    fun patchOrder(data: GetOrdersResponse, isFinish: Boolean = true) {
        val l = arrayListOf<String>()
        try {
            data.images?.forEach {
                l.add(it as String)
            }
        } catch (e: Exception) {
        }
        val d = FastOrderRequestData(
            data.fullDesc,
            data.userId?.id,
            data.workCount,
            data.jobId?.id,
            data.jobId?.title,
            data.price,
            data.clientId?.id,
            (data.location?.coordinates?.get(0)?.toString() ?: 0).toString() + "," + (data.location?.coordinates?.get(1) ?: 0),
            data.desc,
            l,
            isFinish,
            data.status
        )
        _responseOrderPatch.addSourceDisposable(repository.patchFastPost(data.id ?: "", d)) {
            it.onData { res ->
                _responseOrderPatch.value = res
                removeOrderFromClient(data.clientId?.id ?: "")
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    private fun removeOrderFromClient(id: String) {
        _responseDelete.addSourceDisposable(repository.deleteOrderFromClient(id)) {
            it.onData { res ->
                _responseDelete.value = res
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }

    fun getOrders() {
        val workerId = storage.id
        _orders.addSourceDisposable(repository.getOrders(workerId)) {
            it.onData { data ->
                _orders.value = data
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { messageData ->
                messageData.onResource { stringResources ->
                    _message.value = MessageData.resource(stringResources)
                }
            }
        }
    }
}