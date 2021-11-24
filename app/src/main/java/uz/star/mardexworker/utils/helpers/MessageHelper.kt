package uz.star.mardexworker.utils.helpers

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import uz.star.mardexworker.R
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.utils.EmptyBlock

/**
 * Created by Farhod Tohirov on 18-Mar-21
 **/

fun Fragment.showMessage() = Observer<MessageData> {
//    hideLoader()
    it.onMessage {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(it)
            .setPositiveButton("OK", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        dialog.show()
    }.onResource {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(it)
            .setPositiveButton("OK", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        dialog.show()
    }
}

fun Fragment.showMessage(@StringRes text: Int) {
    if (text == R.string.token_expire) {
        try {
            (requireActivity() as MainActivity).logout()
        } catch (e: Exception) {

        }
    }
    if (text == R.string.duplicat_key)
        return
    val dialog = AlertDialog.Builder(requireContext()).setMessage(text).setPositiveButton("OK", null).create()
    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }
    dialog.show()
}

fun Context.showMessage(@StringRes text: Int, dismissListener: EmptyBlock? = null) {
    try {
        val dialog = AlertDialog.Builder(this).setTitle(text).setOnDismissListener { dismissListener?.invoke() }.setPositiveButton("OK", null).create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.new_green))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red))
        }
        dialog.show()
    } catch (e: Exception) {
    }
}


fun Fragment.showAlertDialog(text: String, dismissListener: EmptyBlock? = null) {
    val dialog =
        AlertDialog.Builder(requireContext()).setMessage(text).setOnDismissListener { dismissListener?.invoke() }.setPositiveButton("OK", null)
            .create()
    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }
    dialog.show()
}


fun Fragment.showAlertDialog(text: String, title: String, dismissListener: EmptyBlock? = null): AlertDialog {
    val dialog =
        AlertDialog.Builder(requireContext()).setTitle(title).setMessage(text)
            .setOnDismissListener { dismissListener?.invoke() }.setCancelable(false).setPositiveButton("OK", null)
            .create()
    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }
    dialog.show()
    return dialog
}


fun Fragment.showAlertDialog(text: Int, dismissListener: EmptyBlock? = null) {
    val dialog =
        AlertDialog.Builder(requireContext()).setMessage(text).setOnDismissListener { dismissListener?.invoke() }.setPositiveButton("OK", null)
            .create()
    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }
    dialog.show()
}