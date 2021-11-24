package uz.star.mardexworker.utils.extensions

import android.util.Log

/**
 * Created by Botirali Kozimov on 12/23/20.
 */

class NumberExtensions {
    companion object {
        fun createUniqueNumber(): String {
            var str = ""
            for (i in 1..4) {
                val t = (10..99).random()
                str += t
            }
            Log.d("EEEEEE", str)
            return str
        }

    }
}