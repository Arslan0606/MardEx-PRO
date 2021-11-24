package uz.star.mardexworker.ui.screen.main_activity.orders_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.FragmentOrdersBinding
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.ui.adapter.view_pager.OrdersPagerAdapter
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsActivity
import uz.star.mardexworker.utils.extensions.show
import uz.star.mardexworker.utils.views.goneView

/**
 * Created by Botirali Kozimov on 29.03.2021
 */

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding: FragmentOrdersBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val adapter by lazy { OrdersPagerAdapter(requireActivity()) }

    private val viewModel: OrdersViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadViews()
        loadObserver()

    }

    private fun loadObserver() {
        viewModel.getUnReadOwnNotificationsCount()
        viewModel.unReadNotifications.observe(viewLifecycleOwner, unReadNotificationsObserver)

    }

    private val unReadNotificationsObserver = Observer<List<OwnNotificationResponse>> {
        if (it.isNotEmpty()&& it != null) {
            binding.txtCountNotifications.text = it.size.toString()
            binding.txtCountNotifications.show()
        } else {
            binding.txtCountNotifications.goneView()
        }
    }


    private fun loadViews() {
        binding.apply {
            notification.setOnClickListener {
//                findNavController().navigate(OrdersFragmentDirections.actionOrdersFragmentToNewsFragment())
                startActivity(Intent(requireActivity(), OwnNotificationsActivity::class.java))
            }
            pager.adapter = adapter
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.setText(R.string.fast_orders)
                    }
                    1 -> {
                        tab.setText(R.string.orders)
                    }
                }
            }.attach()

            menuButton.setOnClickListener {
                (requireActivity() as MainActivity).openDrawer()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}