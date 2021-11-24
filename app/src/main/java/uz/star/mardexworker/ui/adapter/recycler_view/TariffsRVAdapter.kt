package uz.star.mardexworker.ui.adapter.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.TariffItemRecyclerBinding
import uz.star.mardexworker.model.response.tariff.TariffData
import uz.star.mardexworker.utils.*
import uz.star.mardexworker.utils.extensions.bindItem
import uz.star.mardexworker.utils.extensions.enable
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.show
import java.text.NumberFormat

/**
 * Created by Botirali Kozimov on 14-03-21
 **/

class
TariffsRVAdapter(val lang: String, val format: NumberFormat) :
    ListAdapter<TariffData, TariffsRVAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {
    private var listenerClick: SingleBlock<TariffData>? = null

    companion object {
        var DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<TariffData>() {
            override fun areItemsTheSame(oldItem: TariffData, newItem: TariffData) =
                newItem._id == oldItem._id

            override fun areContentsTheSame(oldItem: TariffData, newItem: TariffData) =
                newItem.price == oldItem.price && newItem.deadline == oldItem.deadline &&
                        newItem.description == oldItem.description && newItem.title == oldItem.title
        }
    }

    fun setOnClickListener(block: SingleBlock<TariffData>) {
        listenerClick = block
    }

    inner class ViewHolder(private val binding: TariffItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

            }
        }

        fun bind() = bindItem {
            val data = getItem(adapterPosition)
            binding.apply {
                when (lang) {
                    LANG_UZ -> {
                        tariffName.text = data.title.uz
                    }
                    LANG_KRILL -> {
                        tariffName.text = data.title.uz_kr
                    }
                    LANG_RU -> {
                        tariffName.text = data.title.ru
                    }
                    LANG_EN -> {
                        tariffName.text = data.title.en
                    }
                }
                texCall.text = resources.getString(R.string.count_call, data.call.toString())
                texTop.text = resources.getString(R.string.count_top, data.top.toString())
                texView.text = resources.getString(R.string.count_view, data.post.toString())

                if (data.post == 0) {
                    imageType.setImageResource(R.drawable.ic_tariff_star)
                } else {
                    imageType.setImageResource(R.drawable.ic_premium)
                }
                val str = (data.price).toCurrency()
                tariffPrice.text = str

                btnActivate.setOnClickListener {
                    listenerClick?.invoke(data)
                }
                btnYourTariff.setOnClickListener {
                    listenerClick?.invoke(data)
                }
                if (data.type) {
                    btnYourTariff.enable()
                    btnYourTariff.show()
                    btnActivate.hide()
                } else {
                    btnActivate.enable()
                    btnActivate.show()
                    btnYourTariff.hide()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        TariffItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()
}