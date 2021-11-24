package uz.star.mardexworker.ui.screen.own_notifications_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_navigate.*
import uz.star.mardexworker.databinding.ActivityOwnNotificationsBinding

@AndroidEntryPoint
class OwnNotificationsActivity : AppCompatActivity() {

    private var _binding: ActivityOwnNotificationsBinding? = null
    private val binding: ActivityOwnNotificationsBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOwnNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadViews()
    }

    private fun loadViews() {
        binding.apply {
        }
    }


}