package uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentOrdersTabsBinding
import uz.star.mardexworker.model.enum.OrderStatusEnum
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.ui.adapter.recycler_view.OrdersItemRVAdapter
import uz.star.mardexworker.ui.dialogs.OrderInfoDialog
import uz.star.mardexworker.ui.screen.navigate_activity.NavigateActivity
import uz.star.mardexworker.utils.extensions.show
import uz.star.mardexworker.utils.extensions.showBottomMenu
import uz.star.mardexworker.utils.views.goneView
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 29.03.2021
 */

@AndroidEntryPoint
class OrdersTabsFragment : Fragment() {

    private var _binding: FragmentOrdersTabsBinding? = null
    private val binding: FragmentOrdersTabsBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: OrdersViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage
    private lateinit var adapterFirst: OrdersItemRVAdapter
    private lateinit var adapterSecond: OrdersItemRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersTabsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomMenu()

        loadObservers()
        loadViews()
        if (arguments?.getString("type") == "1") {
            viewModel.getOrders()
        }
    }

    private fun loadObservers() {
        viewModel.responseOrderPatch.observe(viewLifecycleOwner, patchOrderObserver)
        viewModel.orders.observe(viewLifecycleOwner, ordersObserver)
    }

    private val ordersObserver = Observer<List<GetOrdersResponse>> { ls ->
        if (ls.isEmpty())
            return@Observer
        if (ls.last().isFinish == false) {
            adapterFirst.submitList(arrayListOf(ls.last().copy()))
            binding.textFirstCount.text = getString(R.string.text_count, 1.toString())

            val list = ls.toMutableList()
            list.removeLast()
            val d = list.filter { it.status == OrderStatusEnum.SUCCESS.text }
            adapterSecond.submitList(d)

            binding.textSecondCount.text = getString(R.string.text_count, d.size.toString())
        } else {
            val d = ls.filter { it.status == OrderStatusEnum.SUCCESS.text }
            adapterSecond.submitList(d)

            binding.textSecondCount.text = getString(R.string.text_count, d.size.toString())
        }
    }

    private val patchOrderObserver = Observer<FastOrderResponseData> {
        if ((it.isFinish == true)) {
            storage.orderType = true
            binding.apply {

            }
        }
    }

    private fun loadViews() {
        binding.apply {
            if (arguments?.getString("type") == "2") {
                layoutCenter.goneView()
                textSoon.show()
            } else {
                adapterFirst = OrdersItemRVAdapter(storage.langLocal)
                firstList.adapter = adapterFirst

                adapterSecond = OrdersItemRVAdapter(storage.langLocal)
                secondList.adapter = adapterSecond

                adapterFirst.clickEnd { it, id ->
                    val dialog = OrderInfoDialog()
                    dialog.groupData = it
                    dialog.clickEnd {
                        it.status = OrderStatusEnum.SUCCESS.text
                        viewModel.patchOrder(it)
                        adapterFirst.submitList(arrayListOf())
                        binding.textFirstCount.text = getString(R.string.text_count, 0.toString())

                        val l = adapterSecond.currentList.toMutableList()
                        it.isFinish = true
                        l.add(it)
                        adapterSecond.submitList(l)
                        binding.textSecondCount.text = getString(R.string.text_count, l.size.toString())
                    }
                    dialog.clickNavigate {
                        val intent = Intent(requireContext(), NavigateActivity::class.java)
                        intent.putExtra("data", it)
                        intent.putExtra("avatar", storage.avatar)
                        startActivity(intent)
                    }
                    dialog.clickCancel {
                        it.status = OrderStatusEnum.CANCEL_USER.text
                        viewModel.patchOrder(it)
                        adapterFirst.submitList(arrayListOf())
                        binding.textFirstCount.text = getString(R.string.text_count, 0.toString())
                    }
                    dialog.show(childFragmentManager, "add")
                }

                adapterSecond.clickEnd { it, id ->
                    val dialog = OrderInfoDialog()
                    dialog.groupData = it
                    dialog.clickEnd {
                    }
                    dialog.clickCancel {
                    }
                    dialog.clickNavigate {
                        val intent = Intent(requireContext(), NavigateActivity::class.java)
                        intent.putExtra("data", it)
                        intent.putExtra("avatar", storage.avatar)
                        startActivity(intent)
                    }
                    dialog.show(childFragmentManager, "add")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}