package uz.star.mardexworker.utils.extensions

import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.model.response.title.Title
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_UZ

/**
 * Created by Farhod Tohirov on 18-Mar-21
 **/

fun Title.title(): String? {
    val localStorage = LocalStorage.instance
    return if (localStorage.language == LANG_UZ) this.uz else if (localStorage.language == LANG_KRILL) this.uz_kr else this.ru
}

