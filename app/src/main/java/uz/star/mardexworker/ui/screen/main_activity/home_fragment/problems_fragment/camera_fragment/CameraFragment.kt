package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.camera_fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentCameraBinding
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import uz.star.mardexworker.utils.extensions.showLog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding: FragmentCameraBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: CameraFragmentArgs by navArgs()

    @Inject
    lateinit var storage: LocalStorage

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null

    // Selector showing which camera is selected (front or back)
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    private var manager: CameraManager? = null

    //    private var imageAnalysis: ImageAnalysis? = null
    private var camera: Camera? = null

    private var outputDirectory: File? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    fun toggleCamera() {
        lensFacing = if (lensFacing != CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        startCamera()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showLog("Camera Fragment onViewCreated")
        hideBottomMenu()

        when (args.photoCount) {
            "1" -> {
                Log.d("Camera", "1")
                binding.txtTitleCamera.text = getString(R.string.photo_of_the_main_page_of_the_passport)
                binding.frame2.layoutFacePassportFrame.visibility = View.INVISIBLE
            }

            "2" -> {
                Log.d("Camera", "2")
                binding.txtTitleCamera.text = getString(R.string.photo_of_the_passport_registration_page)
                binding.frame2.layoutFacePassportFrame.visibility = View.INVISIBLE
            }

            "3" -> {
                Log.d("Camera", "3")
                binding.txtTitleCamera.text = getString(R.string.selfie_with_passport_on_hand)
                binding.frame1.frameFirstPhoto.visibility = View.INVISIBLE
                binding.frame2.layoutFacePassportFrame.visibility = View.VISIBLE
            }
        }

        startCamera()

        binding.btnCamera.setOnClickListener {
            takePhoto()
        }

            outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSwitchCamera.setOnClickListener {
            toggleCamera()
        }

        binding.btnFlash.setOnClickListener {

        }

        manager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {

            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                Toast.makeText(requireContext(), "Something occured error", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                when (args.photoCount) {
                    "1" -> {
                        storage.imageMainPage = savedUri.toString()
                    }
                    "2" -> {
                        storage.imagePropiskaPage = savedUri.toString()
                    }
                    "3" -> {
                        storage.imageSelfiePage = savedUri.toString()
                    }
                }
                findNavController().popBackStack()
                val msg = "Photo capture succeeded: $savedUri"
                Log.d(TAG, msg)
            }
        })
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder().build()

//            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, lensFacing, preview, imageCapture
                )
                preview?.setSurfaceProvider(binding.vCamera.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun getOutputDirectory(): File? {
        /*val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir*/
        return requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}