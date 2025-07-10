package com.ita.poppop.view.empty.myreview

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.profile.ReviewItem
import com.ita.poppop.data.remote.repository.profile.ProfileRepository
import com.ita.poppop.data.remote.repository.profile.ProfileRepositoryImpl
import com.ita.poppop.databinding.FragmentMyReviewBinding
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.myreview.holder.MyReviewAdapter
import com.ita.poppop.view.empty.myreview.holder.MyReviewItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class MyReviewFragment : BaseFragment<FragmentMyReviewBinding>(
    R.layout.fragment_my_review
){
    private val repository: ProfileRepository = ProfileRepositoryImpl(RetrofitClient.profileApi)
    val mainAViewModel: MainAViewModel by activityViewModels()

    override fun initView() {
        setupWindowInsets()
        setupToolbar()
        setipPorfile()
    }
    private fun setipPorfile() {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getProfile(mainAViewModel.tokenPair.value.first.toString(),1,10)
                }

                if (result.isSuccessful) {
                    Log.d("checkUserEx","user : ${result.body()}")
                    setupMyReviewRecycler(result.body()?.data?.reviews!!)
                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
    }
    private fun setupMyReviewRecycler(list: List<ReviewItem>) = with(binding.rvMyReview) {
        adapter = MyReviewAdapter(list)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        addItemDecoration(MyReviewItemDecoration(context,list))
    }


    private fun setupToolbar() {
        binding.mtMyReview.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }










}