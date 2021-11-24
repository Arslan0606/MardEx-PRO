package uz.star.mardexworker.ui.adapter.recycler_view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import uz.star.mardexworker.R
import uz.star.mardexworker.data.models.tariff.statsrvmodel.Type
import uz.star.mardexworker.databinding.ItemPaymentBinding
import uz.star.mardexworker.model.response.tariff.statsrvmodel.StatsInnerModel
import uz.star.mardexworker.utils.SingleBlock
import uz.star.mardexworker.utils.extensions.bindItem
import java.text.NumberFormat

class StatsItemRVAdapter(val lang: String, val numberFormat: NumberFormat) :
    RecyclerView.Adapter<StatsItemRVAdapter.ViewHolder>() {
    private var listenerClick: SingleBlock<StatsInnerModel>? = null

    private val adapterCallback = object : SortedListAdapterCallback<StatsInnerModel>(this) {
        override fun areItemsTheSame(item1: StatsInnerModel, item2: StatsInnerModel): Boolean {
            return item1._id == item2._id
        }

        override fun compare(o1: StatsInnerModel, o2: StatsInnerModel): Int =
            o1.perform_time!!.compareTo(o2.perform_time!!)

        override fun areContentsTheSame(
            oldItem: StatsInnerModel,
            newItem: StatsInnerModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val sortedList = SortedList(StatsInnerModel::class.java, adapterCallback)

    fun setOnClickListener(block: SingleBlock<StatsInnerModel>) {
        listenerClick = block
    }

    inner class ViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

            }
        }

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind() = bindItem {
            val data = sortedList[adapterPosition]
            binding.apply {
                tvSum.text = numberFormat.format(data.amount)

                /*val f = SimpleDateFormat("hh:mm")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = data.perform_time!!
                tvTime.text = f.format(calendar.time)*/

                if (data.type == Type.PLUS) {
                    img.setImageResource(R.drawable.ic_input_money)
                    tvAbout.text = binding.root.context.getString(R.string.payment_income, data.name)
                } else {
                    tvAbout.text = binding.root.context.getString(R.string.payment_outcome)
                    img.setImageResource(R.drawable.ic_payment_out)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()
    override fun getItemCount(): Int = sortedList.size()
    fun submitList(data: List<StatsInnerModel>) {
        sortedList.replaceAll(data)
        notifyDataSetChanged()
    }
}