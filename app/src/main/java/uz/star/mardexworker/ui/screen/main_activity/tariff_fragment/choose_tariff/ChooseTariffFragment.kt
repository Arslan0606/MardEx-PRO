package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.choose_tariff

import android.os.Bundle
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
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentChooseTariffBinding
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.TariffsFragment
import uz.star.mardexworker.utils.*
import uz.star.mardexworker.utils.extensions.*
import java.text.NumberFormat
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 29.03.2021
 */

@AndroidEntryPoint
class ChooseTariffFragment : Fragment() {
    private var _binding: FragmentChooseTariffBinding? = null
    private val binding: FragmentChooseTariffBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var format: NumberFormat

    private val viewModel: ChooseTariffViewModel by viewModels()

    private val args: ChooseTariffFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseTariffBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadObservers()
        loadViews()
    }

    private fun loadViews() {
        binding.apply {
            btnPlusBalance.setOnClickListener {
                findNavController().navigate(ChooseTariffFragmentDirections.actionChooseTariffFragmentToBalancePaymentFragment())
            }
            tariffInfo.apply {
                val data = args.tariffData
                btnActivate.hide()
                when (storage.language) {
                    LANG_UZ -> {
                        tariffName.text = data.title.uz
                    }
                    LANG_KRILL -> {
                        tariffName.text = data.title.uz_kr
                    }
                    LANG_RU -> {
                        tariffName.text = data.title.ru
                    }
                    LANG_EN -> {
                        tariffName.text = data.title.en
                    }
                }
                texCall.text = resources.getString(R.string.count_call, data.call.toString())
                texTop.text = resources.getString(R.string.count_top, data.top.toString())
                texView.text = resources.getString(R.string.count_view, data.post.toString())

                if (data.post == 0) {
                    imageType.setImageResource(R.drawable.ic_tariff_star)
                } else {
                    imageType.setImageResource(R.drawable.ic_premium)
                }

                val str = (data.price).toCurrency()
                tariffPrice.text = str

                if (args.balance < data.price) {
                    layoutError.show()
                    btnSave.disableWithAlpha()
                } else {
                    layoutError.hide()
                    btnSave.enableWithAlpha()
                }
            }

            val str = (args.balance).toCurrency()
            textBalance.text = str

            btnSave.setOnClickListener {
                viewModel.chooseService(args.tariffData, args.balance)
            }
//            btnPlusBalance.setOnClickListener { }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun loadObservers() {
        viewModel.chooseServiceData.observe(viewLifecycleOwner, chooseServiceDataObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)
        viewModel.message.observe(viewLifecycleOwner, messageObserver)
    }

    private val chooseServiceDataObserver = Observer<AuthResponseData> { userData ->
        if (userData != null) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(TariffsFragment.CHOOSE_TARIFF, Bundle().apply {
                putString(TariffsFragment.CHOOSE_TARIFF, args.tariffData._id)
            })
            findNavController().popBackStack()
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
}