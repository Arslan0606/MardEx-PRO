package uz.star.mardexworker.utils

import android.content.ActivityNotFoundException
import android.content.Context

import android.content.Intent

import android.content.pm.PackageManager


/**
 * Created by Botirali Kozimov on 04/05/2021
 */

class IntentHelper {
    companion object {
        /** Open another app.
         * @param context current Context, like Activity, App, or Service
         * @param packageName the full package name of the app to open
         * @return true if likely successful, false if unsuccessful
         */
        fun openApp(context: Context, packageName: String?): Boolean {
            val manager: PackageManager = context.packageManager
            return try {
                val i = manager.getLaunchIntentForPackage(packageName!!)
                    ?: return false
                //throw new ActivityNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER)
                context.startActivity(i)
                true
            } catch (e: ActivityNotFoundException) {
                false
            }
        }
    }
}