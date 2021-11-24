package uz.star.mardexworker.ui.adapter.view_pager

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment.OrdersTabsFragment

/**
 * Created by Botirali Kozimov on 29.03.2021
 */

class OrdersPagerAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OrdersTabsFragment().apply { arguments = bundleOf(Pair("type", "1")) }
        else -> OrdersTabsFragment().apply { arguments = bundleOf(Pair("type", "2")) }
    }
}
