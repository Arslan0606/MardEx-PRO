package uz.star.mardexworker.ui.adapter.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.databinding.OrdersItemRecyclerBinding
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.utils.LANG_KRILL_LOCAL
import uz.star.mardexworker.utils.extensions.show
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

class OrdersItemRVAdapter(
    val lang: String,
    val fastType: Boolean = true
) :
    ListAdapter<GetOrdersResponse, OrdersItemRVAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {
    private var endListener: ((GetOrdersResponse, Int) -> Unit)? = null

    companion object {
        var DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<GetOrdersResponse>() {
            override fun areItemsTheSame(oldItem: GetOrdersResponse, newItem: GetOrdersResponse) =
                newItem.id == oldItem.id

            override fun areContentsTheSame(oldItem: GetOrdersResponse, newItem: GetOrdersResponse) =
                newItem == oldItem
        }
    }

    inner class ViewHolder(private val binding: OrdersItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                val data = getItem(adapterPosition)
                if (fastType) {
                    textJobDesc.text = data.desc
                    val title =
                        if (lang == "default") data.jobId?.title?.uz else if (lang == LANG_KRILL_LOCAL) data.jobId?.title?.uz_kr else data.jobId?.title?.ru
                    textCategory.text = title
                } else {
                    textStatus.show()
                    textStatusTemp.show()
                    textOrdersCount.show()
                    textOrdersCountTemp.show()
                }

                layoutOrder.setOnClickListener {
                    endListener?.invoke(data, adapterPosition)
                }

                val f = SimpleDateFormat("dd.MM.yyyy")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = data.createdAt ?: 0
                textData.text = f.format(calendar.time)

                buttonMore.setOnClickListener { layoutOrder.performClick() }
            }
        }
    }

    fun clickEnd(f: (GetOrdersResponse, Int) -> Unit) {
        endListener = f
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        OrdersItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()
}