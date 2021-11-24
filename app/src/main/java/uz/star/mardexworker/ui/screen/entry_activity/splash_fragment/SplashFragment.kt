package uz.star.mardexworker.ui.screen.entry_activity.splash_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentSplashBinding
import uz.star.mardexworker.firebase.FirebaseMessagingServiceHelper.Companion.notificationSound
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            notificationSound.stop()
        } catch (E: Exception) {

        }

    }

    @Inject
    lateinit var storage: LocalStorage

    private var _binding: FragmentSplashBinding? = null
    private val binding: FragmentSplashBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        setActions()

        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    private fun setActions() {
        Executors.newSingleThreadExecutor().execute {
            Thread.sleep(2000)
            requireActivity().runOnUiThread {
                val versionId = BuildConfig.VERSION_CODE
                if (storage.versionId == -1 || storage.versionId == versionId) {
                    storage.versionId = versionId

                    if (storage.isFirst) {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragment2ToLanguageFragment2())
                    } else {
                        if (storage.logged) {
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            if (!storage.completeIntro) {
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragment2ToIntroFragment2())
                            } else {
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragment2ToLoginFragment())
                            }
                        }
                    }
                    return@runOnUiThread
                } else
                    if (storage.versionId != versionId) {
                        storage.versionId = versionId
                        storage.logged = false

                        if (!storage.completeIntro) {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragment2ToIntroFragment2())
                        } else {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragment2ToLoginFragment())
                        }
                    }
            }
        }
    }
}