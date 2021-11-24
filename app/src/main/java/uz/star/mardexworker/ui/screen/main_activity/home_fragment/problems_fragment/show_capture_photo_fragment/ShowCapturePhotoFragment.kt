package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.show_capture_photo_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentShowCapturePhotoBinding
import uz.star.mardexworker.ui.adapter.recycler_view.CameraPicturesAdapter
import uz.star.mardexworker.utils.extensions.clearImagesFromCache
import uz.star.mardexworker.utils.extensions.hideMainLoader
import uz.star.mardexworker.utils.extensions.showMainLoader
import uz.star.mardexworker.utils.extensions.showMessage
import uz.star.mardexworker.utils.helpers.ImageHelper
import uz.star.mardexworker.utils.helpers.showMessage
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class ShowCapturePhotoFragment : Fragment() {

    private var _binding: FragmentShowCapturePhotoBinding? = null
    private val binding: FragmentShowCapturePhotoBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: ShowCameraCaptureViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    private lateinit var imagesAdapter: CameraPicturesAdapter
    private var images = hashMapOf<String, String>()

    private var count = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowCapturePhotoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadView()
        loadObservers()
    }

    private fun loadView() {
        images["1"] = storage.imageMainPage
        images["2"] = storage.imagePropiskaPage
        images["3"] = storage.imageSelfiePage

        imagesAdapter = CameraPicturesAdapter()
        binding.pager.adapter = imagesAdapter

        val ls = arrayListOf<String>()
        images.forEach {
            ls.add(it.value)
        }
        imagesAdapter.submitList(ls)

        binding.btnCaptureAgain.setOnClickListener {
            openCameraScreen(binding.pager.currentItem + 1)
        }

        binding.btnOkNext.setOnClickListener {
            showMainLoader()
            when (count) {
                1 -> {
                    count++
                    binding.incCheckEvaluation.checkEvaluation1.setBackgroundResource(R.drawable.round_check_back_selected)
                    addImage(storage.imageMainPage.toUri().toFile())
                    binding.pager.currentItem++
                }
                2 -> {
                    count++
                    binding.incCheckEvaluation.checkEvaluation2.setBackgroundResource(R.drawable.round_check_back_selected)
                    addImage(storage.imagePropiskaPage.toUri().toFile())
                    binding.pager.currentItem++
                }
                3 -> {
                    count++
                    binding.incCheckEvaluation.checkEvaluation3.setBackgroundResource(R.drawable.round_check_back_selected)
                    addImage(storage.imageSelfiePage.toUri().toFile())
                    binding.pager.currentItem++
                }
                else -> {
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addImage(file: File) {
        ImageHelper.compressImage(requireContext(), file) { compressed ->
            hideMainLoader()
            viewModel.uploadPassportPicture(compressed)
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.passportWithFaceLiveData.observe(this) {
            hideMainLoader()
            clearImagesFromCache()
            storage.imageMainPage = ""
            storage.imagePropiskaPage = ""
            storage.imageSelfiePage = ""
            findNavController().popBackStack()
        }

        viewModel.uploadCameraCaptureLiveData.observe(this) {

        }

        viewModel.loader.observe(this) {
            if (it) {
                showMainLoader()
            } else {
                hideMainLoader()
            }
        }

        viewModel.message.observe(this) { data ->
            data.onResource {
                showMessage(it)
            }.onMessage {
                showMessage(it)
            }
        }
    }

    private fun openCameraScreen(int: Int) {
        findNavController().navigate(ShowCapturePhotoFragmentDirections.actionShowCapturePhotoFragment2ToCameraFragment2(int.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}