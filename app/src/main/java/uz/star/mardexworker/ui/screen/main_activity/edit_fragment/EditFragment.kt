package uz.star.mardexworker.ui.screen.main_activity.edit_fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.databinding.FragmentEditBinding
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import uz.star.mardexworker.utils.extensions.hideMainLoader

@AndroidEntryPoint
class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding: FragmentEditBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideMainLoader()
        hideBottomMenu()

        binding.menuButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.personalDataButton.setOnClickListener {
            findNavController().navigate(EditFragmentDirections.actionEditFragment2ToEditPersonalDataFragment())
        }

        binding.personNumberButton.setOnClickListener {
            findNavController().navigate(EditFragmentDirections.actionEditFragment2ToEditPhoneFragment())
        }

        binding.personPasswordButton.setOnClickListener {
            findNavController().navigate(EditFragmentDirections.actionEditFragment2ToEditPasswordFragment())
        }

        binding.txtTitleIfYourAccount.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mardex.uz/uz/public-offer"))
            startActivity(browserIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}