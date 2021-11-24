package uz.star.mardexworker.ui.screen.entry_activity.intro_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentIntroBinding
import uz.star.mardexworker.model.response.intro.IntroData
import uz.star.mardexworker.ui.adapter.view_pager.intro.IntroAdapter
import uz.star.mardexworker.utils.extensions.changeStatusColorWhite
import uz.star.mardexworker.utils.extensions.showToast
import javax.inject.Inject

@AndroidEntryPoint
class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding: FragmentIntroBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val adapter = IntroAdapter()
    private val viewModel: IntroViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    private var currentIndex = 0
    private var listSize = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changeStatusColorWhite()
        _binding = FragmentIntroBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        binding.pager.adapter = adapter

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentIndex = position
//                if (currentIndex >= listSize) findNavController().navigate(R.id.)
            }
        })

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    private fun loadPagerButtons() {
        binding.dotsIndicator.setViewPager2(binding.pager)
        binding.btnSave.setOnClickListener {
            currentIndex++
            if (currentIndex < listSize)
                binding.pager.setCurrentItem(binding.pager.currentItem + 1, true)
            else {
                storage.completeIntro = true
                findNavController().navigate(R.id.action_introFragment2_to_loginFragment)
            }
        }

    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.message.observe(this, messageObserver)
        viewModel.introData.observe(this, introDataObserver)
    }

    private val messageObserver = Observer<String> { message ->
        showMessage(message)
    }

    private val introDataObserver = Observer<List<IntroData>> { list ->
        adapter.submitList(list)
        listSize = list.size
        loadPagerButtons()
    }

    private fun showMessage(text: String) {
        showToast(text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}