package uz.star.mardexworker.ui.screen.own_notifications_activity.all_notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentAllOwnNotificationsBinding
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.ui.adapter.recycler_view.OwnNotificationAdapter
import uz.star.mardexworker.utils.extensions.showToast
import javax.inject.Inject

@AndroidEntryPoint
class AllOwnNotificationsFragment : Fragment() {

    private var _binding: FragmentAllOwnNotificationsBinding? = null
    private val binding: FragmentAllOwnNotificationsBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    lateinit var adapter: OwnNotificationAdapter

    @Inject
    lateinit var storage: LocalStorage

    private val viewModel: AllOwnNotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllOwnNotificationsBinding.inflate(layoutInflater)
        return binding.root
    }
    /*add*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {

            adapter = OwnNotificationAdapter(storage.language)
            adapter.submitList(listOf())
            rvNotifications.adapter = adapter

            adapter.setOnClickListener {
                findNavController().navigate(
                    AllOwnNotificationsFragmentDirections.actionAllOwnNotificationsFragmentToNotificationDetailFragment(it)
                )
            }

            btnBack.setOnClickListener {
                requireActivity().finish()
            }
            loadObservers()
        }
    }

    private fun loadObservers() {
        viewModel.getAllOwnNotifications()
        viewModel.allNotifications.observe(viewLifecycleOwner, allOwnNotificationsObserver)
    }

    private val allOwnNotificationsObserver = Observer<List<OwnNotificationResponse>> {
        adapter.submitList(it)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllOwnNotifications()
    }
}