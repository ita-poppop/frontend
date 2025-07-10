package com.ita.poppop.view.empty.upcoming

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.popups.PlannedData
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentUpcomingBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.search.holder.HomeSearchAdapter
import com.ita.poppop.view.empty.search.holder.HomeSearchItemDecoration
import com.ita.poppop.view.empty.upcoming.holder.UpcomingAdapter
import com.ita.poppop.view.empty.upcoming.holder.UpcomingItemDecoration
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.home.upcoming.HomeUpcomingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class UpcomingFragment: BaseFragment<FragmentUpcomingBinding>(R.layout.fragment_upcoming) {
    private val repository: PopupsRepository = PopupsRepositoryImpl(RetrofitClient.popupApi)
    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true
    private val upcomingList = mutableListOf<PlannedData>()
    private lateinit var adapter: UpcomingAdapter
    override fun initView() {

        setupWindowInsets()
        setupToolbar()
        setInitView()


    }

    private fun setInitView() {
        lifecycleScope.launch {
            loadUpcomingData(currentPage)
        }
    }
    private fun setupToolbar() {
        binding.mtHomeSearch.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }
    private suspend fun loadUpcomingData(page: Int) {
        if (isLoading || !hasMoreData) return

        isLoading = true
        Log.d("checkUpcoming", "다음 페이지 로드중 ....page : ${page}")
        try {
            val result = withContext(Dispatchers.IO) {
                repository.getPlannedPopups(page, 10)
            }

            if (result.isSuccessful) {
                val newData = result.body()?.data ?: emptyList()

                if (newData.isNotEmpty()) {
                    if (page == 1) {
                        upcomingList.clear()
                        upcomingList.addAll(newData)
                        setupSearchRecyclerView(upcomingList)
                    } else {
                        val startPosition = upcomingList.size
                        upcomingList.addAll(newData)
                        adapter.notifyItemRangeInserted(startPosition, newData.size)
                    }
                    currentPage++

                    // 받은 데이터가 요청한 크기보다 작으면 더 이상 데이터가 없음을 의미
                    if (newData.size < 10) {
                        hasMoreData = false
                    }
                } else {
                    hasMoreData = false
                }
            }
        } catch (e: HttpException) {
            Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    private fun setupSearchRecyclerView(upcomingList: List<PlannedData>) {
        adapter = UpcomingAdapter(
            onClick = {itemId ->
                val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                val action = MainFragmentDirections.actionMainFragmentToNaviInfo(itemId)
                parentNavController.navigate(action)
            },upcomingList
        )

        with(binding.rvUpcoming) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            this.adapter = this@UpcomingFragment.adapter
            addItemDecoration(UpcomingItemDecoration())

            val itemTouchHelper = ItemTouchHelper(SwipeHelper())
            itemTouchHelper.attachToRecyclerView(this)

            // 스크롤 리스너 추가
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // 현재 보이는 아이템이 전체 아이템의 80% 지점에 도달하면 다음 페이지 로드
                    if (!isLoading && hasMoreData) {
                        val threshold = (totalItemCount * 0.8).toInt()
                        if (firstVisibleItemPosition + visibleItemCount >= threshold) {
                            lifecycleScope.launch {
                                loadUpcomingData(currentPage)
                            }
                        }
                    }
                }
            })
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}