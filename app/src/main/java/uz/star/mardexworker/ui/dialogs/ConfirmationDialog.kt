package uz.star.mardexworker.ui.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import uz.star.mardexworker.databinding.DialogConfirmaitonBinding
import uz.star.mardexworker.utils.EmptyBlock

class ConfirmationDialog(
    activity: Activity,
    @StringRes idRes: Int
) : AlertDialog(activity) {

    private var listenClick: EmptyBlock? = null

    private var _binding: DialogConfirmaitonBinding? = null
    private val binding: DialogConfirmaitonBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    init {
        _binding = DialogConfirmaitonBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.txtTitle.text = activity.getString(idRes)
        binding.btnYes.setOnClickListener {
            listenClick?.invoke()
            dismiss()
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }


    override fun onStop() {
        super.onStop()
        _binding = null
    }


    fun setOnclickYesButtonListener(f: EmptyBlock) {
        listenClick = f
    }

}