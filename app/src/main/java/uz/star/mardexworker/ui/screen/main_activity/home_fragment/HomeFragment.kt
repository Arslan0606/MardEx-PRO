package uz.star.mardexworker.ui.screen.main_activity.home_fragment

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentHomeBinding
import uz.star.mardexworker.model.request.OpportunityEnum
import uz.star.mardexworker.model.response.EditWorkerHomeRequestData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.LocationRequest
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.dialogs.CallNotificationDialog
import uz.star.mardexworker.ui.dialogs.DialogMaker
import uz.star.mardexworker.ui.dialogs.OrderInfoDialog
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.ui.screen.navigate_activity.NavigateActivity
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsActivity
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatus
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatusLiveData
import uz.star.mardexworker.utils.customlivedatas.network.ConnectionLiveData
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.helpers.showAlertDialog
import uz.star.mardexworker.utils.helpers.showMessage
import uz.star.mardexworker.utils.livedata.EventObserver
import uz.star.mardexworker.utils.views.goneView
import java.lang.reflect.Method
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment()/*, DrivingSession.DrivingRouteListener*/ {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    /*@Inject
    lateinit var gpsTracker: GPSTracker*/

    @Inject
    lateinit var dialogMaker: DialogMaker

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var gpsStatusLiveData: GpsStatusLiveData

    @Inject
    lateinit var format: NumberFormat

    private val viewModel: HomeViewModel by viewModels()

    private var editData: EditWorkerHomeRequestData? = null

//    private val mapObjects: MapObjectCollection by lazy { binding.map.map.mapObjects.addCollection() }

    /*private var drivingSession: DrivingSession? = null
    private val drivingRouter: DrivingRouter by lazy {
        DirectionsFactory.getInstance().createDrivingRouter()
    }*/

    /*private var userPlaceMarkMapObject: MapObject? = null
    private var userPolylineMapObject: MapObject? = null*/

    private var paymentStatusData: CheckPaymentStatusData? = null
    private var dialog: CallNotificationDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        DirectionsFactory.initialize(requireContext())
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showBottomMenu()
        loadObservers()
        loadViews()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadViews() {
        if (storage.currentLat != 0.0) {
            moveMapTo(Point(storage.currentLat, storage.currentLong), 17.0f)
        }

        binding.apply {
            if (storage.mapState) {
                map.map.isLiteModeEnabled = true
                buttonDayNight.setImageResource(R.drawable.ic_day_night)
            } else {
                map.map.isNightModeEnabled = true
                buttonDayNight.setImageResource(R.drawable.ic_night)
            }

            layoutNavigate.setOnClickListener {
                getLocation { success, point ->
                    if (success && point != null) {
                        editData?.location = LocationRequest("Point", listOf(point.latitude, point.longitude))
                        editData?.let { it1 ->
                            viewModel.update(it1)
                        }
                        updateLocalLocation(point)
                    }
                }
            }

            layoutZoomIn.setOnClickListener {
                binding.map.zoomIn()
            }

            layoutZoomOut.setOnClickListener {
                binding.map.zoomOut()
            }

            layoutDayNight.setOnClickListener {
                apply {
                    map.map.isNightModeEnabled = !map.map.isNightModeEnabled
                    map.map.isLiteModeEnabled = !map.map.isLiteModeEnabled
                    storage.mapState = !storage.mapState
                    if (!storage.mapState) {
                        buttonDayNight.setImageResource(R.drawable.ic_night)
                    } else {
                        buttonDayNight.setImageResource(R.drawable.ic_day_night)
                    }
                }
            }

            menuButton.setOnClickListener {
                (requireActivity() as MainActivity).openDrawer()
            }

            notification.setOnClickListener {
//                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewsFragment())
                startActivity(Intent(requireActivity(), OwnNotificationsActivity::class.java))
            }

            buttonFreeUnfree.setOnClickListener {
                if (storage.isActive && paymentStatusData?.status == true && viewModel.getGpsStatus()) {
                    if (!storage.freeState) {
                        if (storage.callCount == 0) {
                            showMessage(R.string.call_count_is_null)
                        } else {
                            storage.freeState = true
                            makeWorkerFree()
                        }
                    } else {
                        storage.freeState = false
                        makeWorkerUnFree()
                    }
                } else {
                    hideBottomMenu()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProblemsFragment())
                }
            }

            outerLayout.changeFreeButtonState(storage.freeState, imageCheck, imageArrow)
            textFreeUnfree.changeFreeTextState(storage.freeState)

            if (storage.freeState)
                binding.textFreeUnfree.text = getString(R.string.free)/* else getString(R.string.unfree)*/
        }
        viewModel.getOrders()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        gpsStatusLiveData.observe(
            viewLifecycleOwner,
            gpsObserver
        )
        viewModel.checkPaymentStatus.observe(viewLifecycleOwner, getCheckPaymentStatus)
        viewModel.worker.observe(this, getWorker)
        viewModel.initEdit.observe(this, initEdit)
        (requireActivity() as MainActivity).viewModel.worker.observe(this, getWorker)
        (requireActivity() as MainActivity).viewModel.closeDialogLiveData.observe(
            viewLifecycleOwner,
            closeDialogObserver
        )
        (requireActivity() as MainActivity).viewModel.freeState.observe(
            viewLifecycleOwner,
            freeStateObserver
        )
        viewModel.responseUserUpdateData.observe(this, updateWorkerObserver)
        viewModel.responseOrderPost.observe(viewLifecycleOwner, postOrderObserver)
        viewModel.responseOrderPatch.observe(viewLifecycleOwner, patchOrderObserver)
        viewModel.orders.observe(viewLifecycleOwner, ordersObserver)

        (requireActivity() as MainActivity).socketLiveData.observe(
            viewLifecycleOwner,
            socketObserver
        )
        viewModel.message.observe(requireActivity(), messageObserver)

        viewModel.getUnReadOwnNotificationsCount()
        viewModel.unReadNotifications.observe(viewLifecycleOwner, unReadNotificationsObserver)

    }

    private val unReadNotificationsObserver = Observer<List<OwnNotificationResponse>> {
        if (it.isNotEmpty() && it != null) {
            binding.txtCountNotifications.text = it.size.toString()
            binding.txtCountNotifications.show()
        } else {
            binding.txtCountNotifications.goneView()
        }
    }

    private val messageObserver = Observer<MessageData> { message ->
        message.onResource {
            showMessage(it)
        }
    }

    private val gpsObserver = Observer<GpsStatus> { status ->
        status?.let {
            updateGpsCheckUI(status)
        }
    }

    private fun updateGpsCheckUI(status: GpsStatus) {
        val b = when (status) {
            is GpsStatus.Enabled -> {
                viewModel.getCheckPaymentStatus()
                editData?.let { viewModel.update(it) }
                true
            }
            is GpsStatus.Disabled -> {
                storage.freeState = false
                makeWorkerUnFree()
                changeButtonProblems()
                false
            }
        }
        viewModel.setGpsStatus(b)
    }

    private fun makeWorkerFree() {
        (requireActivity() as MainActivity).viewModel.setWorkerFree()
    }

    private fun makeWorkerUnFree() {
        (requireActivity() as MainActivity).viewModel.setWorkerUnFree()
    }

    private val freeStateObserver = Observer<Boolean> {
        viewModel.updateFree(it)
        binding.apply {
            outerLayout.changeFreeButtonState(it, imageCheck, imageArrow)
            textFreeUnfree.changeFreeTextState(it)

            binding.textFreeUnfree.text =
                when {
                    it -> getString(R.string.free)
                    viewModel.getGpsStatus() -> getString(R.string.unfree)
                    else -> getString(R.string.problems)
                }
        }
    }

    private val closeDialogObserver = Observer<Unit> {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }

    private val socketObserver = EventObserver<SocketResponseData?> { data ->
        if (data != null && dialog == null) {
            if (storage.callCount > 0)
                (requireActivity() as MainActivity).viewModel.minusOpportunity(OpportunityEnum.CALL)
            showNotificationDialog(data)
        }
    }

    private val getCheckPaymentStatus = Observer<CheckPaymentStatusData?> { statusData ->
        paymentStatusData = statusData
        if (!statusData.status) {
            changeButtonProblems()
        }
    }

    private val initEdit = Observer<WorkerResponseData?> { worker ->
        storage.isActive = worker.passport?.isActive ?: false
        storage.freeState = worker.isFree

        worker.apply {
            storage.paymentId = payment_id
            val j = arrayListOf<String>()
            jobs?.forEach {
                j.add(it._id)
            }
            editData = EditWorkerHomeRequestData(
                avatar = avatar,
                location = location,
            )
        }
    }

    private val getWorker = Observer<WorkerResponseData?> { worker ->
        val payments = worker!!.payments ?: emptyList()

        if (payments.isNotEmpty()) {
            storage.lastCreatedPayment = payments.last().createdAt ?: 0
        }
        storage.isActive = worker.passport?.isActive ?: false
        storage.freeState = worker.isFree

        if (worker.passport?.isActive == true
            && !payments.isNullOrEmpty()
            && paymentStatusData?.status == true
            && viewModel.getGpsStatus()
        ) {
            if (worker.isFree) {
//                makeWorkerFree()
                binding.textFreeUnfree.text = getString(R.string.free)
            } else {
//                makeWorkerUnFree()
                binding.textFreeUnfree.text = getString(R.string.unfree)
            }
        } else {
            storage.freeState = false
            changeButtonProblems()
        }

        worker.apply {
            storage.paymentId = payment_id
            val j = arrayListOf<String>()
            jobs?.forEach {
                j.add(it._id)
            }
            editData = EditWorkerHomeRequestData(
                avatar = avatar,
                location = location
            )

            getLocation { success, point ->
                if (success && point != null) {
                    editData?.location = LocationRequest("Point", listOf(point.latitude, point.longitude))
                    editData?.let { it1 ->
                        viewModel.update(it1)
                    }
                    updateLocalLocation(point)
                }
            }
        }
    }

    private fun changeButtonProblems() {
        binding.apply {
            outerLayout.changeFreeButtonState(false, imageCheck, imageArrow)
            textFreeUnfree.changeFreeTextState(false)
        }
        binding.textFreeUnfree.text = getString(R.string.yes_problems)
    }

    private fun updateLocalLocation(location: Point) {
        storage.apply {
            currentLat = location.latitude
            currentLong = location.longitude
        }
    }

    private val updateWorkerObserver = Observer<AuthResponseData?> {
        navigateToCurrent()
    }

    private val postOrderObserver = Observer<FastOrderResponseData> {
        viewModel.getOrders()
    }

    private val patchOrderObserver = Observer<FastOrderResponseData> {
        if ((it.isFinish == true)) {
            storage.orderType = true
            binding.apply {
                layoutCurrentOrder.apply {
                    layoutOrder.hide()
                }
            }
        }
    }

    private val ordersObserver = Observer<List<GetOrdersResponse>> { ls ->
        if (ls.isNotEmpty() && (ls.last().isFinish == false)) {
            storage.orderType = false
            val d = ls.last()
            binding.layoutCurrentOrder.apply {
                layoutOrder.show()
                layoutOrder.setOnClickListener {
                    val dialog = OrderInfoDialog(true)
                    dialog.groupData = d
                    dialog.clickEnd {
                        viewModel.patchOrder(d)
                    }
                    dialog.clickCancel {
                        viewModel.patchOrder(d)
                    }
                    dialog.clickNavigate {
                        val intent = Intent(requireContext(), NavigateActivity::class.java)
                        intent.putExtra("data", it)
                        intent.putExtra("avatar", editData?.avatar ?: "")
                        startActivity(intent)
                    }
                    dialog.show(childFragmentManager, "add")
                }
                this.textClientName.text = d.clientId?.name ?: ""
                this.textCost.text = d.price?.toString() ?: ""
                this.textJobDesc.text = d.desc ?: ""
                this.btnSave.setOnClickListener {
                    d.clientId?.phone?.let { it1 -> callToWorker(it1) }
                }
                this.buttonMore.setOnClickListener { layoutOrder.performClick() }

                submitUsersPoint(
                    Point(
                        d.location?.coordinates?.get(0) ?: 0.0,
                        d.location?.coordinates?.get(1) ?: 0.0
                    )//add photo
                )
            }
        } else {
            binding.layoutCurrentOrder.layoutOrder.hide()
            storage.orderType = true
        }
    }

    private fun callToWorker(phone: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.fromParts("tel", phone, null)
        startActivity(callIntent)
    }

    private fun showNotificationDialog(data: SocketResponseData) {

        val mp = MediaPlayer.create(requireContext(), R.raw.notification)
        mp.start()

        dialog = CallNotificationDialog(requireActivity(), data, storage, format = format)
        dialog?.apply {
            setOnGetJobListener {
                (requireActivity() as MainActivity).viewModel.getJob(data.clientId)
                makeWorkerUnFree()
                viewModel.postOrder(data)
                mp?.stop()
            }
            setCancelListener {
                mp?.stop()
                try {
                    (requireActivity() as MainActivity).viewModel.cancelJob(data.clientId)
                } catch (e: Exception) {

                }
            }
            setOnDismissListener {
                mp?.stop()
                if (isAdded) {
                    (requireActivity() as MainActivity).apply {
                        dialog = null
                        viewModel.deleteValue()
                    }
                }
            }
            window!!.setGravity(Gravity.TOP)
            show()
        }
    }

    private fun navigateToCurrent() {
        if (storage.currentLat != 0.0) {
            val point = Point(storage.currentLat, storage.currentLong)
            addRedPlaceMark(binding.map, point, editData?.avatar ?: "")
            binding.map.navigateToPoint(point)
        } else {
            Log.d("CHECK_LAT", "navigateToCurrent false")
        }
    }

    private fun submitUsersPoint(point: Point, imgPath: String = "") {
        addWorkerPlaceMark(binding.map, point, imgPath)
        binding.map.navigateToPoint(point)
    }

    private fun moveMapTo(point: Point, zoom: Float) = binding.map.map.move(
        CameraPosition(point, zoom, 0.0f, 0.0f)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var showPermissionDialog: AlertDialog? = null

    override fun onResume() {
        super.onResume()
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canDrawOverlayViews(requireContext())) {
                if (showPermissionDialog?.isShowing == true) return
                showPermissionDialog = showAlertDialog(getString(R.string.please_confirm_permission), getString(R.string.pop_up_dialog)) {
                    if ("xiaomi" == Build.MANUFACTURER.toLowerCase(Locale.ROOT)) {
                        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                        intent.setClassName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.permissions.PermissionsEditorActivity"
                        )
                        intent.putExtra("extra_pkgname", context?.packageName)
                        startActivity(intent)
                    } else {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context?.packageName))
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun canDrawOverlayViews(con: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(con)
            } else {
                return true
            }
        } catch (e: NoSuchMethodError) {
            canDrawOverlaysUsingReflection(con)
        }
    }


    private fun canDrawOverlaysUsingReflection(context: Context): Boolean {
        return try {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val clazz: Class<*> = AppOpsManager::class.java
            val dispatchMethod: Method = clazz.getMethod(
                "checkOp", *arrayOf<Class<*>?>(
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
            )
            //AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            val mode =
                dispatchMethod.invoke(manager, arrayOf<Any>(24, Binder.getCallingUid(), context.getApplicationContext().getPackageName())) as Int
            AppOpsManager.MODE_ALLOWED == mode
        } catch (e: java.lang.Exception) {
            false
        }
    }

    override fun onStop() {
        binding.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        binding.map.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }
}