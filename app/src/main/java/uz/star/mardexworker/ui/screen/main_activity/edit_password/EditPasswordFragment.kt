package uz.star.mardexworker.ui.screen.main_activity.edit_password

import android.annotation.SuppressLint
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
import uz.star.mardexworker.databinding.FragmentEditPasswordBinding
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.utils.extensions.changeStatusColorWhite
import uz.star.mardexworker.utils.helpers.showAlertDialog
import uz.star.mardexworker.utils.views.hideKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class EditPasswordFragment : Fragment() {

    private var _binding: FragmentEditPasswordBinding? = null
    private val binding: FragmentEditPasswordBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: EditPasswordViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changeStatusColorWhite()
        _binding = FragmentEditPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.updateUserPasswordData.observe(this, updatePasswordObserver)
        viewModel.message.observe(this, messageObserver)
        viewModel.toastMessageData.observe(this, toastMessageObserver)
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

    private val updatePasswordObserver = Observer<AuthResponseData> {
        findNavController().popBackStack()
        hideKeyboard(binding.llEtConfirmPassword)
        showAlertDialog(getString(R.string.password_changed))
    }

    private fun loadViews() {
        binding.apply {
            btnBackEditPassword.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {
                viewModel.updateUserPassword(
                    oldPassword = etOldPassword.text.toString().trim(),
                    newPassword = etNewPassword.text.toString().trim(),
                    confirmPassword = etConfirmPassword.text.toString().trim()
                )
            }
        }
    }


}