package uz.star.mardexworker.ui.screen.notification_activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.ActivityNotificationBinding
import uz.star.mardexworker.databinding.DialogPhotoviewBinding
import uz.star.mardexworker.firebase.FirebaseMessagingServiceHelper.Companion.notificationSound
import uz.star.mardexworker.model.request.OpportunityEnum
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.login.LocationRequest
import uz.star.mardexworker.model.response.notification.NotificationResponse
import uz.star.mardexworker.model.response.title.Title
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.adapter.recycler_view.ImagesPagerAdapter
import uz.star.mardexworker.ui.screen.entry_activity.EntryActivity
import uz.star.mardexworker.ui.screen.main_activity.activity_components.MainViewModel
import uz.star.mardexworker.utils.LANG_RU
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.MyContextWrapper
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.loadPictureUrl
import uz.star.mardexworker.utils.extensions.show
import uz.star.mardexworker.utils.helpers.showMessage
import uz.star.mardexworker.utils.toCurrency
import java.text.NumberFormat
import javax.inject.Inject


@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private var _binding: ActivityNotificationBinding? = null
    private val binding: ActivityNotificationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private lateinit var imagesAdapter: ImagesPagerAdapter
    private var countDownTimer: CountDownTimer? = null
    private var count = 20

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var format: NumberFormat

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.connectSocket()
        loadObservers()
        viewModel.getNotification()

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(10001520)
    }

    private fun loadObservers() {
        viewModel.notificationData.observe(this, notificationObserver)
        viewModel.responseOrderPost.observe(this, postOrderObserver)

        viewModel.closeDialogLiveData.observe(this, closeDialogObserver)
    }

    private val closeDialogObserver = Observer<Unit> {
        finish()
    }

    private val postOrderObserver = Observer<FastOrderResponseData> {
        startActivity(Intent(this, EntryActivity::class.java))
        finish()
    }

    companion object {
        var clientId = ""
    }

    private val notificationObserver = Observer<NotificationResponse?> { data ->
        clientId = data.clientId?.id ?: ""
        binding.apply {
            layoutNotification.show()

            btnAccept.setOnClickListener {
                viewModel.getJob(data.clientId?.id ?: "")
                viewModel.setWorkerUnFree()

                val d = SocketResponseData(
                    data.workerId?.id ?: "",
                    data.clientId?.id ?: "",
                    data?.jobId?.title ?: Title(),
                    data.jobDescription ?: "",
                    data.jobPrice ?: "",
                    data.jobDescriptionFull ?: "",
                    data.workerCount ?: 0,
                    data.location ?: LocationRequest("Point", listOf(0.0, 0.0)),
                    data.clientId?.name ?: "",
                    data.clientId?.phone ?: "",
                    data?.images ?: listOf(),
                    data.jobId?.id ?: ""
                )
                viewModel.postOrder(d)
            }

            btnCancel.setOnClickListener {
                viewModel.cancelJob(data.clientId?.id ?: "")
                finish()
            }
        }

        val difTime = data.timeUser - (data.timeClient)

        binding.apply {
            txtClientName.text = data.clientId?.name
            txtTitle.text = when (storage.langLocal) {
                "default" -> {
                    data.jobId?.title?.uz
                }
                LANG_UZ -> {
                    data.jobId?.title?.uz_kr
                }
                LANG_RU -> {
                    data.jobId?.title?.ru
                }
                else -> {
                    data.jobId?.title?.en
                }
            }
            txtWorkTitle.text = data.jobDescription

            if (data.jobPrice == null) {
                hidePrice()
            } else {
                try {
                    txtPrice.text = (data.jobPrice.toLong()).toCurrency()
                } catch (e: Exception) {
                    txtPrice.text = data.jobPrice
                }
            }

            if (data.jobDescriptionFull.isNullOrEmpty()) {
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
                data.images.let {
                    val ls = it.toMutableList()
                    ls.removeAt(0)
                    imagesAdapter.submitList(ls)
                }
            }

            count = 20 - (difTime / 1000).toInt()

            progressBar.setProgressDisplayAndInvalidate(count)
        }

        if (difTime / 1000 < 20) {

            if (storage.callCount > 0)
                viewModel.minusOpportunity(OpportunityEnum.CALL)

            binding.apply {
                btnAccept.show()
                btnCancel.show()
            }

            val time = (difTime / 1000).toInt()

            countDownTimer = object : CountDownTimer((20 - time) * 1_000L, 1_000) {
                override fun onTick(millisUntilFinished: Long) {
                    count--
                    if (count < 0)
                        return
                    binding.progressBar.setProgressDisplayAndInvalidate(count)
                    binding.textSecondCount.text = count.toString()
                }

                override fun onFinish() {
                    binding.progressBar.setProgressDisplayAndInvalidate(0)

                    showMessage(R.string.notification_fail) {
                        finish()
                    }
                    binding.apply {
                        btnAccept.hide()
                        btnCancel.hide()
                    }
//                    noListener?.invoke()
                }
            }.start()
        } else {
            binding.progressBar.setProgressDisplayAndInvalidate(0)
            showMessage(R.string.notification_fail) {
                finish()
            }

            binding.apply {
                btnAccept.hide()
                btnCancel.hide()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LocalStorage.instance.langLocal))
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

    override fun onDestroy() {
        super.onDestroy()
        notificationSound.stop()
        countDownTimer?.cancel()
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