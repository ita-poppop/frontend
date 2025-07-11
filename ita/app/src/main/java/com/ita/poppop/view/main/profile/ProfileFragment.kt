package com.ita.poppop.view.main.profile

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.profile.ReviewItem
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.data.remote.repository.profile.ProfileRepository
import com.ita.poppop.data.remote.repository.profile.ProfileRepositoryImpl
import com.ita.poppop.databinding.FragmentProfileBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.profile.holder.ProfileReviewAdapter
import com.ita.poppop.view.main.profile.holder.ProfileReviewItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import com.ita.poppop.viewmodel.main.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val repository: ProfileRepository = ProfileRepositoryImpl(RetrofitClient.profileApi)
    val mainAViewModel: MainAViewModel by activityViewModels()
    private lateinit var mainViewModel: MainViewModel
    override fun initView() {

        setViewModel()

        loadData()

        setupClickListeners()

    }
    private fun setViewModel(){
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.userData.observe(this,Observer{
            Glide.with(binding.root)
                .load(mainViewModel.userData.value?.userProfileImage)
                .centerCrop()
                .error(R.drawable._profile_load_icon)
                .into(binding.ivProfile)

            binding.tvUserName.text = mainViewModel.userData.value?.userName?.replace("\"", "")
            binding.tvUserLikesCount.text = mainViewModel.userData.value?.userLikesCount.toString()
            binding.tvUserReviewCount.text = mainViewModel.userData.value?.userReviewCount.toString()
        })
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
                val profileUser = UserProfile(
                    userName = profileResult?.body()?.data?.userName.toString(),
                    userProfileImage = profileResult?.body()?.data?.profileUrl,
                    userLikesCount = profileResult?.body()?.data?.totalLikeCount,
                    userReviewCount = profileResult?.body()?.data?.reviewCount
                )

                // 3. 요청 모두 완료된 상태
                setupReviewsRecycler(reviewsList)
                mainViewModel.setUser(profileUser)

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

    private fun setupReviewsRecycler(reviewsList:  List<ReviewItem>) = with(binding.rvProfileMyReview) {
        adapter = ProfileReviewAdapter(
            onClick = {itemId ->
                val parentNavController = requireActivity().findNavController(R.id.fcv_main_activity_container)
                val action = MainFragmentDirections.actionMainFragmentToNaviInfo(itemId)
                parentNavController.navigate(action)
            },reviewsList
        )
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(ProfileReviewItemDecoration(context,reviewsList))
        ItemTouchHelper(SwipeHelper()).attachToRecyclerView(this)
    }

    private fun setupClickListeners() = with(binding) {
        txEditProfile.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviProfileEdit())
        }
        ibSetting.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviSetting())
        }
        ibMyReview.setOnClickListener {
            navigateTo(MainFragmentDirections.actionMainFragmentToNaviProfileMyReview())
        }
    }

    private fun navigateTo(action: NavDirections) {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        navController.navigate(action)
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflProfile.startShimmer()
            binding.sflProfile.visibility = View.VISIBLE
            binding.clProfile.visibility = View.GONE
        } else {
            binding.sflProfile.stopShimmer()
            binding.sflProfile.visibility = View.GONE
            binding.clProfile.visibility = View.VISIBLE
        }
    }

}
