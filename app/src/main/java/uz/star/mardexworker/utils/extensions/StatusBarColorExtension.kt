package uz.star.mardexworker.utils.extensions

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.star.mardexworker.R

/**
 * Created by Farhod Tohirov on 21-Mar-21
 **/

fun Fragment.changeStatusColorWhite() {
    requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
}


fun Fragment.changeStatusColorMainColor() {
    requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_color)
}