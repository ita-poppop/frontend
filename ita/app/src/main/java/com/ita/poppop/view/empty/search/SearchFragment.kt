package com.ita.poppop.view.empty.search

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.data.remote.dto.popups.TrendData
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentHomeSearchBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.search.holder.HomeSearchAdapter
import com.ita.poppop.view.empty.search.holder.HomeSearchItemDecoration
import com.ita.poppop.view.main.home.trend.HomeTrendAdapter
import com.ita.poppop.view.main.home.trend.HomeTrendItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SearchFragment: BaseFragment<FragmentHomeSearchBinding>(R.layout.fragment_home_search) {

    private val repository: PopupsRepository = PopupsRepositoryImpl(RetrofitClient.popupApi)

    override fun initView() {
        setupWindowInsets()
        setupToolbar()
//        setupSearchRecyclerView()
        setupSearchInput()
        setSearchFocus()

    }
    private fun setupSearchInput() {
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val inputText = binding.etSearch.text.toString()
                Toast.makeText(requireContext(), "inputText : ${inputText}, keyCode : ${keyCode}", Toast.LENGTH_SHORT).show()
                var trendList = listOf<SearchData>()
                lifecycleScope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            repository.getSearchPopups(inputText,1, 40)
                        }

                        if (result.isSuccessful) {
                            trendList = result.body()?.data!!


                            setupSearchRecyclerView(trendList)
                        }
                    } catch (e: HttpException) {
                        // HTTP 에러 상세 정보
//                        showToast("HTTP 에러: ${e.code()} - ${e.message()}")
                        Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                    } catch (e: Exception) {
//                        showToast("에러 발생: ${e.localizedMessage ?: "알 수 없는 오류"}")
                        Log.e("API_ERROR", "Exception: ${e.message}", e)
                    }
                }


                true // 이벤트 소비
            } else {
                false // 다른 키는 넘김
            }
        }
    }

    private fun setSearchFocus(){
        binding.etSearch.requestFocus()

        // 키보드 자동 표시
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupToolbar() {
        binding.mtHomeSearch.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }

    private fun setupSearchRecyclerView(searchResults : List<SearchData>){
        val adapter = HomeSearchAdapter(searchResults)

        with(binding.rvHomeSearchResult) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
            addItemDecoration(HomeSearchItemDecoration())

            val itemTouchHelper = ItemTouchHelper(SwipeHelper())
            itemTouchHelper.attachToRecyclerView(this)
        }
    }
}