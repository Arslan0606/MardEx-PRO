package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.camera_instruction_fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.FragmentCameraInstructionBinding
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.camera_fragment.CameraFragment
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import uz.star.mardexworker.utils.extensions.showToast


@AndroidEntryPoint
class CameraInstructionFragment : Fragment() {

    private var _binding: FragmentCameraInstructionBinding? = null
    private val binding: FragmentCameraInstructionBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: CameraInstructionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViews()
    }

    fun loadViews() {
        hideBottomMenu()
        binding.apply {
            when (args.buttonId) {
                "1" -> {
                    problemsScreenTitle.setText(R.string.passport_photo_definition1)
                    binding.examplePhoto.setImageResource(R.drawable.passport2)
//                    binding.examplePhoto.setImageResource(R.drawable.picture1)
                }
                "2" -> {
                    problemsScreenTitle.setText(R.string.passport_photo_definition2)
                    binding.examplePhoto.setImageResource(R.drawable.passport1)
//                    binding.examplePhoto.setImageResource(R.drawable.picture2)
                }
                "3" -> {
                    problemsScreenTitle.setText(R.string.passport_photo_definition3)
                    examplePhoto.setImageResource(R.drawable.passport3)
//                    examplePhoto.setImageResource(R.drawable.picture3)
                }
            }

            takePhotoButton.setOnClickListener {
                openCameraScreen(args.buttonId)
            }

            cameraInstructionBackButton.setOnClickListener {
                backToListOfImagesScreen()
            }

            backToTypesScreen.setOnClickListener { findNavController().popBackStack() }
        }
    }


    private var str = ""
    private fun openCameraScreen(it: String?) {
        if (it != null) {
            str = it
        }
        if (allPermissionsGranted()) {
            findNavController().navigate(CameraInstructionFragmentDirections.actionCameraInstructionFragment2ToCameraFragment2(it))
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), CameraFragment.REQUIRED_PERMISSIONS, CameraFragment.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == CameraFragment.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                findNavController().navigate(CameraInstructionFragmentDirections.actionCameraInstructionFragment2ToCameraFragment2(str))
            } else {
                showToast("Permissions not granted by the user.")
            }
            requireActivity().finish()
        }
    }

    private fun backToListOfImagesScreen() {
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = CameraFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

}