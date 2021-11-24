package uz.star.mardexworker.utils.extensions

import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import com.google.android.material.textview.MaterialTextView
import uz.star.mardexworker.R
import kotlin.math.absoluteValue

/**
 * Created by Botirali Kozimov on 20.03.2021
 */

fun LinearLayout.changeFreeButtonState(state: Boolean, imageView: ImageView, imageArrow: ImageView) {
    background = if (state) {
        layoutDirection = View.LAYOUT_DIRECTION_LTR
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_button))
        imageArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_left_arrow))
        ContextCompat.getDrawable(context, R.drawable.ic_free_button)
    } else {
        layoutDirection = View.LAYOUT_DIRECTION_RTL
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_cancel))
        imageArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_right_arrow))
        ContextCompat.getDrawable(context, R.drawable.ic_unfree_button)
    }
}

fun MaterialTextView.changeFreeTextState(state: Boolean) {
    gravity = if (state) {
        Gravity.END
    } else {
        Gravity.START
    }

}