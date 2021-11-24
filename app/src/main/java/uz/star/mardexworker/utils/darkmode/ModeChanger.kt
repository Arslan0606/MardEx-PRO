package uz.star.mardexworker.utils.darkmode

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ModeChanger {

    companion object {
        fun setMode(isNightMode: Boolean, context: Context) {
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("mode_night", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("isNightMode", isNightMode)
            editor.apply()
            editor.commit()
        }

        fun getStatusMode(context: Context) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("mode_night", Context.MODE_PRIVATE)
            val boolean = sharedPreferences.getBoolean("isNightMode", false)

            if (boolean) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun getBooleanStatusMode(context: Context): Boolean {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("mode_night", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("isNightMode", false)
        }
    }
}