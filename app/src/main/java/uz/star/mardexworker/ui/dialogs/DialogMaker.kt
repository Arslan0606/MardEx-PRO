package uz.star.mardexworker.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.StringRes
import com.irozon.sneaker.Sneaker
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.ConfirmActionDialogBinding
import uz.star.mardexworker.databinding.ServerResponseDialogBinding
import uz.star.mardexworker.utils.*


object DialogMaker {

    fun showSuccessDialog(context: Context, @StringRes message: Int, response: EmptyBlock) {
        val dialog = AlertDialog.Builder(context).create()
        val binding = ServerResponseDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            topBgLine.setBackgroundResource(R.color.dilog_successfully_top_view_bg)
            dialogMessageImage.setBackgroundResource(R.drawable.dialog_bg_view)
            dialogMessageImage.setImageResource(R.drawable._check)
            responseMessageText.setTextColor(Color.parseColor("#3CB878"))
            responseMessageText.setText(message)
            responseCommandButton.setBackgroundResource(R.drawable.dialog_success_btn_bg)
            responseCommandButton.setText(R.string.davom_etish)
            bottomBgLine.setBackgroundResource(R.color.dilog_successfully_top_view_bg)
            emptyLine.setBackgroundResource(R.color.dialog_successfully_icon_bg)
            responseCommandButton.setOnClickListener { dialog.dismiss(); response() }
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    fun showConfirmDialog(
        context: Context,
        @StringRes header: Int,
        message: String,
        yesClicked: EmptyBlock
    ) {
        val dialog = AlertDialog.Builder(context).create()
        val binding = ConfirmActionDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            messageText.text = message
            headerText.setText(header)
            btnYes.setOnClickListener { dialog.dismiss(); yesClicked() }
            btnNo.setOnClickListener { dialog.dismiss(); }
        }
        dialog.show()
    }

    fun showErrorDialog(context: Context, @StringRes message: Int, response: EmptyBlock) {
        val dialog = AlertDialog.Builder(context).create()
        val binding = ServerResponseDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            topBgLine.setBackgroundResource(R.color.dialog_error_top_view_bg)
            dialogMessageImage.setBackgroundResource(R.drawable.dialog_bg_view_error)
            dialogMessageImage.setImageResource(R.drawable.baseline_clear)
            responseMessageText.setTextColor(Color.parseColor("#F24F5E"))
            responseMessageText.setText(message)
            responseCommandButton.setBackgroundResource(R.drawable.dialog_error_btn_bg)
            responseCommandButton.setText(R.string.qaytadan)
            bottomBgLine.setBackgroundResource(R.color.dialog_error_top_view_bg)
            emptyLine.setBackgroundResource(R.color.dialog_error_top_view_bg)
            binding.responseCommandButton.setOnClickListener { dialog.dismiss();response() }
            dialogMessageImage.setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    fun showErrorDialog(activity: Activity, @StringRes text: Int) {
        val sneaker = Sneaker.with(activity)
            .setDuration(3000)
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.custom_snackbar, sneaker.getView(), false)
        view.findViewById<TextView>(R.id.textTitle).text = ""
        view.findViewById<TextView>(R.id.sneakerErrorMessage).setText(text)
        sneaker.sneakCustom(view)
    }

    fun showSuccessDialog(activity: Activity, @StringRes text: Int) {
        Sneaker.with(activity)
            .setDuration(3000)
            .setMessage(activity.getString(text))
            .sneakSuccess()
    }

    /*   fun showTariffInfoDialog(
           context: Context,
           data: TariffData,
           storage: LocalStorage,
           yesClicked: EmptyBlock,
           shareClicked: EmptyBlock
       ) {
           val title: String?
           val desc: String?

           when (storage.language) {
               LANG_UZ -> {
                   title = data.title[0].uz
                   desc = data.description[0].uz
               }
               LANG_KRILL -> {
                   title = data.title[0].uz_kr
                   desc = data.description[0].uz_kr
               }
               LANG_RU -> {
                   title = data.title[0].ru
                   desc = data.description[0].ru
               }
               LANG_EN -> {
                   title = data.title[0].en
                   desc = data.description[0].en
               }
               else -> {
                   title = data.title[0].uz
                   desc = data.description[0].uz
               }
           }
           val dialog = AlertDialog.Builder(context).create()
           val binding = DialogInfoTariffBinding.inflate(LayoutInflater.from(context))
           dialog.setView(binding.root)
           dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
           binding.apply {
               textName.text = title
               textInfo.text = desc
               btnYes.setOnClickListener { dialog.dismiss(); yesClicked() }
               buttonClose.setOnClickListener { dialog.dismiss(); }
               buttonShare.setOnClickListener { dialog.dismiss(); shareClicked() }
           }
           dialog.show()
       }*/
}

