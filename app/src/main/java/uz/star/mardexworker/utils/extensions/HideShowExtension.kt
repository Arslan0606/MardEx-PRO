package uz.star.mardexworker.utils.extensions

import android.view.View

/**
 * Created by Botirali Kozimov on 10-03-21
 **/

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun View.disableWithAlpha() {
    this.isEnabled = false
    this.alpha = 0.5f
}

fun View.enableWithAlpha() {
    this.isEnabled = true
    this.alpha = 1f
}