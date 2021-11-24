package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.history_payment_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentHistoryPaymentBinding
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.tariff.statsrvmodel.StatsOuterModel
import uz.star.mardexworker.ui.adapter.recycler_view.StatsRVAdapter
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.helpers.showAlertDialog
import java.text.NumberFormat
import javax.inject.Inject

@AndroidEntryPoint
class HistoryPaymentFragment : Fragment() {

    private var _binding: FragmentHistoryPaymentBinding? = null
    private val binding: FragmentHistoryPaymentBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: HistoryPaymentViewModel by viewModels()
    private lateinit var adapter: StatsRVAdapter

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var format: NumberFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryPaymentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideBottomMenu()
        loadObservers()
        loadViews()
    }

    private fun loadViews() {
        adapter = StatsRVAdapter(storage.language, format)
        binding.apply {
            backButton.setOnClickListener { findNavController().navigateUp() }
            listPayments.adapter = adapter
            listPayments.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter.setOnClickListener {

            }
        }
        viewModel.getPayments()
    }

    private fun loadObservers() {
        viewModel.paymentsData.observe(viewLifecycleOwner, paymentsDataObserver)
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
    }

    private val paymentsDataObserver = Observer<List<StatsOuterModel>> { data ->
        if (data != null) {
            binding.apply {
                adapter.submitList(data)
            }
        }
    }

    private val messageObserver = Observer<MessageData> { message ->
        message.onResource {
            showAlertDialog(it)
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
}