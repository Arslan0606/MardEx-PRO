package uz.star.mardexworker.ui.screen.navigate_activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.ActivityNavigateBinding
import uz.star.mardexworker.model.response.GetOrdersResponse
import uz.star.mardexworker.utils.extensions.addRedPlaceMark
import uz.star.mardexworker.utils.extensions.addWorkerPlaceMark
import uz.star.mardexworker.utils.extensions.navigateToPoint
import javax.inject.Inject

@AndroidEntryPoint
class NavigateActivity : AppCompatActivity(), DrivingSession.DrivingRouteListener {
    private var _binding: ActivityNavigateBinding? = null
    private val binding: ActivityNavigateBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    @Inject
    lateinit var storage: LocalStorage

    lateinit var data: GetOrdersResponse
    lateinit var avatar: String

    private val mapObjects: MapObjectCollection by lazy { binding.map.map.mapObjects.addCollection() }

    private var drivingSession: DrivingSession? = null
    private val drivingRouter: DrivingRouter by lazy {
        DirectionsFactory.getInstance().createDrivingRouter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNavigateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DirectionsFactory.initialize(this)
        MapKitFactory.initialize(this)

        intent.getSerializableExtra("data")?.let {
            try {
                data = it as GetOrdersResponse
            } catch (e: Exception) {

            }
        }

        intent.getStringExtra("avatar")?.let {
            try {
                avatar = it
            } catch (e: Exception) {

            }
        }

        loadViews()
    }

    private fun loadViews() {
        binding.apply {

            layoutNavigate.setOnClickListener {
                val point = Point(storage.currentLat, storage.currentLong)
                binding.map.navigateToPoint(point)
            }

            layoutBack.setOnClickListener {
                finish()
            }

            if (storage.mapState) {
                map.map.isLiteModeEnabled = true
            } else {
                map.map.isNightModeEnabled = true
            }
        }
        if (storage.currentLat != 0.0) {
            val point = Point(storage.currentLat, storage.currentLong)
            addRedPlaceMark(binding.map, point, avatar)

            submitUsersPoint(
                Point(
                    data.location?.coordinates?.get(0) ?: 0.0,
                    data.location?.coordinates?.get(1) ?: 0.0
                )
            )
        }
    }

    private fun submitUsersPoint(point: Point, imgPath: String = "") {
        addWorkerPlaceMark(binding.map, point, imgPath)
        binding.map.navigateToPoint(point)

        val drivingOptions = DrivingOptions()
        val startPoint = Point(storage.currentLat, storage.currentLong)
        val requestPoints: ArrayList<RequestPoint> = ArrayList()
        requestPoints.add(
            RequestPoint(
                startPoint,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(RequestPoint(point, RequestPointType.WAYPOINT, null))
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, this)
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        try {
            if (!routes.isNullOrEmpty()) {
                mapObjects.addPolyline(routes.first().geometry)
                /*routes.forEach {
                    mapObjects.addPolyline(it.geometry)
                }*/
            }
        } catch (e: Exception) {

        }
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = "unknown_error_message"
        if (error is RemoteError) {
            errorMessage = "remote_error_message"
        } else if (error is NetworkError) {
            errorMessage = "network_error_message"
        }
        Log.d("ERROR_MSG", "onDrivingRoutesError: $errorMessage")
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