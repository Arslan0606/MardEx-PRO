package uz.star.mardexworker.ui.screen.main_activity.change_language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentChangeLanguageBinding
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_KRILL_LOCAL
import uz.star.mardexworker.utils.LANG_RU
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import javax.inject.Inject

@AndroidEntryPoint
class ChangeLanguageFragment : Fragment() {

    private var _binding: FragmentChangeLanguageBinding? = null
    private val binding: FragmentChangeLanguageBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage
    private lateinit var lan: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeLanguageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomMenu()
        loadViews()
    }

    private fun loadViews() {
        binding.apply {
            firstLan = storage.langLocal
            loadLangOptions()

            imageViewHeaderExit.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private lateinit var firstLan: String

    @Suppress("DEPRECATION")
    private fun loadLangOptions() {
        binding.apply {
            lan = storage.langLocal

            btnUzb.setOnClickListener {
                bgUz()
                changeLang("default")
                storage.language = LANG_UZ
            }

            btnRus.setOnClickListener {
                bgRus()
                changeLang(LANG_RU)
                storage.language = LANG_RU
            }

            btnUzbKirill.setOnClickListener {
                bgKirill()
                changeLang(LANG_KRILL_LOCAL)
                storage.language = LANG_KRILL
            }

            val checkedBg = when (lan) {
                "default" -> {
                    bgUz()
                    checkBackgroundUzb
                }
                LANG_RU -> {
                    bgRus()
                    checkBackgroundRus
                }
                LANG_KRILL_LOCAL -> {
                    bgKirill()
                    checkBackgroundUzbKirill
                }
                else -> {
                    bgUz()
                    checkBackgroundUzb
                }
            }
            checkedBg.setCardBackgroundColor(resources.getColor(R.color.language_selected))
        }
    }

    private fun bgUz() {
        binding.apply {
            checkBackgroundUzb.setCardBackgroundColor(resources.getColor(R.color.language_selected))
            checkBackgroundRus.setCardBackgroundColor(resources.getColor(R.color.light_white))
            checkBackgroundUzbKirill.setCardBackgroundColor(resources.getColor(R.color.light_white))
        }
    }

    private fun bgRus() {
        binding.apply {
            checkBackgroundRus.setCardBackgroundColor(resources.getColor(R.color.language_selected))
            checkBackgroundUzb.setCardBackgroundColor(resources.getColor(R.color.light_white))
            checkBackgroundUzbKirill.setCardBackgroundColor(resources.getColor(R.color.light_white))
        }
    }

    private fun bgKirill() {
        binding.apply {
            checkBackgroundUzbKirill.setCardBackgroundColor(resources.getColor(R.color.language_selected))
            checkBackgroundUzb.setCardBackgroundColor(resources.getColor(R.color.light_white))
            checkBackgroundRus.setCardBackgroundColor(resources.getColor(R.color.light_white))
        }
    }

    private fun changeLang(language: String) {
        storage.langLocal = language
        (activity as MainActivity).changeLang()
        (activity as MainActivity).recreateUI()
        binding.apply {
            textViewHeaderName.text = getString(R.string.change_language)
        }
    }
}