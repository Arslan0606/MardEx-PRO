package uz.star.mardexworker.ui.screen.main_activity.news_fragment.news_info_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_notification.view.*
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.FragmentCameraBinding
import uz.star.mardexworker.databinding.FragmentNewsInfoBinding
import uz.star.mardexworker.model.response.news.toLocalString
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.camera_fragment.CameraFragmentArgs
import uz.star.mardexworker.ui.screen.main_activity.news_fragment.NewsFragment
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewsInfoFragment : Fragment() {

    private var _binding: FragmentNewsInfoBinding? = null
    private val binding: FragmentNewsInfoBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: NewsInfoFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewsInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {

    }

    private fun loadViews() {
        if (args.newsData != null) {
            val data = args.newsData
            binding.apply {
                textViewNewsInfoTitle.text = data?.title?.toLocalString()
                newsDescription.text = data?.description?.toLocalString()
                val f = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = data?.createdAt!!
                newsDate.text = f.format(calendar.time)
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}