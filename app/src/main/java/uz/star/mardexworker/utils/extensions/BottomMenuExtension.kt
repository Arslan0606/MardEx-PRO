package uz.star.mardexworker.utils.extensions

import androidx.fragment.app.Fragment
import uz.star.mardexworker.ui.screen.main_activity.MainActivity

/**
 * Created by Botirali Kozimov on 11-03-21
 **/

fun Fragment.showBottomMenu() {
    try {
        (activity as MainActivity).showBottomMenu()
    } catch (e: Exception) {

    }
}

fun Fragment.hideBottomMenu() {
    try {
        (activity as MainActivity).hideBottomMenu()
    } catch (e: Exception) {

    }
}