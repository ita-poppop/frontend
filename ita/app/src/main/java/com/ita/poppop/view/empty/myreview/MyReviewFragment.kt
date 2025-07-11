package com.ita.poppop.view.empty.myreview

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.profile.ReviewItem
import com.ita.poppop.data.remote.repository.profile.ProfileRepository
import com.ita.poppop.data.remote.repository.profile.ProfileRepositoryImpl
import com.ita.poppop.databinding.FragmentMyReviewBinding
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.myreview.holder.MyReviewAdapter
import com.ita.poppop.view.empty.myreview.holder.MyReviewItemDecoration
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.main.UserProfile
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException


class MyReviewFragment : BaseFragment<FragmentMyReviewBinding>(R.layout.fragment_my_review){
    private val repository: ProfileRepository = ProfileRepositoryImpl(RetrofitClient.profileApi)
    val mainAViewModel: MainAViewModel by activityViewModels()

    override fun initView() {
        setupWindowInsets()

        setupToolbar()

        loadData()
    }
    private fun loadData() {
        lifecycleScope.launch {
            try {
                showSampleData(isLoading = true)

                // 1. 병렬 요청 시작
                val profileDeferred = async {
                    try {
                        repository.getProfile(mainAViewModel.tokenPair.value.first.toString(),1,10)
                    } catch (e: Exception) {
                        Log.e("Profile", "Reviews 에러: ${e.message}")
                        null
                    }
                }

                // 2. 두 요청의 결과를 기다림 (await)
                val profileResult = profileDeferred.await()
                val reviewsList = profileResult?.body()?.data?.reviews ?: emptyList()

                // 3. 요청 모두 완료된 상태
                setupMyReviewRecycler(reviewsList)

                showSampleData(isLoading = false)

            } catch (e: HttpException) {
                // 예외가 catch 안에서 잡히지 않은 경우 여기에 걸림
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                // 기타 예외
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    private fun setupMyReviewRecycler(reviewsList: List<ReviewItem>) = with(binding.rvMyReview) {
        adapter = MyReviewAdapter(
            onClick = {itemId ->
                val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                val action = MyReviewFragmentDirections.actionMyReviewFragmentToNaviInfo(itemId)
                parentNavController.navigate(action)
            },reviewsList
        )
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        addItemDecoration(MyReviewItemDecoration(context,reviewsList))
    }


    private fun setupToolbar() {
        binding.mtMyReview.apply {
            setNavigationIcon(R.drawable.chevron_left)
            setNavigationOnClickListener { handleBackNavigation() }
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflMyReview.startShimmer()
            binding.sflMyReview.visibility = View.VISIBLE
            binding.rvMyReview.visibility = View.GONE
        } else {
            binding.sflMyReview.stopShimmer()
            binding.sflMyReview.visibility = View.GONE
            binding.rvMyReview.visibility = View.VISIBLE
        }
    }








}