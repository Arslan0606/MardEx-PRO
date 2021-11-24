package uz.star.mardexworker.ui.screen.main_activity.promocode

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
import uz.star.mardexworker.databinding.FragmentPromocodeBinding
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.views.hideKeyboard

@AndroidEntryPoint
class PromocodeFragment : Fragment() {

    private var _binding: FragmentPromocodeBinding? = null
    private val binding: FragmentPromocodeBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: PromocodeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromocodeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        binding.btnNext.setOnClickListener {
            val promocode = binding.etPromo.rawText.toString()
            if (promocode.length != 5) return@setOnClickListener
            viewModel.sendPromocode(promocode)
            showMainLoader()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.message.observe(this, messageObserver)
        viewModel.successData.observe(this, successObserver)
    }

    private val successObserver = Observer<Boolean> {
        hideMainLoader()
        binding.promoError.show()
        if (it) {
            binding.promoError.setText(R.string.success_promo)
        } else {
            binding.promoError.setText(R.string.error_promocode)
        }
    }

    private val messageObserver = Observer<MessageData> {
        hideMainLoader()
        binding.promoError.show()
        it.onMessage {
            binding.promoError.text = it
        }.onResource {
            binding.promoError.setText(it)
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard(binding.root)
        _binding = null
    }
}