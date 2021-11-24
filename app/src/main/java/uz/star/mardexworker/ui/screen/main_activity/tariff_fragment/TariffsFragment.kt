package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentTariffsBinding
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.Payment
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.model.response.tariff.TariffData
import uz.star.mardexworker.ui.adapter.recycler_view.TariffsRVAdapter
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsActivity
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.views.goneView
import java.text.NumberFormat
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 29.03.2021
 */

@AndroidEntryPoint
class TariffsFragment : Fragment() {
    private var _binding: FragmentTariffsBinding? = null
    private val binding: FragmentTariffsBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private var balance: Long = 0

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var format: NumberFormat

    private val viewModel: TariffsViewModel by activityViewModels()

    private lateinit var adapterSimple: TariffsRVAdapter
    private lateinit var adapterPremium: TariffsRVAdapter
    private lateinit var lastPayment: Payment

    //
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTariffsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadObservers()
        loadViews()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadViews() {
        showBottomMenu()
        binding.notification.setOnClickListener {
//            findNavController().navigate(TariffsFragmentDirections.actionTariffsFragmentToNewsFragment())
            startActivity(Intent(requireActivity(), OwnNotificationsActivity::class.java))
        }

        adapterSimple = TariffsRVAdapter(storage.language, format)
        adapterPremium = TariffsRVAdapter(storage.language, format)
        binding.tariffsContent.apply {
            firstList.adapter = adapterSimple
            secondList.adapter = adapterPremium

            adapterSimple.setOnClickListener {
                hideBottomMenu()
                findNavController().navigate(TariffsFragmentDirections.actionTariffsFragmentToChooseTariffFragment(it, balance))
            }

            adapterPremium.setOnClickListener {
                hideBottomMenu()
                findNavController().navigate(TariffsFragmentDirections.actionTariffsFragmentToChooseTariffFragment(it, balance))
            }

//            viewModel.getTariff()
//            viewModel.getWorker()
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                try {
                    Executors.newSingleThreadExecutor().execute {
                        Thread.sleep(2000)
                        activity?.runOnUiThread {
                            swipeRefresh.isRefreshing = false
                            viewModel.getTariff()
                            viewModel.getWorker()
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }

        binding.menuButton.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(CHOOSE_TARIFF)
            ?.observe(this) { result ->
                val state = result.getString(CHOOSE_TARIFF)
                if (!state.isNullOrEmpty()) {
                    viewModel.getWorker()
                }
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Bundle>(CHOOSE_TARIFF)
            }

    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.responseUserData.observe(viewLifecycleOwner, responseUserDataObserver)
        viewModel.responseTariffData.observe(viewLifecycleOwner, responseTariffObserver)
        viewModel.checkPaymentStatus.observe(viewLifecycleOwner, getCheckPaymentStatus)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
        viewModel.message.observe(requireActivity(), messageObserver)

        viewModel.getUnReadOwnNotificationsCount()
        viewModel.unReadNotifications.observe(viewLifecycleOwner, unReadNotificationsObserver)
    }

    private val unReadNotificationsObserver = Observer<List<OwnNotificationResponse>> {
        if (it.isNotEmpty() && it != null) {
            binding.txtCountNotifications.text = it.size.toString()
            binding.txtCountNotifications.show()
        } else {
            binding.txtCountNotifications.goneView()
        }
    }

    private val responseTariffObserver = Observer<List<TariffData>> { tariffs ->
        if (tariffs != null) {
            binding.apply {
                val simpleList = arrayListOf<TariffData>()
                val premiumList = arrayListOf<TariffData>()

                tariffs.forEach {
                    if (it.post == 0) {
                        simpleList.add(it)
                    } else
                        premiumList.add(it)
                }
                adapterSimple.submitList(simpleList)
                adapterPremium.submitList(premiumList)
            }
        }
    }

    private val getCheckPaymentStatus = Observer<CheckPaymentStatusData?> { statusData ->
        if (statusData.status) {
            adapterSimple.currentList.forEach {
                it.type = lastPayment.service_id == it._id
            }
            adapterSimple.notifyDataSetChanged()

            adapterPremium.currentList.forEach {
                it.type = lastPayment.service_id == it._id
            }
            adapterPremium.notifyDataSetChanged()
        }
    }

    private val responseUserDataObserver = Observer<WorkerResponseData> { userData ->
        if (userData != null) {
            binding.apply {
                balance = userData.balance
                if (userData.payments != null && userData.payments.isNotEmpty())
                    userData.payments.last().apply {
                        lastPayment = this
                    }
            }
        }
    }

    private val messageObserver = Observer<MessageData> { message ->
        message.onResource {
//            showSnackBarMessage(it)
        }
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showMainLoader() else hideMainLoader()
    }

    private fun showMessage(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val CHOOSE_TARIFF = "CHOOSE_TARIFF"
    }
}