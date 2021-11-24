package uz.star.mardexworker.ui.screen.main_activity.profile_fragment.images_fragment

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.DialogPhotoviewBinding
import uz.star.mardexworker.databinding.FragmentImagesBinding
import uz.star.mardexworker.databinding.LayoutChooseDialogBinding
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.ImagePath
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.adapter.recycler_view.ImagesPagerAdapter
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileFragment
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.helpers.ImageHelper
import uz.star.mardexworker.utils.views.showView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ImagesFragment : Fragment() {
    private var _binding: FragmentImagesBinding? = null
    private val binding: FragmentImagesBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var api: String

    private val viewModel: ImagesViewModel by viewModels()

    private val args: ImagesFragmentArgs by navArgs()

    private lateinit var imagesAdapter: ImagesPagerAdapter

    private var images = arrayListOf<String>()
    private var editProfileRequestData: EditProfileRequestData? = null
    private var currentIndex = 0
    private lateinit var uri: Uri
    private var image: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadViews()
    }

    private fun loadViews() {
        imagesAdapter = ImagesPagerAdapter()

        imagesAdapter.setOnItemClickListener {
            showPicture(it)
        }

        if (args.editData != null) {
            images.addAll(args.imagesList)
            editProfileRequestData = args.editData!!

            imagesAdapter.submitList(images)

            if (args.imagesList.isEmpty() || args.imagesList.size == 1) {
                binding.dotsIndicator.showView(false)
            } else
                binding.dotsIndicator.show()
        }

        binding.apply {
            pager.adapter = imagesAdapter
            dotsIndicator.setViewPager2(binding.pager)

            pager.apply {
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        currentIndex = position
                        if (currentIndex == imagesAdapter.currentList.size - 1) {
                            layoutRight.disable()
                            layoutLeft.enable()
                            return
                        }
                        if (currentIndex == 0) {
                            layoutRight.enable()
                            layoutLeft.disable()
                            return
                        }
                        layoutRight.enable()
                        layoutLeft.enable()
                    }
                })
            }

            layoutLeft.setOnClickListener {
                if (currentIndex != 0) {
                    currentIndex = pager.currentItem - 1
                    pager.setCurrentItem(currentIndex, true)
                }
            }

            layoutRight.setOnClickListener {
                if (currentIndex != imagesAdapter.currentList.size) {
                    currentIndex = pager.currentItem + 1
                    pager.setCurrentItem(currentIndex, true)
                }
            }

            layoutDelete.setOnClickListener {
                if (images.isEmpty()) {
                    layoutDelete.disable()
                    return@setOnClickListener
                } else {
                    layoutDelete.enable()
                }
                images.removeAt(currentIndex)
                imagesAdapter.submitList(images)
                imagesAdapter.notifyDataSetChanged()
                if (images.size == 1) {
                    dotsIndicator.showView(false)
                }
            }

            layoutAdd.setOnClickListener {
                showSuggestDialog()
            }

            btnBack.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(ProfileFragment.EDIT_PROFILE, Bundle().apply {
                    putBoolean(ProfileFragment.EDIT_PROFILE, true)
                })
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {
                editProfileRequestData?.let { profileData ->
                    val ls = arrayListOf<String>()
                    profileData.images?.let {
                        val removeList = it.toMutableList()
                        removeList.removeAll(images)
                        ls.addAll(removeList)
                    }
                    profileData.images?.clear()
                    profileData.images?.addAll(images)
                    profileData.let { it1 -> viewModel.update(it1, ls) }
                }
            }
        }
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
        viewModel.responseUserUpdateData.observe(this, responseUserUpdateDataObserver)
        viewModel.responseDeleteImages.observe(this, responseDeleteImageObserver)
        viewModel.addImageLiveData.observe(this, addImageLiveDataObserver)
        viewModel.loader.observe(this, loaderObserver)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showMainLoader() else hideMainLoader()
    }

    private val responseUserUpdateDataObserver = Observer<AuthResponseData> {
        findNavController().popBackStack()
    }

    private val responseDeleteImageObserver = Observer<Unit> {

    }

    private val addImageLiveDataObserver = Observer<ImagePath> {
        images.add(it.path)
        editProfileRequestData?.images?.add(it.path)
        imagesAdapter.submitList(images)
        imagesAdapter.notifyDataSetChanged()
        if (images.size > 1) {
            binding.apply {
                dotsIndicator.showView(true)
                layoutDelete.enable()
                pager.currentItem = images.size - 1
            }
        }
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
                    viewModel.uploadImage(it)
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
                viewModel.uploadImage(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}