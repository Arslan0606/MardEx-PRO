package uz.star.mardexworker.ui.screen.main_activity.news_fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentNewsBinding
import uz.star.mardexworker.databinding.FragmentNotificationBinding
import uz.star.mardexworker.model.response.news.NewsData
import uz.star.mardexworker.ui.adapter.recycler_view.JobItemRVAdapter
import uz.star.mardexworker.ui.adapter.recycler_view.NewsRVAdapter
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileViewModel
import uz.star.mardexworker.utils.extensions.hideBottomMenu
import uz.star.mardexworker.utils.extensions.hideMainLoader
import uz.star.mardexworker.utils.extensions.showMainLoader
import uz.star.mardexworker.utils.views.goneView
import uz.star.mardexworker.utils.views.showView
import javax.inject.Inject


@AndroidEntryPoint
class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage


    private val viewModel: NewsViewModel by viewModels()


    private lateinit var newsAdapter: NewsRVAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        hideMainLoader()
        hideBottomMenu()
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {
        viewModel.news.observe(viewLifecycleOwner, newsObserver)
        viewModel.loader.observe(viewLifecycleOwner, loaderObserver)

    }

    private fun loadViews() {
        viewModel.getNews()
        newsAdapter = NewsRVAdapter(storage.language)
        binding.newsRecyclerView.adapter = newsAdapter
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        newsAdapter.setOnItemClickListener {
            findNavController().navigate(NewsFragmentDirections.actionNewsFragment2ToNewsInfoFragment(it))
        }
    }

    private val newsObserver = Observer<List<NewsData>> { newsList ->
        binding.textSoon.goneView()
        binding.newsRecyclerView.showView(true)
        newsAdapter.submitList(newsList)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (b) showMainLoader() else hideMainLoader()
    }


}