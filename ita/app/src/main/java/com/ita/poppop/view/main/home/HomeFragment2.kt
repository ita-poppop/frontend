package com.ita.poppop.view.main.home

import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
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
import com.ita.poppop.data.remote.api.StoriesApi
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
import com.ita.poppop.view.empty.search.SearchFragmentDirections
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HomeFragment2 : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var dimManager: DimManager
    private lateinit var mainViewModel: MainViewModel
    val mainAViewModel: MainAViewModel by activityViewModels()

    private val repository: PopupsRepository = PopupsRepositoryImpl(RetrofitClient.popupApi)
    private val repository2: StoriesRepository = StoriesRepositoryImpl(RetrofitClient.storiesApi)


    override fun initView() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        dimManager = DimManager(requireActivity().window)

        setupBackPress()
        setupAnimations()
        setupDimFab()

        setupTrendRecycler()
        setupDirectionRecycler()
        setupUpcomingRecycler()
        setupWaitingRecycler()

        setupClickListeners()

    }
    private fun setupAnimations() {
        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
    }

    private fun setupDimFab() = with(binding) {
        dimManager.addFabButtons(listOf(fab1, fab2))
        dimManager.addFabText(listOf(txFab1, txFab2))
        dimManager.bindToggleButton(ibFab)

        ibFab.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) dimManager.showDim()
            else dimManager.hideDim()
        }
    }

    private fun setupTrendRecycler() = with(binding.rvTrend) {
        var trendList = listOf<TrendData>()
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getTrendPopups(1, 10)
                }

                if (result.isSuccessful) {
                    trendList = result.body()?.data!!
                    Log.d("checkDatata","trendList : ${trendList}")
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
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                showToast("TrendHTTP 에러: ${e.code()} - ${e.message()}")
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                showToast("Trend에러 발생: ${e.localizedMessage ?: "알 수 없는 오류"}")
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    private fun setupWaitingRecycler() = with(binding.rvWaiting) {

        var waitingList = listOf<StoryData>()
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository2.getStories(mainAViewModel.tokenPair.value.first.toString(),1, 10)
                }

                if (result.isSuccessful) {
                    waitingList = result.body()?.data!!
                    waitingList = waitingList.sortedBy { it.isRead }
                    Log.d("checkDatas", "waitingList: \n${waitingList.joinToString(separator = "\n") {
                        "popupTitle: ${it.popupTitle}, isRead: ${it.isRead}"
                    }}")

                    mainViewModel.setStoryList(waitingList)

                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                showToast("WaitingHTTP 에러: ${e.code()} - ${e.message()}")
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                showToast("Waiting에러 발생: ${e.localizedMessage ?: "알 수 없는 오류"}")
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }

            adapter = HomeWaitingAdapter(
                onclick = { position ->
                    navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeStory(position))
                },mainViewModel.storyList.value)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HomeWaitingItemDecoration(context,waitingList))
        }

    }

    private fun setupUpcomingRecycler() = with(binding.rvUpcoming) {
        var upcomingList = listOf<PlannedData>()
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getPlannedPopups(1, 4)
                }

                if (result.isSuccessful) {
                    upcomingList = result.body()?.data!!
                    Log.d("checkDatata","upcomingList : ${upcomingList}")


                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                showToast("UpcomingHTTP 에러: ${e.code()} - ${e.message()}")
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                showToast("Upcoming에러 발생: ${e.localizedMessage ?: "알 수 없는 오류"}")
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
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


    }

    private fun setupClickListeners() = with(binding) {

        clSearchArea.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeSearch(SearchMode.RETURN_TO_DETAIL))
        }
        ibNotification.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviHomeNotification())
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

    override fun onResume() {
        super.onResume()
        setupWaitingRecycler()
    }
}


