package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.list_of_images_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentListOfImagesBinding
import javax.inject.Inject


@AndroidEntryPoint
class ListOfImagesFragment : Fragment() {

    private var _binding: FragmentListOfImagesBinding? = null
    private val binding: FragmentListOfImagesBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfImagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadView()
    }

    private fun loadView() {

        binding.text1Image.setCompoundDrawablesWithIntrinsicBounds(
            if (storage.imageMainPage.isNotEmpty()) R.drawable.ic_ticked_truly else R.drawable.ic_not,
            0,
            R.drawable.ic_arrow_grey,
            0
        )

        binding.text2Image.setCompoundDrawablesWithIntrinsicBounds(
            if (storage.imagePropiskaPage.isNotEmpty()) R.drawable.ic_ticked_truly else R.drawable.ic_not,
            0,
            R.drawable.ic_arrow_grey,
            0
        )

        binding.text3Image.setCompoundDrawablesWithIntrinsicBounds(
            if (storage.imageSelfiePage.isNotEmpty()) R.drawable.ic_ticked_truly else R.drawable.ic_not,
            0,
            R.drawable.ic_arrow_grey,
            0
        )

        binding.btnPhotoMainPage.setOnClickListener {
            openInstructionFragment("1")
        }
        binding.btnPhotoSecondPage.setOnClickListener {
            openInstructionFragment("2")
        }
        binding.btnPhotoWithFace.setOnClickListener {
            openInstructionFragment("3")
        }

        binding.continueButton.setOnClickListener {
            when {
                storage.imageMainPage.isEmpty() || storage.imagePropiskaPage.isEmpty() || storage.imageSelfiePage.isEmpty() -> {
                    Toast.makeText(requireContext(), "Ko'rsatilgan talabdagi suratlarni oling", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    findNavController().navigate(ListOfImagesFragmentDirections.actionListOfImagesToShowCapturePhotoFragment())
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun openInstructionFragment(it: String) {
        val action = ListOfImagesFragmentDirections.actionListOfImagesToCameraInstructionFragment2(it)
        findNavController().navigate(action)
    }
}