package uz.star.mardexworker.ui.screen.main_activity

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.BuildConfig
import uz.star.mardexworker.R
import uz.star.mardexworker.app.App
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.ActivityMainBinding
import uz.star.mardexworker.firebase.FirebaseMessagingServiceHelper
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.screen.entry_activity.EntryActivity
import uz.star.mardexworker.ui.screen.main_activity.activity_components.MainViewModel
import uz.star.mardexworker.utils.MyContextWrapper
import uz.star.mardexworker.utils.darkmode.ModeChanger
import uz.star.mardexworker.utils.extensions.checkPermission
import uz.star.mardexworker.utils.extensions.hide
import uz.star.mardexworker.utils.extensions.show
import uz.star.mardexworker.utils.livedata.Event
import uz.star.mardexworker.utils.livedata.EventObserver
import uz.star.mardexworker.utils.playStoreUrl
import uz.star.mardexworker.utils.shareText
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    val socketLiveData = MutableLiveData<Event<SocketResponseData?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        binding.apply {
            /*val navController = findNavController(R.id.fragment)
            mainContent.bottomNavigationView.setupWithNavController(navController)*/

            val navController = Navigation.findNavController(this@MainActivity, R.id.fragment)
            mainContent.bottomNavigationView.setupWithNavController(navController)
            binding.navigationView.setupWithNavController(navController)
            NavigationUI.setupWithNavController(binding.navigationView, navController)

            navigationView.itemIconTintList = null
            headerLayout.imageViewHeaderExit.setOnClickListener {
                closeDrawer()
            }
            btnLogOut.setOnClickListener {
                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.log_out))
                    .setMessage(getString(R.string.log_out_content))
                    .setPositiveButton(getString(R.string.log_out)) { _, _ -> // continue with delete
                        logout()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                        // do nothing
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                dialog.setOnShowListener {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this@MainActivity, R.color.new_green))
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                }
                dialog.show()
            }
            switchNightMode.isChecked = ModeChanger.getBooleanStatusMode(this@MainActivity)
            switchNightMode.setOnCheckedChangeListener { _, isChecked ->
                ModeChanger.setMode(isChecked, this@MainActivity)
            }
            layoutNightMode.setOnClickListener { switchNightMode.performClick() }
            layoutShare.setOnClickListener {
                shareText(playStoreUrl())
            }

            layoutLanguage.setOnClickListener {
                findNavController(R.id.fragment).navigate(R.id.changeLanguageFragment)
                closeDrawer()
            }

            layoutNews.setOnClickListener {
                findNavController(R.id.fragment).navigate(R.id.newsFragment2)
                closeDrawer()
            }

            layoutPromocode.setOnClickListener {
                findNavController(R.id.fragment).navigate(R.id.promocodeFragment)
                closeDrawer()
            }

            layoutSettings.setOnClickListener {
                findNavController(R.id.fragment).navigate(R.id.editFragment2)
                closeDrawer()
            }

            layoutCall.setOnClickListener {
                call()
            }

            layoutMardex.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mardex.uz"))
                startActivity(browserIntent)
            }
        }
        initDrawerContent()
        binding.textVersion.text = getString(R.string.formatted_mardex_v, BuildConfig.VERSION_NAME)
    }

    private fun call() {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.fromParts("tel", getString(R.string._998_93_000_88_55_call), null)
        startActivity(callIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        findNavController(R.id.editFragment2)
        return super.onSupportNavigateUp()
    }

    fun initDrawerContent() {
        binding.headerLayout.textViewHeaderName.text = storage.name
        binding.headerLayout.textViewPhone.text = storage.phone
    }

    private fun loadObservers() {
        viewModel.callResLiveData.observe(
            this,
            callFromUserObserver
        )
    }

    private val callFromUserObserver = EventObserver<SocketResponseData?> { data ->
        if (data != null) {
            binding.mainContent.bottomNavigationView.apply {
                if (selectedItemId != R.id.homeFragment) {
                    selectedItemId = R.id.homeFragment
                }
            }
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(10001520)
            socketLiveData.value = Event(data)
        }
    }

    fun hideLoader() {
        binding.mainContent.loader.root.hide()
    }

    fun showLoader() {
        binding.mainContent.loader.root.show()
    }

    fun hideBottomMenu() {
        try {
            binding.mainContent.bottomNavigationViewLayout.hide()
        } catch (e: Exception) {

        }
    }

    fun showBottomMenu() {
        try {
            binding.mainContent.bottomNavigationViewLayout.show()
        } catch (e: Exception) {

        }
    }

    fun openDrawer() {
        binding.apply {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun closeDrawer() {
        binding.apply {
            drawerLayout.close()
        }
    }

    override fun onPause() {
        viewModel.disconnectSocket()
        super.onPause()
        App.isForeground = false
    }

    override fun onResume() {
        super.onResume()
        viewModel.connectSocket()
        App.isForeground = true
        try {
            FirebaseMessagingServiceHelper.notificationSound.stop()
        } catch (E: Exception) {

        }

    }

    fun logout() {
        storage.currentLat = 0.0
        storage.currentLong = 0.0
        storage.expiredDate = 0
        storage.id = ""
        storage.isActive = false
        storage.lastCreatedPayment = 0
        storage.lastPaymentAmount = 0
        storage.logged = false
        storage.paymentId = ""
        storage.mapState = true
        storage.token = ""

        startActivity(Intent(this, EntryActivity::class.java))
        finish()
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

    fun recreateUI() {
        binding.apply {
            settingsText.setText(R.string.settings)
            newsText.setText(R.string.news)
//            navigationView.menu[2].setTitle(R.string.how_to_use)
            languageText.setText(R.string.soft_lang)
            switchText.setText(R.string.night_mode)
            shareText.setText(R.string.share)
            textOurSite.setText(R.string.t_site)
            btnLogOut.setText(R.string.menu_logout)

            mainContent.bottomNavigationView.menu[0].setTitle(R.string.title_home)
            mainContent.bottomNavigationView.menu[1].setTitle(R.string.title_orders)
            mainContent.bottomNavigationView.menu[2].setTitle(R.string.title_tariff)
            mainContent.bottomNavigationView.menu[3].setTitle(R.string.title_profile)
        }
    }
}


