package com.ita.poppop.view.empty.search

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentHomeSearchBinding
import com.ita.poppop.model.empty.search.SearchMode
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.search.holder.HomeSearchAdapter
import com.ita.poppop.view.empty.search.holder.HomeSearchItemDecoration
import com.ita.poppop.view.empty.story.StoryFragmentArgs
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SearchFragment: BaseFragment<FragmentHomeSearchBinding>(R.layout.fragment_home_search) {
    private lateinit var mainViewModel: MainViewModel
    private val args: SearchFragmentArgs by navArgs()
    private val repository: PopupsRepository = PopupsRepositoryImpl(RetrofitClient.popupApi)
    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true
    private var currentQuery = "팝업"
    private lateinit var mode : SearchMode
    private val trendList = mutableListOf<SearchData>()
    private lateinit var adapter: HomeSearchAdapter


    override fun initView() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mode = args.searchMode
        setupWindowInsets()
        setupToolbar()
        setInitView()
        setupSearchInput()
        setSearchFocus()
    }

    private fun setInitView() {
        lifecycleScope.launch {
            loadSearchData(currentQuery, currentPage)
        }
    }

    private suspend fun loadSearchData(query: String, page: Int) {
        if (isLoading || !hasMoreData) return

        isLoading = true

        Log.d("checkSearch", "다음 페이지 로드중 ....")

        try {
            val result = withContext(Dispatchers.IO) {
                repository.getSearchPopups(query, page, 10)
            }

            if (result.isSuccessful) {
                val newData = result.body()?.data ?: emptyList()
                Log.d("checkSearch", "newData : ${newData}")
                if (newData.isNotEmpty()) {
                    if (page == 1) {
                        trendList.clear()
                        trendList.addAll(newData)
                        setupSearchRecyclerView(trendList)
                    } else {
                        val startPosition = trendList.size
                        trendList.addAll(newData)
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

    private fun setupSearchInput() {
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val inputText = binding.etSearch.text.toString()

                Log.d("checkSearch", "1. Toast 이후 실행됨")

                // 새로운 검색 시작
                currentQuery = inputText
                currentPage = 1
                hasMoreData = true


                Log.d("checkSearch", "2. 변수 설정 완료")

                try {
                    Log.d("checkSearch", "3. 코루틴 시작 전")
                    lifecycleScope.launch {
                        Log.d("checkSearch", "4. 코루틴 내부 진입")
                        Log.d("checkSearch", "다음 페이지 로드중....")
                        loadSearchData(currentQuery, currentPage)
                    }
                    Log.d("checkSearch", "5. 코루틴 시작 완료")
                } catch (e: Exception) {
                    Log.e("checkSearch", "코루틴 시작 중 예외: ${e.message}")
                }

                true
            } else {
                false
            }
        }
    }

    private fun setSearchFocus() {
        binding.etSearch.requestFocus()

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupToolbar() {
        binding.mtHomeSearch.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }


    private fun setupSearchRecyclerView(searchResults: List<SearchData>) {
        adapter = HomeSearchAdapter(
            onAddClick = { item ->
                mainViewModel.setSelectItem(item)
                when(mode){
                    SearchMode.RETURN_TO_DETAIL -> {
                        val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                        val action = SearchFragmentDirections.actionHomeSearchFragmentToNaviInfo(item.id)
                        parentNavController.navigate(action)
                    }
                    SearchMode.RETURN_TO_UPLOAD -> {handleBackNavigation()}
                }
        },searchResults)

        with(binding.rvHomeSearchResult) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = this@SearchFragment.adapter
            addItemDecoration(HomeSearchItemDecoration())

            val itemTouchHelper = ItemTouchHelper(SwipeHelper())
            itemTouchHelper.attachToRecyclerView(this)

            // 스크롤 리스너 추가
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // 현재 보이는 아이템이 전체 아이템의 80% 지점에 도달하면 다음 페이지 로드
                    if (!isLoading && hasMoreData) {
                        val threshold = (totalItemCount * 0.8).toInt()
                        if (firstVisibleItemPosition + visibleItemCount >= threshold) {
                            lifecycleScope.launch {
                                loadSearchData(currentQuery, currentPage)
                            }
                        }
                    }
                }
            })
        }
    }
}