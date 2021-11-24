package uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import uz.star.mardexworker.databinding.FragmentProblemsBinding
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatus
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatusLiveData
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.show
import uz.star.mardexworker.utils.extensions.showLog
import javax.inject.Inject

@AndroidEntryPoint
class ProblemsFragment : Fragment() {

    private var _binding: FragmentProblemsBinding? = null
    private val binding: FragmentProblemsBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var gpsStatusLiveData: GpsStatusLiveData

    private val viewModel: ProblemsViewModel by viewModels()

    private var paymentStatusData: CheckPaymentStatusData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProblemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadObservers()
        loadViews()
    }

    private fun loadViews() {
        if (!viewModel.getGpsStatus()) {
            binding.gpsControl.show()
            binding.gpsControl.setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                requireContext().startActivity(intent)
            }
        } else {
            binding.gpsControl.hide()
        }
        binding.photoControl.setOnClickListener {
            findNavController().navigate(ProblemsFragmentDirections.actionProblemsFragmentToListOfImages())
        }

        binding.balanceControl.setOnClickListener {
            findNavController().navigate(ProblemsFragmentDirections.actionProblemsFragmentToTariffsFragment())
        }

        binding.cameraInstructionBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getCheckPaymentStatus()
        viewModel.getWorker()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        gpsStatusLiveData.observe(
            viewLifecycleOwner,
            gpsObserver
        )
        viewModel.checkPaymentStatus.observe(viewLifecycleOwner, getCheckPaymentStatus)
        viewModel.responseUserData.observe(viewLifecycleOwner, responseUserDataObserver)
    }

    private val gpsObserver = Observer<GpsStatus> { status ->
        status?.let {
            updateGpsCheckUI(status)
        }
    }

    private fun updateGpsCheckUI(status: GpsStatus) {
        val b = when (status) {
            is GpsStatus.Enabled -> {
                binding.gpsControl.hide()
                true
            }
            is GpsStatus.Disabled -> {
                binding.gpsControl.show()
                false
            }
        }
        viewModel.setGpsStatus(b)
    }

    private val responseUserDataObserver = Observer<WorkerResponseData> { userData ->
        if (userData != null) {
            binding.apply {
                val a = userData.passport!!.isActive
                showLog(a.toString())
                if (a) {
                    photoControl.hide()
                } else {
                    if (userData.passport?.backScan?.isNotEmpty() == true) {
                        photoControl.setText(R.string.complete_photo_passport_in_proccess)
                        photoControl.setOnClickListener { }
                    }
                    photoControl.show()
                }
            }
        }
    }

    private val getCheckPaymentStatus = Observer<CheckPaymentStatusData?> { statusData ->
        paymentStatusData = statusData
        binding.balanceControl.visibility =
            if (paymentStatusData!!.status) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}