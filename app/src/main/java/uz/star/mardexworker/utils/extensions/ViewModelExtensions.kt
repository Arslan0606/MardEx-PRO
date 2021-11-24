package uz.star.mardexworker.utils.extensions

import androidx.lifecycle.ViewModel
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData

/**
 * Created by Botirali Kozimov on 16/04/2021
 */

fun ViewModel.updateAllDynamic(
    workerResponseData: WorkerResponseData? = null,
    authResponseData: AuthResponseData? = null,
    storage: LocalStorage
) {
    workerResponseData?.let {
        storage.balance = it.balance
        storage.callCount = it.call
        storage.topCount = it.top.count
        storage.postCount = it.post
        storage.name = it.fullName
        storage.phone = it.phone
        storage.avatar = it.avatar.toString()
        storage.payId = it.payment_id
        storage.description = it.description
    }

    authResponseData?.let {
        storage.balance = it.balance
        storage.callCount = it.call
        storage.topCount = it.top.count
        storage.postCount = it.post
        storage.name = it.fullName
        storage.phone = it.phone
        storage.avatar = it.avatar.toString()
        storage.payId = it.payment_id
        storage.description = it.description
    }
}