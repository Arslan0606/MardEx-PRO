package uz.star.mardexworker.utils.views

import android.view.View
import android.widget.Button

fun View.showView(state: Boolean) {
    this.visibility = if (state) View.VISIBLE else View.INVISIBLE
}

fun View.goneView() {
    this.visibility = View.GONE
}

fun Button.isVisibleState(state: Boolean) {
    this.visibility = if (state) View.VISIBLE else View.GONE
}


