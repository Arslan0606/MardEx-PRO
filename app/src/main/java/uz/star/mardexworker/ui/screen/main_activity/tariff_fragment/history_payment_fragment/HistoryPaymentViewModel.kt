package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.history_payment_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.models.tariff.statsrvmodel.Type
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.tariff.statsrvmodel.StatsInnerModel
import uz.star.mardexworker.model.response.tariff.statsrvmodel.StatsOuterModel
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_RU
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import java.util.*
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@HiltViewModel
class HistoryPaymentViewModel @Inject constructor(
    private val model: HistoryPaymentRepository,
    private val storage: LocalStorage
) : ViewModel() {
    private val _paymentsData = MediatorLiveData<List<StatsOuterModel>>()
    val paymentsData: LiveData<List<StatsOuterModel>> get() = _paymentsData

    private val _message = MutableLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    fun getPayments() {
        val paymentId = storage.paymentId

        _loader.value = true
        _paymentsData.addSourceDisposable(model.getPayments(paymentId)) {
            it.onData { data ->
                val calendar = Calendar.getInstance()
                val mapDate = hashMapOf<String, Long>()
                val statsInnerList = arrayListOf<StatsInnerModel>()
                data.allPaidPayments?.forEach {
                    it.payments.forEach {
                        var str: String? = null
                        it.service?.apply {
                            str = when (storage.langLocal) {
                                LANG_RU -> {
                                    ru
                                }
                                LANG_UZ -> {
                                    uz
                                }
                                LANG_KRILL -> {
                                    uz_kr
                                }
                                else -> {
                                    en
                                }
                            }
                        }

                        statsInnerList.add(
                            StatsInnerModel(
                                it._id,
                                -it.price,
                                it.createdAt,
                                str.toString(),
                                Type.MINUS
                            )
                        )
                        it.createdAt?.let {
                            calendar.timeInMillis = it
                            val c = Calendar.getInstance()
                            c.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DATE),
                                0,
                                0,
                                0
                            )
                            mapDate[c.time.toString()] = c.timeInMillis
                        }
                    }
                }
                data.data?.forEach { d ->
                    d.payments?.forEach {
                        val amount = if (d.nomi != "click") it.amount / 100 else it.amount
                        statsInnerList.add(
                            StatsInnerModel(
                                it._id,
                                amount,
                                it.perform_time,
                                d.nomi,
                                Type.PLUS
                            )
                        )
                        it.perform_time.let {
                            calendar.timeInMillis = it
                            val c = Calendar.getInstance()
                            c.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DATE),
                                0,
                                0,
                                0
                            )
                            mapDate[c.time.toString()] = c.timeInMillis
                        }
                    }
                }

                val statsOuterModel = arrayListOf<StatsOuterModel>()
                mapDate.forEach { b ->
                    var amount: Long = 0
                    val sorted = statsInnerList.filter { statsInnerModel ->
                        calendar.timeInMillis = statsInnerModel.perform_time!!
                        val a = Calendar.getInstance()
                        a.timeInMillis = b.value
                        a.add(Calendar.DAY_OF_MONTH, 1)
                        a.set(Calendar.HOUR_OF_DAY, 0)
                        a.set(Calendar.MINUTE, 0)
                        a.set(Calendar.SECOND, 0)

                        val boolean =
                            calendar.timeInMillis >= b.value &&
                                    calendar.timeInMillis < a.timeInMillis

                        if (boolean) {
                            amount += statsInnerModel.amount
                        }
                        return@filter boolean
                    }
                    statsOuterModel.add(
                        StatsOuterModel(
                            amount,
                            b.value,
                            sorted
                        )
                    )

                    statsOuterModel.sortBy {
                        it.date
                    }
                }
                _paymentsData.value = statsOuterModel
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { data ->
                _message.value = data
            }
            _loader.value = false
        }
    }
}