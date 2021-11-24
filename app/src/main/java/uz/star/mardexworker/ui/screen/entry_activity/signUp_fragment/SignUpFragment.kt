package uz.star.mardexworker.ui.screen.entry_activity.signUp_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.FragmentSignUpBinding
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.regions.CityResponse
import uz.star.mardexworker.model.response.regions.RegionResponse
import uz.star.mardexworker.model.response.regions.SpinnerRegion
import uz.star.mardexworker.utils.NothingSelectedSpinnerAdapter
import uz.star.mardexworker.utils.extensions.hideEntryLoader
import uz.star.mardexworker.utils.extensions.showEntryLoader
import uz.star.mardexworker.utils.extensions.showLog
import uz.star.mardexworker.utils.helpers.showAlertDialog
import uz.star.mardexworker.utils.toLocalString
import uz.star.mardexworker.utils.views.hideKeyboard

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: SignUpViewModel by viewModels()
    private var isMan = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.responseSignUp.observe(this, responseSignUpObserver)
        viewModel.responseCities.observe(this, responseCitiesObserver)
        viewModel.responseRegions.observe(this, responseRegionsObserver)
        viewModel.check.observe(this, check)
        viewModel.backToLoginLiveData.observe(this, backToLoginObserver)
        viewModel.message.observe(this, messageObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
    }

    private var regions: List<RegionResponse>? = null
    private val responseRegionsObserver = Observer<List<RegionResponse>> { data ->
        regions = data
        initRegions(data)
    }

    private fun initRegions(data: List<RegionResponse>) {
        binding.apply {
            val subjectMs = ArrayList<SpinnerRegion>()
            for (subject in data) {
                subjectMs.add(SpinnerRegion(title = subject.title?.toLocalString() ?: "", id = subject.id ?: ""))
            }

            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subjectMs)

            binding.spinnerRegion.adapter = NothingSelectedSpinnerAdapter(
                arrayAdapter,
                R.layout.contact_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                binding.root.context
            )
            binding.spinnerRegion.prompt = getString(R.string.select_an_option)
        }
    }

    private var cities: List<CityResponse>? = null
    private val responseCitiesObserver = Observer<List<CityResponse>> { data ->
        cities = data
        initCities(data)
    }

    private fun initCities(data: List<CityResponse>) {
        binding.spinnerRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val d = binding.spinnerRegion.adapter.getItem(position) as SpinnerRegion
                showLog(d.toString(), "T12T")
                d.let {
                    val subjectMs = ArrayList<SpinnerRegion>()
                    val filtered = data.filter { it.cityId?.id == d.getId() }

                    for (subject in filtered) {
                        subjectMs.add(SpinnerRegion(title = subject.title?.toLocalString() ?: "", id = subject.id ?: ""))
                    }

                    val arrayAdapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subjectMs)
                    binding.spinnerCity.adapter = arrayAdapter
                    binding.spinnerCity.prompt = getString(R.string.select_an_option)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private val responseSignUpObserver = Observer<SignUpData> { signUpData ->
        findNavController().navigate(
            SignUpFragmentDirections.actionSignUpFragmentToSmsVerificationFragment(
                signUpData,
                "+998" + binding.etPhone.rawText,
                "signup",
                binding.etPassword.text.toString()
            )
        )
    }

    private val check = Observer<OpportunityResponse> { _ ->
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showEntryLoader() else hideEntryLoader()
    }

    private val backToLoginObserver = Observer<Unit> {
        findNavController().popBackStack()
    }

    private val messageObserver = Observer<MessageData> { message ->
        message.onMessage {
            showAlertDialog(it)
        }.onResource {
            showAlertDialog(it)
        }
    }

    private fun loadViews() {
        binding.textMan.setOnClickListener {
            binding.textMan.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.textMan.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_man_white, 0, 0, 0)
            binding.textMan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.new_green))

            binding.textWoman.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_number_color))
            binding.textWoman.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_woman_black, 0, 0, 0)
            binding.textWoman.background = null
            isMan = true
        }

        binding.textWoman.setOnClickListener {
            binding.textWoman.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.textWoman.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_woman_white, 0, 0, 0)
            binding.textWoman.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.new_green))

            binding.textMan.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_number_color))
            binding.textMan.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_man_black, 0, 0, 0)
            binding.textMan.background = null
            isMan = false
        }

        binding.btnCity.setOnClickListener {
            binding.spinnerCity.performClick()
        }
        binding.btnRegion.setOnClickListener {
            binding.spinnerRegion.performClick()
        }
        binding.etPhone.setOnEditorActionListener { _, actionId, _ ->
            actionId == EditorInfo.IME_ACTION_DONE
        }

        binding.txtPolicy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mardex.uz/uz/public-offer"))
            startActivity(browserIntent)
        }
        binding.btnNext.setOnClickListener {
            hideKeyboard(binding.root)
            if (binding.checkboxConfirmPolicy.isChecked)
                retryRequest()
            else
                showAlertDialog(R.string.check_policy)
        }

        binding.btnGoLogin.setOnClickListener {
            viewModel.backToLogin()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.etPhone.setOnEditorActionListener { v, actionId, event ->
            actionId == EditorInfo.IME_ACTION_DONE
        }

        regions?.let {
            initRegions(it)
        }

        cities?.let {
            initCities(it)
        }
        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retryRequest() {
//        binding.btnNext.isVisibleState(false)
        val fullName = binding.etFullName.text.toString()
        val phoneNumber = "+998" + binding.etPhone.rawText
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val promocode = binding.etPromo.rawText.toString()

        var reg = ""
        try {
            reg = (binding.spinnerRegion.selectedItem as SpinnerRegion).getId()
        } catch (e: Exception) {
        }

        var city = ""
        try {
            city = (binding.spinnerCity.selectedItem as SpinnerRegion).getId()
        } catch (e: Exception) {
        }

        viewModel.openCodeVerifyFragment(
            etFullName = fullName,
            etPhoneNumber = phoneNumber,
            etPassword = password,
            etConfirmPassword = confirmPassword,
            promocode = promocode,
            region = reg,
            city = city,
            gender = isMan
        )
    }
}