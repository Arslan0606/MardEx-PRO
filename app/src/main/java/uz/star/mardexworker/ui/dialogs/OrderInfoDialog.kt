package uz.star.mardexworker.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.DialogOrderDetailsBinding
import uz.star.mardexworker.databinding.DialogPhotoviewBinding
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.ui.adapter.recycler_view.ImagesPagerAdapter
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_RU
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.loadPictureUrl
import uz.star.mardexworker.utils.extensions.show

class OrderInfoDialog(private val isHome: Boolean = false) : BottomSheetDialogFragment() {
    lateinit var groupData: GetOrdersResponse

    private var _binding: DialogOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private var endListener: ((GetOrdersResponse) -> Unit)? = null
    private var navigateListener: ((GetOrdersResponse) -> Unit)? = null
    private var cancelListener: ((GetOrdersResponse) -> Unit)? = null
    private lateinit var imagesAdapter: ImagesPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            if (isHome) {
                callButton.show()
                finishButton.hide()
                card2.hide()
                cancelButton.hide()
            } else {
                callButton.hide()
                finishButton.show()
//                card2.show()
            }
            setupButtons()

            txtClientName.text = groupData.clientId?.name
            textDefinition.text = when (LocalStorage.instance.langLocal) {
                LANG_UZ -> {
                    groupData.jobId?.title?.uz
                }
                LANG_KRILL -> {
                    groupData.jobId?.title?.uz_kr
                }
                LANG_RU -> {
                    groupData.jobId?.title?.ru
                }
                else -> {
                    groupData.jobId?.title?.en
                }
            }

            /*if (groupData.price) {
                hidePrice()
            } else {*/
            /*try {
                txtPrice.text = format.format(groupData.jobPrice.toLong())
            } catch (e: Exception) {
                txtPrice.text = groupData.jobPrice
            }*/
            if (groupData.price != null) {
                workCost.text = groupData.price.toString()
            } else {
                hidePrice()
            }
//            }

            if (groupData.fullDesc?.isEmpty() == true) {
                hideDesc()
            } else {
                textBookingDefinition.text = groupData.fullDesc
            }

            if (groupData.desc?.isEmpty() == true) {
                textDefinition.hide()
            } else {
                textDefinition.text = groupData.desc
            }

            if (groupData.images.isNullOrEmpty()) {
                hideImages()
            } else {
                imagesAdapter = ImagesPagerAdapter()

                imagesAdapter.setOnItemClickListener {
                    showPicture(it)
                }
                list.adapter = imagesAdapter
                groupData.images?.let {
                    val l = arrayListOf<String>()
                    try {
                        it.forEach {
                            l.add(it as String)
                        }
                    } catch (e: Exception) {
                    }
                    imagesAdapter.submitList(l)
                }
            }

            if (groupData.isFinish == true) {
                buttons.hide()
            }
        }
    }

    private fun hideImages() {
        binding.apply {
            detailPhotoTitle.hide()
            list.hide()
        }
    }

    private fun hideDesc() {
        binding.apply {
            textBookingDefinition.hide()
        }
    }

    private fun hidePrice() {
        binding.apply {
            costIcon.hide()
            costTitle.hide()
            workCost.hide()
        }
    }

    private fun callToWorker(phone: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.fromParts("tel", phone, null)
        startActivity(callIntent)
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

    private fun setupButtons() {
        binding.apply {
            btnBack.setOnClickListener {
                dismiss()
            }

            cancelButton.setOnClickListener {
                cancelListener?.invoke(groupData)
                dismiss()
            }

            finishButton.setOnClickListener {
                endListener?.invoke(groupData)
                dismiss()
            }

            buttonNavigate.setOnClickListener {
                navigateListener?.invoke(groupData)
                dismiss()
            }

            callButton.setOnClickListener {
                groupData.clientId?.phone?.let { it1 -> callToWorker(it1) }
            }
        }

    }

    fun clickEnd(f: (GetOrdersResponse) -> Unit) {
        endListener = f
    }

    fun clickCancel(f: (GetOrdersResponse) -> Unit) {
        cancelListener = f
    }

    fun clickNavigate(f: (GetOrdersResponse) -> Unit) {
        navigateListener = f
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


