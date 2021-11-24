package uz.star.mardexworker.ui.screen.entry_activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.ActivityEntryBinding
import uz.star.mardexworker.utils.MyContextWrapper
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.show

@AndroidEntryPoint
class EntryActivity : AppCompatActivity() {

    private var _binding: ActivityEntryBinding? = null
    private val binding: ActivityEntryBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("T12T", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            LocalStorage.instance.notificationToken = token ?: ""
        })
    }

    fun hideLoader() {
        binding.loader.root.hide()
    }

    fun showLoader() {
        binding.loader.root.show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LocalStorage.instance.langLocal))
    }

    @Suppress("DEPRECATION")
    fun changeLang() {
        val context: Context = MyContextWrapper.wrap(this, LocalStorage.instance.langLocal)
        resources.updateConfiguration(
            context.resources.configuration,
            context.resources.displayMetrics
        )
    }
}