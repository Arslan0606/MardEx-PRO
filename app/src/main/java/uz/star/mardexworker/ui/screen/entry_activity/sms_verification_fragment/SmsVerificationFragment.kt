package uz.star.mardexworker.ui.screen.entry_activity.sms_verification_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.FragmentSmsVerificationBinding
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.screen.entry_activity.EntryActivity
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.helpers.showAlertDialog
import uz.star.mardexworker.utils.views.goneView
import uz.star.mardexworker.utils.views.hideKeyboard
import uz.star.mardexworker.utils.views.showKeyboard
import uz.star.mardexworker.utils.views.showView


@AndroidEntryPoint
class SmsVerificationFragment : Fragment() {

    private var _binding: FragmentSmsVerificationBinding? = null
    private val binding: FragmentSmsVerificationBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: SmsVerificationViewModel by viewModels()
    private val args: SmsVerificationFragmentArgs by navArgs()

    private var mCountDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsVerificationBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadViews()
    }

    private fun loadViews() {
        binding.verificationScreenSubtitle.text = getString(R.string.telefon_raqamingiz_1_s_ga_sms_kod_yuborildi, args.phoneNumber)

        showKeyboard(binding.code)
        binding.code.setAnimationEnable(true)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.code.setOtpCompletionListener {
            Log.d("LLLL", "Code Verification ${args.signUpData} \n PhoneNumber: ${args.phoneNumber}")
            showEntryLoader()
            hideKeyboard(binding.code)
            viewModel.verifyPhone(args.signUpData, it, args.newPassword, args.phoneNumber, args.screenName)
        }

        binding.aboveCode.setOnClickListener {
            showKeyboard(binding.code)
        }

        binding.btnSendAgain.setOnClickListener {
            viewModel.sendAgain(args.phoneNumber)
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.timerLiveData.observe(this, timerObserver)
        viewModel.message.observe(this, messageObserver)
        viewModel.loginData.observe(this, loginDataObserver)
        viewModel.sendAgainCodeData.observe(this, sendAgainCodeObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showLoader() else hideLoader()
    }

    private val messageObserver = Observer<MessageData> { data ->
        data.onMessage {

        }.onResource {
            if (args.screenName == "restore" && it == R.string.duplicat_key) {
                showAlertDialog(R.string.user_not_found)
                return@Observer
            }
            showAlertDialog(it)
        }
    }

    private val loginDataObserver = Observer<AuthResponseData> {
        hideKeyboard(binding.root)
        if (args.screenName == "signup")
            findNavController().navigate(SmsVerificationFragmentDirections.actionSmsVerificationFragmentToJobChooserFragment())
        else {
            startActivity(Intent(requireContext(), EntryActivity::class.java))
            requireActivity().finish()
        }
    }

    private val timerObserver = Observer<Unit> {
        mCountDownTimer = object : CountDownTimer(120_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.txtCodeDesc.text = getString(R.string._1_d_soniyadan_keyin_qayta_harakat_qilib_ko_ring, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.btnSendAgain.show()
                binding.txtCodeDesc.hide()
            }
        }.start()
    }

    private val sendAgainCodeObserver = Observer<Boolean> {
        binding.txtCodeDesc.show()
        mCountDownTimer?.start()
        binding.txtCodeDesc.text = getString(R.string._1_d_soniyadan_keyin_qayta_harakat_qilib_ko_ring, 120_000 / 1000)
        binding.btnSendAgain.hide()
    }

    private fun hideLoader() {
        if (requireActivity() is EntryActivity)
            hideEntryLoader()
        else hideMainLoader()
        binding.btnSendAgain.goneView()
    }

    private fun showLoader() {
        if (requireActivity() is EntryActivity)
            showEntryLoader()
        else showEntryLoader()
        binding.btnSendAgain.showView(true)
    }

    private fun showMessage(text: Int) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mCountDownTimer?.cancel()
    }
}
