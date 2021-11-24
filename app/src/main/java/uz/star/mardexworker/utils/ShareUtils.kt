package uz.star.mardexworker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import uz.star.mardexworker.app.App

fun AppCompatActivity.shareText(text: String) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(sharingIntent, "Yuborish"))
}

fun Fragment.appUrl() =
    "To'liq korish:" + "https://xsoft.uz"

fun Context.playStoreUrl() =
    "Hoziroq yuklab oling!: " + "https://play.google.com/store/apps/details?id=${this.applicationContext?.packageName}"


@SuppressLint("HardwareIds")
fun createUniqueDeviceID(): String {
    val id = Settings.Secure.getString(
        App.instance.contentResolver, Settings.Secure.ANDROID_ID
    )
    return id
}




