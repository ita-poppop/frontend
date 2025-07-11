package com.ita.poppop.view.main.map

import android.Manifest
import android.app.ProgressDialog.show
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentMapBinding
import com.ita.poppop.databinding.ItemMapCustomMarkerBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.hide
import com.ita.poppop.view.main.show
import com.ita.poppop.viewmodel.MainAViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class MapFragment: BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel

    val mainViewModel: MainAViewModel by activityViewModels()

    private val mapRVAdapter by lazy {
        MapRVAdapter()
    }

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val markers = mutableListOf<Marker>()

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun initView() {
        binding.apply {
            val mapRepository = PopupsRepositoryImpl(RetrofitClient.popupApi)
            val mapFactory = ViewModelFactory { MapViewModel(mapRepository) }
            mapViewModel = ViewModelProvider(this@MapFragment, mapFactory)[MapViewModel::class.java]
            //mapViewModel = ViewModelProvider(this@MapFragment).get(MapViewModel::class.java)
            linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            // 지도 설정
            rvMap.apply {
                layoutManager = linearLayoutManager
                adapter = mapRVAdapter

                /*val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
                addItemDecoration(dividerItemDecoration)*/
            }

            mapViewModel.mapList.observe(viewLifecycleOwner, Observer { response ->
                mapRVAdapter.submitList(response)
                if (::naverMap.isInitialized) {
                    Log.d("MapFragment", "맵 초기화 후 호출")
                    emptyStateLayout.root.run { if(response.isNullOrEmpty()) show() else hide()}
                    addCustomMarkers(response)
                } else {

                }
            })

            mapRVAdapter.setMapItemClickListener(object : MapRVAdapter.MapItemClickListener{
                override fun onItemClick(position: Int) {
                    //val popupId = 1
                    val item = mapRVAdapter.currentList.getOrNull(position) ?: return
                    val popupId = item.itemId

                    val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                    val action = MainFragmentDirections.actionMainFragmentToNaviInfo(popupId)
                    parentNavController.navigate(action)
                }
            })
            
            locationSource = FusedLocationSource(
                requireActivity(),
                LOCATION_PERMISSION_REQUEST_CODE
            )

            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as com.naver.maps.map.MapFragment
            mapFragment.getMapAsync(this@MapFragment)
            
            // 바텀 시트
            setBottomSheet()

            /*ivSearchIcon.setOnClickListener {
                val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                navController.navigate(MainFragmentDirections.actionMainFragmentToNaviMap())
            }*/


            // 검색 주소 위경도 변환 후 카메라 이동
            editSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    // 엔터시 키보드 내리기
                    val imm = ContextCompat.getSystemService(v.context, InputMethodManager::class.java)
                    imm!!.hideSoftInputFromWindow(v.windowToken, 0)

                    // 맵 이동
                    val address = editSearch.text.toString()
                    try {
                        val result = getGeocodeFromAddress(address)
                        val latLng = LatLng(result.latitude, result.longitude)
                        Log.d("MapFragment", "입력된 주소의 위경도: ${latLng.latitude}, ${latLng.longitude}")

                        val cameraPosition = CameraPosition(latLng, 15.0)
                        val cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition)
                            .animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)

                        mapViewModel.getLocationPopup(latLng.longitude,latLng.latitude)

                        Log.d("MapFragment", "${address} 이동 완료")

                    } catch (e: Exception) {
                        Log.e("MapFragment", "Geocode 실패: ${e.message}")
                    }
                    return@OnKeyListener true
                }
                false
            })
        }
    }

    private fun setBottomSheet() {
        // 현재 프래그먼트의 View의 크기 정보를 가져오기 위한 ViewTreeObserver 사용
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 레이아웃이 그려진 후에 한 번만 실행되도록 리스너 제거
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // 프래그먼트 뷰의 전체 높이
                val fragmentHeight = binding.root.height

                // 바텀시트 설정
                val bottomSheetBehavior = BottomSheetBehavior.from(binding.nsvBottomSheet)

                // dp 값을 픽셀로 변환
                val topMarginPx = dpToPx(50)
                val bottomMarginPx = dpToPx(48)

                // 바텀시트 최소 높이
                val peekHeight = bottomMarginPx

                // 바텀시트 최대 높이
                val expandedOffset = topMarginPx

                // 중간 상태의 높이 (최소와 최대의 중간 값)
                val halfExpandedRatio = 0.5f

                bottomSheetBehavior.apply {
                    this.peekHeight = peekHeight
                    this.isFitToContents = false  // 중간 상태 사용
                    this.halfExpandedRatio = halfExpandedRatio
                    this.expandedOffset = expandedOffset
                    this.state = BottomSheetBehavior.STATE_COLLAPSED

                    // 중간 상태(STATE_HALF_EXPANDED) 활성화
                    this.saveFlags = BottomSheetBehavior.SAVE_ALL
                }
                
                // 슬라이드 중간 상태 사용
                bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        // 상태 변경 처리
                        when (newState) {
                            BottomSheetBehavior.STATE_COLLAPSED -> {
                                Log.d("BottomSheet", "상태: 접힘(COLLAPSED)")
                                // 접힌 상태에서 맨 위로 스크롤
                                binding.nsvSs.scrollTo(0, 0)
                            }
                            BottomSheetBehavior.STATE_EXPANDED -> {
                                Log.d("BottomSheet", "상태: 확장(EXPANDED)")
                            }
                            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                                Log.d("BottomSheet", "상태: 반접힘(HALF_EXPANDED)")
                                // 반접힌 상태에서 맨 위로 스크롤
                                binding.nsvSs.scrollTo(0, 0)
                            }
                        }

                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // 슬라이드 오프셋에 따른 상태 결정
                        when {
                            slideOffset > 0.6f && bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED -> {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                            }
                            slideOffset in 0.25f..0.75f &&
                                    bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                            }
                            slideOffset < 0.25f && bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                            }
                        }
                    }
                })
            }
        })

        // BottomSheet 동작 설정
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.nsvBottomSheet)

        // 초기상태 접힘
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    // dp -> px
    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    // 위치 권한 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (
            locationSource.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부시 위치 안 보이도록
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // 지도 로딩 후 자동 호출
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 위치 권한 확인
        checkLocationPermission()

        // 실내 지도 활성화
        naverMap.isIndoorEnabled = true

        var isInitialCameraState = true

        // 재검색 버튼
        naverMap.addOnCameraIdleListener {
            if (isInitialCameraState) {
                isInitialCameraState = false
            } else {
                binding.acbRebrowse.visibility = View.VISIBLE
            }
        }
        binding.acbRebrowse.setOnClickListener {
            it.visibility = View.GONE

            if (::naverMap.isInitialized) {
                val currentPosition = naverMap.cameraPosition.target
                val longitude = currentPosition.longitude
                val latitude = currentPosition.latitude

                mapViewModel.getLocationPopup(longitude, latitude)
            }
        }

        // naverMap 호출 후 데이터 있을시 마커 추가
        mapViewModel.mapList.value?.let { items ->
            Log.d("MapFragment", "기존 list 호출")
            addCustomMarkers(items)
        }


        // 줌 일정 레벨 이하일 경우 마커 숨기기
        naverMap.addOnCameraChangeListener { reason, animated ->
            val zoom = naverMap.cameraPosition.zoom
            val shouldShow = zoom >= 13
            markers.forEach { it.isVisible = shouldShow }
        }


        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false // +/- 버튼
        uiSettings.isCompassEnabled = false  // 나침반
        uiSettings.isScaleBarEnabled = false
        uiSettings.isLocationButtonEnabled = false // 현위치 버튼
        uiSettings.isLogoClickEnabled = false

        // 위치 권한 확인 후 위치 추적 모드 설정
        binding.fabSearchTracking.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 없으면 요청
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한 있으면 위치 추적 모드 켜기
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

            // 현재 위치로 카메라 이동
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Easing)
                    naverMap.moveCamera(cameraUpdate)
                    Log.d("MapFragment", "현재 위치: $latLng")
                    mapViewModel.getLocationPopup(location.longitude, location.latitude)
                } else {
                    Log.w("MapFragment", "현재 위치 정보 없음")
                }
            }.addOnFailureListener {
                Log.e("MapFragment", "현재 위치 가져오기 실패: ${it.message}")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    
    // 검색어 -> 위경도 변환
    fun getGeocodeFromAddress(address: String): Address {
        val coder = Geocoder(requireContext())
        val geocodedAddress = coder.getFromLocationName(address, 1)
        if (!geocodedAddress.isNullOrEmpty()) {
            val newAddress = geocodedAddress[0]
            Log.d("MapFragment", "위경도로 변환: ${newAddress.latitude}, ${newAddress.longitude}")
            return newAddress
        } else {
            //Toast.makeText(requireContext(), "잘못된 주소입니다.", Toast.LENGTH_LONG).show()
            //Snackbar.make(binding.root, "잘못된 주소입니다.", Snackbar.LENGTH_LONG).show()
            /*val binding = ToastMessageBinding.inflate(layoutInflater)
            binding.tvToastMessage.text = "${address}는 잘못된 주소입니다."

            Toast(requireContext()).apply {
                duration = Toast.LENGTH_LONG
                view = binding.root
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }*/
            throw IllegalArgumentException("위경도 변환 실패 $address")
        }
    }
    
    // 이미지 비트맵 변환
    private fun createBitmap(view: View): Bitmap {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(dpToPx(48), View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(dpToPx(63), View.MeasureSpec.EXACTLY)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    // 마커 커스텀
    private fun addCustomMarkers(items: List<MapRVItem>) {
        Log.d("MapFragment", "마커 개수: ${items.size}")
        // 기존 마커 제거 (중복)
        markers.forEach { it.map = null }
        markers.clear()

        items.forEach { item ->
            Log.d("MapFragment", "마커 추가 : ${item.itemId} (${item.lat}, ${item.lng})")
            Glide.with(this)
                .asBitmap()
                .load(item.imageUrl)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .centerCrop()
                .circleCrop()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val binding = ItemMapCustomMarkerBinding.inflate(LayoutInflater.from(context))
                        val customMarker = binding.root
                        val markerImg = binding.ivMapCustomMarker
                        markerImg.setImageBitmap(resource)

                        val bitmap = createBitmap(customMarker)

                        val marker = Marker().apply {
                            position = LatLng(item.lat, item.lng)
                            icon = OverlayImage.fromBitmap(bitmap)
                            width = dpToPx(48)
                            height = dpToPx(63)
                            map = naverMap

                            setOnClickListener {
                                // 마커 클릭 시 팝업 상세 화면으로 이동
                                val popupId = item.itemId
                                val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                                val action = MainFragmentDirections.actionMainFragmentToNaviInfo(popupId)
                                parentNavController.navigate(action)

                                true
                            }
                        }

                        markers.add(marker)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }
}