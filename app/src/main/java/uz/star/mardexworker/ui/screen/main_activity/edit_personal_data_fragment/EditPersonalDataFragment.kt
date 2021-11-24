package uz.star.mardexworker.ui.screen.main_activity.edit_personal_data_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentEditPersonalDataBinding
import uz.star.mardexworker.model.response.EditProfileRequestData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileFragment
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import uz.star.mardexworker.utils.helpers.showAlertDialog
import javax.inject.Inject

@AndroidEntryPoint
class EditPersonalDataFragment : Fragment() {

    private var _binding: FragmentEditPersonalDataBinding? = null
    private val binding: FragmentEditPersonalDataBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: EditPersonalDataViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    private var editData: EditProfileRequestData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditPersonalDataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadView()
    }

    private fun loadObservers() {
        viewModel.initEdit.observe(viewLifecycleOwner, initEdit)
        viewModel.responseUserUpdateData.observe(viewLifecycleOwner, update)
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
    }

    private fun loadView() {
        hideBottomMenu()
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSavePersonalData.setOnClickListener {
            editData?.let { it1 ->
                viewModel.updateUserPersonalData(
                    it1, binding.etFullName.text.toString(), binding.etDescription.text.toString()
                )
            }
        }
    }

    private val update = Observer<AuthResponseData?> { worker ->
        (requireActivity() as MainActivity).initDrawerContent()
        findNavController().previousBackStackEntry?.savedStateHandle?.set(ProfileFragment.EDIT_PROFILE, Bundle().apply {
            putBoolean(ProfileFragment.EDIT_PROFILE, true)
        })
        findNavController().navigateUp()
    }

    private val initEdit = Observer<WorkerResponseData?> { worker ->
        storage.isActive = worker.passport?.isActive ?: false
        storage.freeState = worker.isFree

        binding.etFullName.setText(worker.fullName)
        binding.etDescription.setText(worker.description)

        worker.apply {
            storage.paymentId = payment_id
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

    }

    private val toastMessageObserver = Observer<String> { errorToastMessage ->
        showAlertDialog(errorToastMessage)
    }

    private val messageObserver = Observer<MessageData> { data ->
        data.onMessage {

        }.onResource {
            if (it != R.string.failure)
                showAlertDialog(it)
            else
                showAlertDialog(getString(R.string.password_error2))
        }
    }

    private val updateUserPersonalData = Observer<AuthResponseData> {
        findNavController().popBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}