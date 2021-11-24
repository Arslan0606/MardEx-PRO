package uz.star.mardexworker.utils

import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.regions.Title

/**
 * Created by Botirali Kozimov on 27/06/2021
 */

fun Title.toLocalString(): String? {
    return when (LocalStorage.instance.langLocal) {
        "uz" -> {
            this.uz
        }
        "ru" -> {
            this.ru
        }
        else -> {
            this.uzKr
        }
    }
}