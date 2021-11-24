package uz.star.mardexworker.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.DialogCallNotificationBinding
import uz.star.mardexworker.databinding.DialogPhotoviewBinding
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.adapter.recycler_view.ImagesPagerAdapter
import uz.star.mardexworker.utils.EmptyBlock
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_RU
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.loadPictureUrl
import uz.star.mardexworker.utils.views.goneView
import uz.star.mardexworker.utils.views.showView
import java.text.NumberFormat

/**
 * dialog
 * a
 */

class CallNotificationDialog(
    activity: Activity,
    private val data: SocketResponseData,
    private val storage: LocalStorage,
    format: NumberFormat
) : AlertDialog(activity) {

    private var okListener: EmptyBlock? = null
    private var noListener: EmptyBlock? = null
    private lateinit var imagesAdapter: ImagesPagerAdapter
    private var countDownTimer: CountDownTimer? = null
    private var count = 20

    private var _binding: DialogCallNotificationBinding? = null
    private val binding: DialogCallNotificationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    init {
        _binding = DialogCallNotificationBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setGravity(Gravity.TOP)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setupButtons()

        binding.apply {
            txtClientName.text = data.userName
            txtTitle.text = when (storage.langLocal) {
                "default" -> {
                    data.jobName.uz
                }
                LANG_UZ -> {
                    data.jobName.uz_kr
                }
                LANG_RU -> {
                    data.jobName.ru
                }
                else -> {
                    data.jobName.en
                }
            }
            txtWorkTitle.text = data.jobDescription

            if (data.jobPrice.isEmpty()) {
                hidePrice()
            } else {
                /*try {
                    txtPrice.text = format.format(data.jobPrice.toLong())
                } catch (e: Exception) {
                    txtPrice.text = data.jobPrice
                }*/
                txtPrice.text = data.jobPrice
            }

            if (data.jobDescriptionFull.isEmpty()) {
                hideDesc()
            } else {
                txtDesc.text = data.jobDescriptionFull
            }

            if (data.images.isNullOrEmpty()) {
                hideImages()
            } else {
                imagesAdapter = ImagesPagerAdapter()

                imagesAdapter.setOnItemClickListener {
                    showPicture(it)
                }
                list.adapter = imagesAdapter
                data.images?.let {
                    val ls = it.toMutableList()
                    ls.removeAt(0)
                    Log.d("TTTT", ls.toString())
                    imagesAdapter.submitList(ls)
                }
            }

            progressBar.setProgressDisplayAndInvalidate(count)
        }

        countDownTimer = object : CountDownTimer(20_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                count--
                binding.progressBar.setProgressDisplayAndInvalidate(count)
                binding.textSecondCount.text = count.toString()
            }

            override fun onFinish() {
                count--
                binding.progressBar.setProgressDisplayAndInvalidate(0)
                noListener?.invoke()
                dismiss()
            }
        }.start()
    }

    private fun hideImages() {
        binding.apply {
            txtPhoto.hide()
            list.hide()
        }
    }

    private fun hideDesc() {
        binding.apply {
            txtDesc.hide()
        }
    }

    private fun hidePrice() {
        binding.apply {
            imgMoney.hide()
            txtPrice.hide()
            txtPM.hide()
        }
    }

    private fun setupButtons() {
        binding.apply {
            btnAccept.setOnClickListener {
                okListener?.invoke()
                dismiss()
            }

            btnCancel.setOnClickListener {
                noListener?.invoke()
                dismiss()
            }

            btnMore.setOnClickListener {
                btnMore.goneView()
                svContentMore.showView(true)
            }
        }

    }

    fun setOnGetJobListener(function: EmptyBlock) {
        okListener = function
    }

    fun setCancelListener(function: EmptyBlock) {
        noListener = function
    }

    private fun showPicture(it: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(binding.root.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()
        val bindingDialog = DialogPhotoviewBinding.inflate(LayoutInflater.from(binding.root.context))

        binding.root.context.resources?.displayMetrics?.heightPixels
        val height = binding.root.context.resources?.displayMetrics?.heightPixels
        val width = binding.root.context.resources?.displayMetrics?.widthPixels
        bindingDialog.apply {
            if (height != null && width != null) {
                imageView.minimumHeight = height
                imageView.minimumWidth = width
            }
            imageView.loadPictureUrl(it)
            btnBack.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setView(root)
            dialog.show()
        }
    }
}