package uz.star.mardexworker.ui.adapter.recycler_view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.star.mardexworker.R
import uz.star.mardexworker.databinding.OwnNotificationItemBinding
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.utils.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Davronbek Raximjanov on 27/06/21
 **/

class OwnNotificationAdapter(private val language: String) :
    ListAdapter<OwnNotificationResponse, OwnNotificationAdapter.ViewHolder>(DIFF_JOB_CALLBACK) {

    private var clickListener: SingleBlock<OwnNotificationResponse>? = null


    inner class ViewHolder(private val binding: OwnNotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind() {
            val notificationData = getItem(adapterPosition)
            binding.apply {

                when (language) {
                    LANG_UZ -> {
                        txtTitle.text = notificationData.title!!.uz
                    }
                    LANG_RU -> {
                        txtTitle.text = notificationData.title!!.ru
                    }
                    LANG_KRILL -> {
                        txtTitle.text = notificationData.title!!.uzKr
                    }
                    LANG_EN -> {
                        txtTitle.text = notificationData.title!!.en
                    }
                }

                val f = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = notificationData.createdAt!!
                txtDate.text = f.format(calendar.time)

                if (!notificationData.isRead) {
                    layoutCl.setBackgroundResource(R.drawable.back_notif_un_read)
                }

                root.setOnClickListener {
                    clickListener?.invoke(notificationData)
                }


            }
        }
    }

    fun setOnClickListener(f: SingleBlock<OwnNotificationResponse>) {
        clickListener = f
    }

    companion object {
        var DIFF_JOB_CALLBACK = object : DiffUtil.ItemCallback<OwnNotificationResponse>() {
            override fun areItemsTheSame(oldItem: OwnNotificationResponse, newItem: OwnNotificationResponse) =
                newItem.id == oldItem.id

            override fun areContentsTheSame(oldItem: OwnNotificationResponse, newItem: OwnNotificationResponse): Boolean {
                return newItem.title!!.uz == oldItem.title!!.uz && newItem.body!!.uz == oldItem.body!!.uz && newItem.createdAt == oldItem.createdAt
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        OwnNotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()
}