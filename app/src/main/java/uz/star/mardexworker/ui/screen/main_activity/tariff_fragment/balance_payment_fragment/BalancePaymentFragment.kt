package uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.balance_payment_fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentBalancePaymentBinding
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import uz.star.mardexworker.utils.extensions.showToast
import javax.inject.Inject


@AndroidEntryPoint
class BalancePaymentFragment : Fragment() {

    private var _binding: FragmentBalancePaymentBinding? = null
    private val binding: FragmentBalancePaymentBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalancePaymentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideBottomMenu()
        binding.apply {
            buttonBalanceIdCopy.setOnClickListener {
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", storage.payId)
                clipboardManager.setPrimaryClip(clipData)

                showToast(R.string.adder_to_clipboard)
            }
            backButton.setOnClickListener { findNavController().navigateUp() }
            textPaymentId.text = storage.payId
            cvPayme.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://payme.uz/home/payment"))
                startActivity(browserIntent)
            }

            cvClick.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://click.uz/ru#service_id=17357"))
                startActivity(browserIntent)
            }
        }
    }
}