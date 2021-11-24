package uz.star.mardexworker.ui.screen.entry_activity.signUp_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.star.mardexworker.R
import uz.star.mardexworker.model.request.CheckData
import uz.star.mardexworker.model.request.LocationRequest
import uz.star.mardexworker.model.request.signup.PassportData
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.regions.CityResponse
import uz.star.mardexworker.model.response.regions.RegionResponse
import uz.star.mardexworker.utils.extensions.NumberExtensions.Companion.createUniqueNumber
import uz.star.mardexworker.utils.livedata.addSourceDisposable
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val model: SignUpRepository
) : ViewModel() {
    private val _check = MediatorLiveData<OpportunityResponse>()
    val check: LiveData<OpportunityResponse> get() = _check

    private val _responseSignUp = MediatorLiveData<SignUpData>()
    val responseSignUp: LiveData<SignUpData> get() = _responseSignUp

    private val _responseRegions = MediatorLiveData<List<RegionResponse>>()
    val responseRegions: LiveData<List<RegionResponse>> get() = _responseRegions

    private val _responseCities = MediatorLiveData<List<CityResponse>>()
    val responseCities: LiveData<List<CityResponse>> get() = _responseCities

    private val _backToLoginLiveData = MutableLiveData<Unit>()
    val backToLoginLiveData: LiveData<Unit> get() = _backToLoginLiveData

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> get() = _loader

    fun backToLogin() {
        _backToLoginLiveData.value = Unit
    }

    init {
        _loader.value = true
        _responseRegions.addSourceDisposable(model.getRegions()) { it ->
            _loader.value = false
            it.onData {
                if (it.success) {
                    _responseRegions.value = it.data!!
                }
                getCities()
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }

    fun getCities() {
        _loader.value = true
        _responseCities.addSourceDisposable(model.getCities()) { it ->
            _loader.value = false
            it.onData {
                if (it.success) {
                    _responseCities.value = it.data!!
                }
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }

    private fun checkPhone(
        etPhoneNumber: String,
        signUpData: SignUpData
    ) {
        _loader.value = true
        _check.addSourceDisposable(model.checkExsist(CheckData("user", etPhoneNumber))) {
            _loader.value = false
            it.onData {
                if (it.success == true) {
                    _message.value = MessageData.resource(R.string.duplicat_key)
                } else {
                    _loader.value = true
                    _message.addSourceDisposable(model.sendVerificationCode(etPhoneNumber)) {
                        _loader.value = false
                        it.onData {
                            _responseSignUp.value = signUpData
                        }.onMessage { message ->
                            _message.value = MessageData.message(message)
                        }.onMessageData { message ->
                            _message.value = message
                        }
                    }
                }
            }.onMessage { message ->
                _message.value = MessageData.message(message)
            }.onMessageData { message ->
                _message.value = message
            }
        }
    }

    fun openCodeVerifyFragment(
        etFullName: String,
        etPhoneNumber: String,
        etPassword: String,
        etConfirmPassword: String,
        promocode: String,
        gender: Boolean,
        region: String,
        city: String
    ) {
        when {
            etFullName.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.enter_your_surname)
                return
            }
            etPhoneNumber.length < 13 -> {
                _message.value =
                    MessageData.resource(R.string.input_phone_number_right)
                return
            }

            etPassword.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.entering_password)
                return
            }

            etConfirmPassword.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.repeat_your_passord)
                return
            }
            etPassword != etConfirmPassword -> {
                _message.value =
                    MessageData.resource(R.string.incompatible_password)
                return
            }
            region.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.choose_region_)
                return
            }
            city.isEmpty() -> {
                _message.value =
                    MessageData.resource(R.string.choose_city_)
                return
            }
            else -> {
                val signUpData =
                    if (promocode.isNotEmpty())
                        SignUpData(
                            fullName = etFullName,
                            phone = etPhoneNumber,
                            password = etPassword,
                            location = LocationRequest(
                                type = "Point",
                                coordinates = listOf(0.0, 0.0)
                            ),
                            payment_id = createUniqueNumber(),
                            passport = PassportData(region = region, city = city, gender = gender),
                            promocode = promocode.toInt()
                        ) else SignUpData(
                        fullName = etFullName,
                        phone = etPhoneNumber,
                        password = etPassword,
                        location = LocationRequest(
                            type = "Point",
                            coordinates = listOf(0.0, 0.0)
                        ),
                        payment_id = createUniqueNumber(),
                        passport = PassportData(region = region, city = city, gender = gender),
                        promocode = 0
                    )
                checkPhone(etPhoneNumber, signUpData)
            }
        }
    }


}