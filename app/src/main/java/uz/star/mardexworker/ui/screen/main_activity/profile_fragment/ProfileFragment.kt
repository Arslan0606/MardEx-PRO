package uz.star.mardexworker.ui.screen.main_activity.profile_fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.DialogPhotoviewBinding
import uz.star.mardexworker.databinding.FragmentProfileBinding
import uz.star.mardexworker.databinding.LayoutChooseAvatarOptionDialogBinding
import uz.star.mardexworker.databinding.LayoutChooseDialogBinding
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.jobs_data.EditJobsDeliverData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.jobs_data.JobData
import uz.star.mardexworker.model.response.jobs_data.UserJobsDeliver
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.login.rating
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.ui.adapter.recycler_view.ImagesRVAdapter
import uz.star.mardexworker.ui.adapter.recycler_view.JobItemRVAdapter
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsActivity
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatus
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatusLiveData
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.helpers.ImageHelper
import uz.star.mardexworker.utils.helpers.showAlertDialog
import uz.star.mardexworker.utils.helpers.showMessage
import uz.star.mardexworker.utils.toCurrency
import uz.star.mardexworker.utils.views.goneView
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var api: String

    @Inject
    lateinit var format: NumberFormat

    @Inject
    lateinit var gpsStatusLiveData: GpsStatusLiveData

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var jobsAdapter: JobItemRVAdapter

    private val imagesAdapter: ImagesRVAdapter = ImagesRVAdapter()

    private var jobsData: ArrayList<JobData>? = null

    private var editData: EditProfileRequestData? = null
    private var paymentStatusData: CheckPaymentStatusData? = null

    private lateinit var uri: Uri
    private var image: File? = null
    private var toAvatar = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        showBottomMenu()
        jobsAdapter = JobItemRVAdapter(storage.language, false)
        loadObservers()
        loadViews()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadViews() {
        binding.apply {
            notification.setOnClickListener {
//                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNewsFragment())
                startActivity(Intent(requireActivity(), OwnNotificationsActivity::class.java))
            }
            layoutBalance.btnPlusHistory.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHistoryPaymentFragment())
            }
            layoutDesc.layoutDescription.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditPersonalDataFragment())
            }
            buttonFreeUnfree.setOnClickListener {
                if (!storage.orderType) {
                    showAlertDialog(R.string.finish_current_order)
                    return@setOnClickListener
                }
                if (storage.isActive && paymentStatusData?.status == true && viewModel.getGpsStatus()) {
                    if (!storage.freeState) {
                        if (storage.callCount == 0) {
                            showMessage(R.string.call_count_is_null)
                        } else {
                            storage.freeState = true
                            makeWorkerFree()
                        }
                    } else {
                        storage.freeState = false
                        makeWorkerUnFree()
                    }
                } else {
                    hideBottomMenu()
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProblemsFragment())
                }
            }

            outerLayout.changeFreeButtonState(storage.freeState, imageCheck, imageArrow)
            textFreeUnfree.changeFreeTextState(storage.freeState)

            if (storage.freeState)
                binding.textFreeUnfree.text = getString(R.string.free)/* else getString(R.string.unfree)*/

            layoutJobs.apply {
                listJobs.adapter = jobsAdapter
                listJobs.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                buttonSettingsJobs.setOnClickListener {
                    viewModel.getJobs()
                }

                btnAddJobs.setOnClickListener {
                    viewModel.getJobs()
                }
            }

            layoutBalance.buttonBalanceIdCopy.setOnClickListener {
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", layoutBalance.textId.text)
                clipboardManager.setPrimaryClip(clipData)

                showToast(R.string.adder_to_clipboard)
            }

            layoutBalance.btnPlusBalance.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBalancePaymentFragment())
            }

            swipeRefresh.setOnRefreshListener {
                try {
                    Executors.newSingleThreadExecutor().execute {
                        Thread.sleep(2000)
                        activity?.runOnUiThread {
                            swipeRefresh.isRefreshing = false
                            viewModel.getWorker()
                        }
                    }
                } catch (e: Exception) {
                }
            }

            layoutPhotos.listPhotos.adapter = imagesAdapter
            layoutPhotos.listPhotos.layoutManager =
                GridLayoutManager(requireContext(), 3)
            layoutPhotos.buttonSettingsPhotos.setOnClickListener {
                val ls = imagesAdapter.currentList.toMutableList()
                ls.remove("Add")
                val array = ls.toTypedArray()
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToImagesFragment(array, editData)
                )
                hideBottomMenu()
            }

            buttonAvatarChange.setOnClickListener {
                toAvatar = true
                showSuggestDialogForAvatar()
            }

            imageAvatar.setOnClickListener {
                editData?.avatar?.let { it1 -> showPicture(it1) }
            }

            imagesAdapter.setOnAddListener {
                toAvatar = false
                showSuggestDialog()
            }

            imagesAdapter.setOnListenerImageClick {
                showPicture(it)
            }

            menuButton.setOnClickListener {
                (requireActivity() as MainActivity).openDrawer()
            }

            loadDataFromStorage()
