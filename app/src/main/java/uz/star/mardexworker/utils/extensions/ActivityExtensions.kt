package uz.star.mardexworker.utils.extensions

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast

fun Activity.windowSetFullScreen() {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.resetScreen() {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.showToast(string: String) {
    Toast.makeText(this, "$string", Toast.LENGTH_SHORT).show()
}


