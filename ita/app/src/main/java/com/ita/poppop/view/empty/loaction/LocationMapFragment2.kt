package com.ita.poppop.view.empty.loaction

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentLocationMapBinding
import com.ita.poppop.databinding.ItemMapCustomMarkerBinding
import com.ita.poppop.view.main.map.MapRVAdapter
import com.ita.poppop.view.main.map.MapRVItem
import com.ita.poppop.view.main.map.MapViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class LocationMapFragment2: BaseFragment<FragmentLocationMapBinding>(R.layout.fragment_location_map), OnMapReadyCallback {
    private val args: LocationMapFragmentArgs by navArgs()

    // Constants
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val MIN_ZOOM_LEVEL_FOR_MARKERS = 14.5
        private const val CAMERA_ZOOM_LEVEL = 15.0
        private const val MARKER_WIDTH_DP = 48
        private const val MARKER_HEIGHT_DP = 63
        private const val BOTTOM_SHEET_TOP_MARGIN_DP = 55
        private const val BOTTOM_SHEET_BOTTOM_MARGIN_DP = 36
        private const val BOTTOM_SHEET_HALF_EXPANDED_RATIO = 0.5f
        private const val SLIDE_THRESHOLD_EXPANDED = 0.6f
        private const val SLIDE_THRESHOLD_HALF_MIN = 0.25f
        private const val SLIDE_THRESHOLD_HALF_MAX = 0.75f
        private const val TAG = "LocationMapFragment"
    }

    // Properties
    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val mapRVAdapter by lazy { MapRVAdapter() }
    private val markers = mutableListOf<Marker>()
    private var isInitialCameraState = true
    private var targetLatLng: LatLng? = null
    // Lifecycle Methods
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            handleLocationPermissionResult()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        initializeMap()
        setupMapListeners()
        setupMapUI()
        handleExistingMapData()

    }

    fun setclick(){
        binding.ibCancel.setOnClickListener {
            Toast.makeText(requireContext(),"click",Toast.LENGTH_SHORT).show()
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            } else {
                findNavController(requireParentFragment()).popBackStack()
            }
        }
    }

    override fun initView() {
        initializeViewModel()
        setclick()
        val location = args.location
        val latitude = args.latitude.toDouble()
        val longitude = args.longitude.toDouble()
        binding.tvMapLocation.text = location+ "역"
        targetLatLng = LatLng(longitude, latitude)

        Toast.makeText(requireContext(),"위치는 $latitude,$longitude",Toast.LENGTH_SHORT).show()
        setupWindowInsets()

        setupRecyclerView()
        setupLocationSource()
        setupMapFragment()
        setupBottomSheet()
        observeMapData()

    }

    // Initialization Methods
    private fun initializeViewModel() {
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.rvMap.apply {
            linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            layoutManager = linearLayoutManager
            adapter = mapRVAdapter
        }
    }

    private fun setupLocationSource() {
        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as com.naver.maps.map.MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeMapData() {
        targetLatLng?.let {
            mapViewModel.getLocationPopup(it.longitude, it.latitude)
        }
        mapViewModel.mapList.observe(viewLifecycleOwner) { response ->
            mapRVAdapter.submitList(response)
            if (::naverMap.isInitialized) {
                Log.d(TAG, "맵 초기화 후 호출")
                addCustomMarkers(response)
            }
        }
    }

    // Map Initialization Methods
    private fun initializeMap() {
        naverMap.locationSource = locationSource
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
        naverMap.isIndoorEnabled = true
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()
    }

    private fun setupMapListeners() {
        setupCameraIdleListener()
        setupCameraChangeListener()
        setupLocationTrackingButton()
    }

    private fun setupCameraIdleListener() {
        naverMap.addOnCameraIdleListener {
            if (isInitialCameraState) {
                isInitialCameraState = false
            } else {
                showRebrowseButton()
            }
        }

        binding.acbRebrowse.setOnClickListener {
            it.visibility = View.GONE
        }
    }

    private fun setupCameraChangeListener() {
        naverMap.addOnCameraChangeListener { _, _ ->
            val currentZoom = naverMap.cameraPosition.zoom
            val shouldShowMarkers = currentZoom >= MIN_ZOOM_LEVEL_FOR_MARKERS
            toggleMarkersVisibility(shouldShowMarkers)
        }
    }

    private fun setupLocationTrackingButton() {
        binding.fabSearchTracking.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun setupMapUI() {
        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isCompassEnabled = false
            isScaleBarEnabled = false
            isLocationButtonEnabled = false
            isLogoClickEnabled = false
        }
    }

    private fun handleExistingMapData() {
        mapViewModel.mapList.value?.let { items ->
            Log.d(TAG, "기존 list 호출")
            addCustomMarkers(items)
        }
    }

    // Bottom Sheet Methods
    private fun setupBottomSheet() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                configureBottomSheet()
            }
        })
    }

    private fun configureBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.nsvBottomSheet)
        val topMarginPx = dpToPx(BOTTOM_SHEET_TOP_MARGIN_DP)
        val bottomMarginPx = dpToPx(BOTTOM_SHEET_BOTTOM_MARGIN_DP)

        bottomSheetBehavior.apply {
            peekHeight = bottomMarginPx
            isFitToContents = false
            halfExpandedRatio = BOTTOM_SHEET_HALF_EXPANDED_RATIO
            expandedOffset = topMarginPx
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
            saveFlags = BottomSheetBehavior.SAVE_ALL
        }

        setupBottomSheetCallback(bottomSheetBehavior)
    }

    private fun setupBottomSheetCallback(bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>) {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleBottomSheetStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                handleBottomSheetSlide(slideOffset, bottomSheetBehavior)
            }
        })
    }

    private fun handleBottomSheetStateChange(newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                Log.d("BottomSheet", "상태: 접힘(COLLAPSED)")
                scrollToTop()
            }
            BottomSheetBehavior.STATE_EXPANDED -> {
                Log.d("BottomSheet", "상태: 확장(EXPANDED)")
            }
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                Log.d("BottomSheet", "상태: 반접힘(HALF_EXPANDED)")
                scrollToTop()
            }
        }
    }

    private fun handleBottomSheetSlide(slideOffset: Float, bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>) {
        when {
            slideOffset > SLIDE_THRESHOLD_EXPANDED &&
                    bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            slideOffset in SLIDE_THRESHOLD_HALF_MIN..SLIDE_THRESHOLD_HALF_MAX &&
                    bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
            slideOffset < SLIDE_THRESHOLD_HALF_MIN &&
                    bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun scrollToTop() {
        binding.nsvSs.scrollTo(0, 0)
    }

    // Location and Permission Methods
    private fun handleLocationPermissionResult() {
        if (!locationSource.isActivated) {
            naverMap.locationTrackingMode = LocationTrackingMode.None
        }
    }

    private fun checkLocationPermission() {
        if (hasLocationPermission()) {
            enableLocationTracking()
            moveCameraToCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun enableLocationTracking() {
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    private fun moveCameraToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    Log.d(TAG, "현재 latLng: $latLng")
                    Log.d(TAG, "현재 targetLatLng: $targetLatLng")
                    if(targetLatLng == null){
                        val cameraUpdate = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)
                    }else{
                        val cameraUpdate = CameraUpdate.scrollTo(targetLatLng!!).animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)
                    }


                } ?: Log.w(TAG, "현재 위치 정보 없음")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "현재 위치 가져오기 실패: ${exception.message}")
            }
    }

    // Geocoding Methods
    private fun getGeocodeFromAddress(address: String): Address {
        val geocoder = Geocoder(requireContext())
        val addresses = geocoder.getFromLocationName(address, 1)

        return if (!addresses.isNullOrEmpty()) {
            val targetAddress = addresses[0]
            Log.d(TAG, "위경도로 변환: ${targetAddress.latitude}, ${targetAddress.longitude}")
            targetAddress
        } else {
            throw IllegalArgumentException("위경도 변환 실패: $address")
        }
    }

    // Marker Methods
    private fun addCustomMarkers(items: List<MapRVItem>) {
        Log.d(TAG, "마커 개수: ${items.size}")
        clearExistingMarkers()

        items.forEach { item ->
            Log.d(TAG, "마커 추가: ${item.itemId} (${item.lat}, ${item.lng})")
            loadMarkerImage(item)
        }
    }

    private fun clearExistingMarkers() {
        markers.forEach { it.map = null }
        markers.clear()
    }

    private fun loadMarkerImage(item: MapRVItem) {
        Glide.with(this)
            .asBitmap()
            .load(item.imageUrl)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .centerCrop()
            .circleCrop()
            .into(createMarkerImageTarget(item))
    }

    private fun createMarkerImageTarget(item: MapRVItem): CustomTarget<Bitmap> {
        return object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val customMarkerView = createCustomMarkerView(resource)
                val markerBitmap = createBitmap(customMarkerView)
                val marker = createMarker(item, markerBitmap)
                markers.add(marker)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        }
    }

    private fun createCustomMarkerView(bitmap: Bitmap): View {
        val binding = ItemMapCustomMarkerBinding.inflate(LayoutInflater.from(context))
        binding.ivMapCustomMarker.setImageBitmap(bitmap)
        return binding.root
    }

    private fun createMarker(item: MapRVItem, bitmap: Bitmap): Marker {
        return Marker().apply {
            position = LatLng(item.lat, item.lng)
            icon = OverlayImage.fromBitmap(bitmap)
            width = dpToPx(MARKER_WIDTH_DP)
            height = dpToPx(MARKER_HEIGHT_DP)
            map = naverMap
        }
    }

    private fun createBitmap(view: View): Bitmap {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(dpToPx(MARKER_WIDTH_DP), View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(dpToPx(MARKER_HEIGHT_DP), View.MeasureSpec.EXACTLY)

        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    // UI Helper Methods
    private fun showRebrowseButton() {
        binding.acbRebrowse.visibility = View.VISIBLE
    }

    private fun toggleMarkersVisibility(shouldShow: Boolean) {
        markers.forEach { it.isVisible = shouldShow }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}