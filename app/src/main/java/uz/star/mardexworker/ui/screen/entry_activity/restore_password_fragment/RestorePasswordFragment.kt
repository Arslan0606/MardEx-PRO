package uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentRestorePasswordBinding
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.ui.dialogs.DialogMaker
import uz.star.mardexworker.utils.EmptyBlock
import uz.star.mardexworker.utils.extensions.hideEntryLoader
import uz.star.mardexworker.utils.extensions.showEntryLoader
import javax.inject.Inject

@AndroidEntryPoint
class RestorePasswordFragment : Fragment() {

    private var _binding: FragmentRestorePasswordBinding? = null
    private val binding: FragmentRestorePasswordBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: RestorePasswordViewModel by viewModels()

    @Inject
    lateinit var dialogMaker: DialogMaker

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestorePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadViews()
    }

    private fun loadObservers() {
        viewModel.responseRestorePassword.observe(viewLifecycleOwner, responseRestorePassword)
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)

    }

    private val responseRestorePassword = Observer<LoginData> { loginData ->
        findNavController().navigate(
            RestorePasswordFragmentDirections.actionRestorePasswordFragmentToSmsVerificationFragment(
                SignUpData("", "", "", "", "", null, null),
                "+998" + binding.etPhone.rawText,
                "restore",
                loginData.password
            )
        )
    }

    private fun loadViews() {
        binding.etPhone.setOnEditorActionListener { v, actionId, event ->
            actionId == EditorInfo.IME_ACTION_DONE
        }

        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            binding.btnNext.setOnClickListener {
                viewModel.openCodeVerifyScreen(
                    "+998" + binding.etPhone.rawText,
                    binding.etPassword.text.toString(),
                    binding.etConfirmPassword.text.toString()
                )
            }
        }
        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showEntryLoader() else hideEntryLoader()
    }

    private fun retryRequest() {
        binding.apply {
            viewModel.openCodeVerifyScreen(
                "+998" + etPhone.rawText,
                etPassword.text.toString(),
                etConfirmPassword.toString()
            )
        }
    }

    private val messageObserver = Observer<MessageData> { data ->
        data.onResource {
            showSnackBarMessage(it)
        }
    }

    private fun showSnackBarMessage(@StringRes text: Int) {
        dialogMaker.showErrorDialog(requireActivity(), text)
    }

    private fun showSuccessDialogMessage(@StringRes message: Int, emptyBlock: EmptyBlock) {
        dialogMaker.showSuccessDialog(requireContext(), message) {
            emptyBlock()
        }
    }

    private fun showErrorDialogMessage(@StringRes message: Int, emptyBlock: EmptyBlock) {
        dialogMaker.showErrorDialog(requireContext(), message) {
            emptyBlock()
            // TODO: 25.01.2021
            // Button clicked
        }
    }
}