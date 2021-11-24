package uz.star.mardexworker.ui.adapter.recycler_view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.app.App
import uz.star.mardexworker.databinding.ItemStatBinding
import uz.star.mardexworker.model.response.tariff.statsrvmodel.StatsOuterModel
import uz.star.mardexworker.utils.SingleBlock
import uz.star.mardexworker.utils.extensions.bindItem
import uz.star.mardexworker.utils.toCurrency
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class StatsRVAdapter(val lang: String, val numberFormat: NumberFormat) :
    ListAdapter<StatsOuterModel, StatsRVAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {
    private var listenerClick: SingleBlock<StatsOuterModel>? = null

    companion object {
        var DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<StatsOuterModel>() {
            override fun areItemsTheSame(oldItem: StatsOuterModel, newItem: StatsOuterModel) =
                newItem.date == oldItem.date

            override fun areContentsTheSame(oldItem: StatsOuterModel, newItem: StatsOuterModel) =
                newItem.amount == oldItem.amount && newItem.data == oldItem.data
        }
    }

    fun setOnClickListener(block: SingleBlock<StatsOuterModel>) {
        listenerClick = block
    }

    inner class ViewHolder(private val binding: ItemStatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

            }
        }

        @SuppressLint("SimpleDateFormat")
        fun bind() = bindItem {
            val data = getItem(adapterPosition)
            binding.apply {
                val adapter = StatsItemRVAdapter(lang, numberFormat)
                list.layoutManager =
                    LinearLayoutManager(App.instance, LinearLayoutManager.VERTICAL, false)
                list.adapter = adapter
                adapter.submitList(data.data)
                adapter.setOnClickListener {

                }

                val f = SimpleDateFormat("dd.MM.yyyy")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = data.date
                tvDay.text = f.format(calendar.time)
                val str = (data.amount).toCurrency()
                tvSum.text = str

                cardItem.setOnClickListener {
                    listenerClick?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()
}