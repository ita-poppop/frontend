package com.ita.poppop.view.main.home

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.popups.PlannedData
import com.ita.poppop.data.remote.dto.popups.TrendData
import com.ita.poppop.data.remote.dto.stories.StoryData
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.data.remote.repository.stories.StoriesRepository
import com.ita.poppop.data.remote.repository.stories.StoriesRepositoryImpl
import com.ita.poppop.databinding.FragmentHomeBinding
import com.ita.poppop.model.empty.search.SearchMode
import com.ita.poppop.util.DimManager
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.home.direction.HomeDirectionAdapter
import com.ita.poppop.view.main.home.direction.HomeDirectionItemDecoration
import com.ita.poppop.view.main.home.trend.HomeTrendAdapter
import com.ita.poppop.view.main.home.trend.HomeTrendItemDecoration
import com.ita.poppop.view.main.home.upcoming.HomeUpcomingAdapter
import com.ita.poppop.view.main.home.upcoming.HomeUpcomingItemDecoration
import com.ita.poppop.view.main.home.waiting.HomeWaitingAdapter
import com.ita.poppop.view.main.home.waiting.HomeWaitingItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var dimManager: DimManager
    private lateinit var mainViewModel: MainViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()

    private val repository: PopupsRepository = PopupsRepositoryImpl(RetrofitClient.popupApi)
    private val repository2: StoriesRepository = StoriesRepositoryImpl(RetrofitClient.storiesApi)


    override fun initView() {


        setViewModel()
        setupBackPress()
        setupAnimations()
        setupDimFab()

        loadData()

        setupClickListeners()

    }

    private fun setViewModel(){
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.storyList.observe(this, Observer { list ->
//            setupWaitingRecycler(list)
        })
    }

    private fun loadData() {
        lifecycleScope.launch {
            showSampleData(isLoading = true)

            var trendList = listOf<TrendData>() // 적절한 타입으로 변경
            var waitingList = listOf<StoryData>() // 적절한 타입으로 변경
            var plannedList = listOf<PlannedData>() // 적절한 타입으로 변경

            try {
                // 타임아웃 설정으로 무한 대기 방지
                withTimeout(3000) { // 30초 타임아웃
                    // 1. 병렬 요청 시작
                    val trendDeferred = async {
                        try {
                            Log.d("Home", "Trend 요청")
                            repository.getTrendPopups((1..5).random(), 10).body()?.data
                        } catch (e: Exception) {
                            Log.e("Home", "Trend 에러: ${e.message}")
                            null
                        }
                    }

                    val waitingDeferred = async {
                        try {
                            Log.d("Home", "Waiting 요청")
                            repository2.getStories(
                                mainAViewModel.tokenPair.value.first.toString(),
                                1,
                                10
                            ).body()?.data

                        } catch (e: Exception) {
                            Log.e("Home", "Waiting 에러: ${e.message}")
                            null
                        }
                    }

                    val plannedDeferred = async {
                        try {
                            Log.d("Home", "Planned 요청")
                            repository.getPlannedPopups((1..4).random(), 4).body()?.data

                        } catch (e: Exception) {
                            Log.e("Home", "Planned 에러: ${e.message}")
                            null
                        }
                    }

                    // 2. 요청의 결과를 기다림 (await)
                    trendList = trendDeferred.await() ?: emptyList()
                    waitingList = waitingDeferred.await() ?: emptyList()
                    plannedList = plannedDeferred.await() ?: emptyList()
                }

            } catch (e: TimeoutCancellationException) {
                Log.e("API_ERROR", "요청 타임아웃")
            } catch (e: HttpException) {
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            } finally {
                // 에러 발생 여부와 상관없이 무조건 실행
                setupTrendRecycler(trendList)
                setupWaitingRecycler(waitingList)
                setupUpcomingRecycler(plannedList)
                setupDirectionRecycler()

                showSampleData(isLoading = false)
            }
        }
    }


    private fun setupAnimations() {
        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
    }


    private fun setupDimFab() = with(binding) {
        dimManager = DimManager(requireActivity().window)

        dimManager.addFabButtons(listOf(fab1, fab2))
        dimManager.addFabText(listOf(txFab1, txFab2))
        dimManager.bindToggleButton(ibFab)

        ibFab.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) dimManager.showDim()
            else dimManager.hideDim()
        }
    }

    private fun setupTrendRecycler(trendList: List<TrendData>) = with(binding.rvTrend) {
        adapter = HomeTrendAdapter(
            onClick = {itemId ->
                val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                val action = MainFragmentDirections.actionMainFragmentToNaviInfo(itemId)
                parentNavController.navigate(action)
            },trendList
        )
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(HomeTrendItemDecoration())
    }

    private fun setupWaitingRecycler(waitingList: List<StoryData>) = with(binding.rvWaiting) {
        adapter = HomeWaitingAdapter(
            onclick = { position ->
                navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeStory(position))
            },waitingList)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(HomeWaitingItemDecoration(context,waitingList))

        mainViewModel.setStoryList(waitingList)
    }

    private fun setupUpcomingRecycler(upcomingList: List<PlannedData>) = with(binding.rvUpcoming) {
        adapter = HomeUpcomingAdapter(
            onClick = {itemId ->
                val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                val action = MainFragmentDirections.actionMainFragmentToNaviInfo(itemId)
                parentNavController.navigate(action)
            },upcomingList
        )
        layoutManager = GridLayoutManager(context, 2)
        addItemDecoration(HomeUpcomingItemDecoration(context,upcomingList))
    }

    private fun setupDirectionRecycler() = with(binding.rvDirection) {
        adapter = HomeDirectionAdapter(
            onClick = { location,latitude,longitude ->
                navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeLocationMap(location,latitude,longitude))
            }
        )
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(HomeDirectionItemDecoration())
    }



    private fun setupClickListeners() = with(binding) {

        clSearchArea.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeSearch(SearchMode.RETURN_TO_DETAIL))
        }
        ibNotification.setOnClickListener {
//            navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeNotification())
        }
        ibUpcomingDetail.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeUpcoming())
        }

        fab1.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviUploadWaiting())
            dimManager.hideDim()
        }
        fab2.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviUploadReview())
            dimManager.hideDim()
        }

    }



    private fun navigateTo(action: NavDirections) {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        navController.navigate(action)
    }

    private fun setupBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (dimManager.isDimmed()) {
                        dimManager.hideDim()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflHome.startShimmer()
            binding.sflHome.visibility = View.VISIBLE
            binding.clHome.visibility = View.GONE
        } else {
            binding.sflHome.stopShimmer()
            binding.sflHome.visibility = View.GONE
            binding.clHome.visibility = View.VISIBLE
        }
    }


}

