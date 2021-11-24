package uz.star.mardexworker.ui.screen.entry_activity.language_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentLanguageBinding
import uz.star.mardexworker.ui.screen.entry_activity.EntryActivity
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_KRILL_LOCAL
import uz.star.mardexworker.utils.LANG_RU
import uz.star.mardexworker.utils.LANG_UZ
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding: FragmentLanguageBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage
    private lateinit var lan: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadViews()
    }

    private fun loadViews() {
        binding.apply {
            loadLangOptions()

            backButton.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnSave.setOnClickListener {
                storage.isFirst = false
                storage.language = lan
                findNavController()
                    .navigate(LanguageFragmentDirections.actionLanguageFragment2ToIntroFragment2())
            }
        }
        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    @Suppress("DEPRECATION")
    private fun loadLangOptions() {
        binding.apply {
            lan = storage.langLocal
            val checkedBg = when (lan) {
                "default" -> checkBackgroundUzb
                LANG_RU -> checkBackgroundRus
                LANG_KRILL_LOCAL -> checkBackgroundUzbKirill
                else -> checkBackgroundUzb
            }
            checkedBg.setCardBackgroundColor(resources.getColor(R.color.language_selected))

            btnUzb.setOnClickListener {
                checkBackgroundUzb.setCardBackgroundColor(resources.getColor(R.color.language_selected))
                checkBackgroundRus.setCardBackgroundColor(resources.getColor(R.color.light_white))
                checkBackgroundUzbKirill.setCardBackgroundColor(resources.getColor(R.color.light_white))
                changeLang("default")
                lan = LANG_UZ
            }

            btnRus.setOnClickListener {
                checkBackgroundRus.setCardBackgroundColor(resources.getColor(R.color.language_selected))
                checkBackgroundUzb.setCardBackgroundColor(resources.getColor(R.color.light_white))
                checkBackgroundUzbKirill.setCardBackgroundColor(resources.getColor(R.color.light_white))
                changeLang(LANG_RU)
                lan = LANG_RU
            }

            btnUzbKirill.setOnClickListener {
                checkBackgroundUzbKirill.setCardBackgroundColor(resources.getColor(R.color.language_selected))
                checkBackgroundUzb.setCardBackgroundColor(resources.getColor(R.color.light_white))
                checkBackgroundRus.setCardBackgroundColor(resources.getColor(R.color.light_white))
                changeLang(LANG_KRILL_LOCAL)
                lan = LANG_KRILL
            }

            btnUzb.performClick()
        }
    }

    private fun changeLang(language: String) {
        storage.langLocal = language
        (activity as EntryActivity).changeLang()
        binding.apply {
            textTitle.text = getString(R.string.mardex_dasturiga_xush_kelibsiz)
            textChooseLanguage.text = getString(R.string.choose_language)
            btnSave.text = getString(R.string.continue_)
        }
    }

}