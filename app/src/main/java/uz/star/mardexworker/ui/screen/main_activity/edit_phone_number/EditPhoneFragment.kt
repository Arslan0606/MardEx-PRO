package uz.star.mardexworker.ui.screen.main_activity.edit_phone_number

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.FragmentEditBinding
import uz.star.mardexworker.databinding.FragmentEditPhoneBinding


class EditPhoneFragment : Fragment() {

    private var _binding: FragmentEditPhoneBinding? = null
    private val binding: FragmentEditPhoneBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditPhoneBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}