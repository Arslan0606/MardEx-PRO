package uz.star.mardexworker.utils.extensions

import androidx.fragment.app.Fragment
import uz.star.mardexworker.ui.screen.entry_activity.EntryActivity
import uz.star.mardexworker.ui.screen.main_activity.MainActivity

/**
 * Created by Botirali Kozimov on 10-03-21
 **/

fun Fragment.showEntryLoader() {
    (activity as EntryActivity).showLoader()
}

fun Fragment.hideEntryLoader() {
    (activity as EntryActivity).hideLoader()
}

fun Fragment.showMainLoader() {
    (activity as MainActivity).showLoader()
}

fun Fragment.hideMainLoader() {
    (activity as MainActivity).hideLoader()
}