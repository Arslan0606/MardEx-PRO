package uz.star.mardexworker.ui.screen.entry_activity.login_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import uz.star.mardexworker.databinding.FragmentLoginBinding
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.dialogs.DialogMaker
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.utils.EmptyBlock
import uz.star.mardexworker.utils.extensions.hideEntryLoader
import uz.star.mardexworker.utils.extensions.showEntryLoader
import uz.star.mardexworker.utils.views.hideKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dialogMaker: DialogMaker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadViews()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.responseLoginData.observe(this, responseLoginObserver)
        viewModel.openSignUpData.observe(this, openSignUpObserver)
        viewModel.message.observe(this, messageObserver)
        viewModel.openResetPasswordData.observe(this, openResetPasswordObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        Log.d("TTTLL", "$b")
        if (b) showEntryLoader() else hideEntryLoader()
    }

    private val messageObserver = Observer<MessageData> { data ->
        data.onResource {
            showSnackBarMessage(it)
        }
    }

    private val responseLoginObserver = Observer<ResultData<AuthResponseData>> {
//        showSuccessDialogMessage(R.string.successfully_message_dialog) {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
//        }
    }

    private val openSignUpObserver = Observer<Unit> {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    private val openResetPasswordObserver = Observer<Unit> {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRestorePasswordFragment())
    }

    private fun loadViews() {
        binding.apply {
            btnEnter.setOnClickListener {
                Log.d("T11", "btnEnter is working")
                hideKeyboard(binding.root)
                retryRequest()
            }

            btnGoSignUp.setOnClickListener {
                viewModel.openSignUp()
            }

            btnResetPassword.setOnClickListener {
                viewModel.openResetPassword()
            }

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            etPhone.setOnEditorActionListener { v, actionId, event ->
                actionId == EditorInfo.IME_ACTION_DONE
            }
        }
        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retryRequest() {
        binding.apply {
            viewModel.login(
                "+998" + etPhone.rawText,
                etPassword.text.toString()
            )
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