//            viewModel.getWorker()
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(EDIT_PROFILE)
            ?.observe(this) { result ->
                val state = result.getBoolean(EDIT_PROFILE)
                if (state)
                    viewModel.getWorker()
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Bundle>(EDIT_PROFILE)
            }
    }

    private fun loadDataFromStorage() {
        binding.apply {
            val str = (storage.balance).toCurrency()
            layoutBalance.textBalance.text = str
            layoutTariffCount.textCall.text = if (storage.callCount < 0) 0.toString() else storage.callCount.toString()
            layoutTariffCount.textPost.text = storage.postCount.toString()
            layoutTariffCount.textTop.text = storage.topCount.toString()
            textName.text = storage.name
            layoutBalance.textId.text = storage.payId
            imageAvatar.loadPictureUrl(storage.avatar)
        }
    }

    private fun makeWorkerFree() {
        (requireActivity() as MainActivity).viewModel.setWorkerFree()
    }

    private fun makeWorkerUnFree() {
        (requireActivity() as MainActivity).viewModel.setWorkerUnFree()
    }

    private fun showPicture(it: String) {
        val dialog = AlertDialog.Builder(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()
        val bindingDialog = DialogPhotoviewBinding.inflate(LayoutInflater.from(requireContext()))

        requireContext().resources?.displayMetrics?.heightPixels
        val height = requireContext().resources?.displayMetrics?.heightPixels
        val width = requireContext().resources?.displayMetrics?.widthPixels
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

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        gpsStatusLiveData.observe(
            viewLifecycleOwner,
            gpsObserver
        )
        viewModel.jobs.observe(this, jobsObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
        viewModel.responseUserUpdateData.observe(viewLifecycleOwner, responseUserUpdateDataObserver)
        viewModel.responseUserData.observe(viewLifecycleOwner, responseUserDataObserver)
        viewModel.editAvatarLiveData.observe(this, editAvatarLiveDataObserver)
        viewModel.successOrdersLiveData.observe(this, successOrdersLiveDataObserver)
        viewModel.cancelOrdersLiveData.observe(this, cancelOrdersLiveDataObserver)
        viewModel.addImageLiveData.observe(this, addImageLiveDataObserver)
        viewModel.checkPaymentStatus.observe(viewLifecycleOwner, getCheckPaymentStatus)
        (requireActivity() as MainActivity).viewModel.worker.observe(this, responseUserDataObserver)
        (requireActivity() as MainActivity).viewModel.freeState.observe(
            viewLifecycleOwner,
            freeStateObserver
        )

        viewModel.getUnReadOwnNotificationsCount()
        viewModel.unReadNotifications.observe(viewLifecycleOwner, unReadNotificationsObserver)
    }

    private val unReadNotificationsObserver = Observer<List<OwnNotificationResponse>> {
        if (it.isNotEmpty() && it != null) {
            binding.txtCountNotifications.text = it.size.toString()
            binding.txtCountNotifications.show()
        } else {
            binding.txtCountNotifications.goneView()
        }
    }


    private val gpsObserver = Observer<GpsStatus> { status ->
        status?.let {
            updateGpsCheckUI(status)
        }
    }

    private fun updateGpsCheckUI(status: GpsStatus) {
        val b = when (status) {
            is GpsStatus.Enabled -> {
                viewModel.getCheckPaymentStatus()
                true
            }
            is GpsStatus.Disabled -> {
                storage.freeState = false
                makeWorkerUnFree()
                changeButtonProblems()
                false
            }
        }
        viewModel.setGpsStatus(b)
    }

    private val getCheckPaymentStatus = Observer<CheckPaymentStatusData?> { statusData ->
        paymentStatusData = statusData
        if (!statusData.status) {
            changeButtonProblems()
        }
    }

    private val freeStateObserver = Observer<Boolean> {
        viewModel.updateFree(it)
        binding.apply {
            outerLayout.changeFreeButtonState(it, imageCheck, imageArrow)
            textFreeUnfree.changeFreeTextState(it)

            binding.textFreeUnfree.text =
                when {
                    it -> getString(R.string.free)
                    viewModel.getGpsStatus() -> getString(R.string.unfree)
                    else -> getString(R.string.problems)
                }
            storage.freeState = it
        }
    }

    private fun changeButtonProblems() {
        binding.apply {
            outerLayout.changeFreeButtonState(false, imageCheck, imageArrow)
            textFreeUnfree.changeFreeTextState(false)
        }
        binding.textFreeUnfree.text = getString(R.string.yes_problems)
    }

    private val responseUserDataObserver = Observer<WorkerResponseData?> { userData ->
        if (userData != null) {

            binding.apply {

                val payments = userData.payments ?: emptyList()

                if (payments.isNotEmpty()) {
                    storage.lastCreatedPayment = payments.last().createdAt ?: 0
                }
                storage.isActive = userData.passport?.isActive ?: false

                if (userData.passport?.isActive == true
                    && !payments.isNullOrEmpty()
                    && paymentStatusData?.status == true
                    && viewModel.getGpsStatus()
                ) {
                    if (userData.isFree) {
                        binding.textFreeUnfree.text = getString(R.string.free)
                    } else {
                        binding.textFreeUnfree.text = getString(R.string.unfree)
                    }
                } else {
                    storage.freeState = false
//                    makeWorkerUnFree()
                    changeButtonProblems()
                }
                userData.sum_mark?.let {
                    binding.rating.rating = it.rating()
                    textRating.text = it.rating().toString()
                }

                textName.text = userData.fullName

                userData.avatar?.let {
                    if (it.isNotEmpty())
                        imageAvatar.loadPictureUrl(it)
                }
                layoutBalance.apply {
                    textId.text = userData.payment_id
                }

                if (!userData.description.isNullOrEmpty())
                    layoutDesc.textDesc.text = userData.description
                else {
                    layoutDesc.textDesc.setText(R.string.temp_about)
                }

                if (userData.jobs != null) {
                    showLog("jobs.ls.size.toString()")
                    jobsData = userData.jobs
                    jobsAdapter.submitList(userData.jobs)
                }

                userData.apply {
                    val j = arrayListOf<String>()
                    jobs?.forEach {
                        j.add(it._id)
                    }
                    editData = EditProfileRequestData(
                        avatar = avatar,
                        description = description,
                        fullName = fullName,
                        images = images,
                        jobs = j,
                        location = location,
                        phone = phone
                    )
                }

                if (userData.images != null) {
                    val ls = arrayListOf<String>()
                    ls.add("Add")
                    ls.addAll(userData.images.toMutableList())
                    imagesAdapter.submitList(ls)
                }
            }
        }
    }

    private val responseUserUpdateDataObserver = Observer<AuthResponseData> { profileNewData ->
        if (!profileNewData.description.isNullOrEmpty())
            binding.layoutDesc.textDesc.text = profileNewData.description
        if (profileNewData.fullName.isNotEmpty())
            binding.textName.text = profileNewData.fullName
        profileNewData.avatar?.let { binding.imageAvatar.loadPictureUrl(it) }
    }

    private val editAvatarLiveDataObserver = Observer<ImagePath> {
        if (editData != null) {
            editData!!.avatar = it.path
            viewModel.update(editData!!)
        }
    }

    private val successOrdersLiveDataObserver = Observer<List<FastOrderResponseData>> {
        binding.layoutOrdersInfo.textCall.text = it.size.toString()
    }

    private val cancelOrdersLiveDataObserver = Observer<List<FastOrderResponseData>> {
        binding.layoutOrdersInfo.textPost.text = it.size.toString()
    }

    private val addImageLiveDataObserver = Observer<ImagePath> {
        val ls = imagesAdapter.currentList.toMutableList()
        ls.removeAll { s -> s == "Add" }
        ls.add(it.path)
        if (editData != null) {
            editData!!.images?.clear()
            editData!!.images?.addAll(ls)
            viewModel.update(editData!!)
        }
        ls.add(0, "Add")
        imagesAdapter.submitList(ls)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showMainLoader() else hideMainLoader()
    }

    private val jobsObserver = Observer<List<JobCategory>> { list ->
        if (jobsData != null) {
            list?.let {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToJobChooserFragment2(
                        EditJobsDeliverData(list),
                        UserJobsDeliver(jobsData!!)
                    )
                )
                hideBottomMenu()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        takeImageFromCamera.unregister()
        getImageFromGallery.unregister()
        super.onDestroy()
    }

    private fun showSuggestDialogForAvatar() {
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = LayoutChooseAvatarOptionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.dismissWithAnimation = true
        editData?.let {
            it.avatar.let { avatarStr ->
                if (avatarStr.isNullOrEmpty()) {
                    view.layoutAvatar.hide()
                    view.layoutAvatarChange.show()
                    view.deleteButton.hide()
                }
            }
        }
        view.takePhotoButton.setOnClickListener {
            checkPermission(android.Manifest.permission.CAMERA) {
                getImageFromCamera()
            }
            dialog.dismiss()
        }
        view.takeFromGalleryButton.setOnClickListener {
            getImageFromGallery()
            dialog.dismiss()
        }
        view.changeButton.setOnClickListener {
            view.layoutAvatar.hide()
            view.layoutAvatarChange.show()
        }
        view.deleteButton.setOnClickListener {
            editData?.avatar = ""
            editData?.let { it1 -> viewModel.update(it1) }
            dialog.dismiss()
        }
        view.showButton.setOnClickListener {
            editData?.avatar?.let { it1 -> showPicture(it1) }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showSuggestDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = LayoutChooseDialogBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.dismissWithAnimation = true
        view.takePhotoButton.setOnClickListener {
            checkPermission(android.Manifest.permission.CAMERA) {
                getImageFromCamera()
            }
            dialog.dismiss()
        }
        view.takeFromGalleryButton.setOnClickListener {
            getImageFromGallery()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getImageFromCamera() {
        val imageFile = createImageFile()
        uri = FileProvider.getUriForFile(
            requireActivity(),
            BuildConfig.APPLICATION_ID,
            imageFile
        )
        takeImageFromCamera.launch(uri)
    }

    private val takeImageFromCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            image = File(LocalStorage.instance.avatarPath)
            if (image != null) {
                showMainLoader()
                ImageHelper.compressImage(requireActivity(), image!!) {
                    viewModel.uploadImage(it, toAvatar)
                }
            }
        }
    }

    private fun getImageFromGallery() {
        getImageFromGallery.launch("image/*")
    }

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val ins = requireActivity().contentResolver.openInputStream(uri)
        image = File.createTempFile("avatar", ".jpg", requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        val fileOutputStream = FileOutputStream(image)
        ins?.copyTo(fileOutputStream)
        ins?.close()
        fileOutputStream.close()

        if (image != null) {
            showMainLoader()
            ImageHelper.compressImage(requireActivity(), image!!) {
                viewModel.uploadImage(it, toAvatar)
            }
        }
        LocalStorage.instance.avatarPath = image?.absolutePath ?: ""
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "Avatar_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            LocalStorage.instance.avatarPath = absolutePath
        }
    }

    companion object {
        const val EDIT_PROFILE = "EDIT_JOBS"
    }
}