package com.ita.poppop.view.main.profile

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val repository: ProfileRepository = ProfileRepositoryImpl(RetrofitClient.profileApi)
    val mainAViewModel: MainAViewModel by activityViewModels()
    private lateinit var mainViewModel: MainViewModel
    override fun initView() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setipPorfile()
        setData()
        setupClickListeners()

    }
    private fun setData(){
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

    private fun setipPorfile() {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getProfile(mainAViewModel.tokenPair.value.first.toString(),1,10)
                }

                if (result.isSuccessful) {
                    Log.d("checkUserEx","user : ${result.body()}")



                    mainViewModel.setUser(UserProfile(
                        userName = result.body()?.data?.userName.toString(),
                        userProfileImage = result.body()?.data?.profileUrl,
                        userLikesCount = result.body()?.data?.totalLikeCount,
                        userReviewCount = result.body()?.data?.reviewCount

                    ))


                    setupProfileReviewRecyclerView(result.body()?.data?.reviews!!)
                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        txEditProfile.setOnClickListener { navigateToEditProfile() }
        ibSetting.setOnClickListener { navigateToSettings() }
        ibMyReview.setOnClickListener { navigateToMyReview() }
    }

    private fun navigateToEditProfile() {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        val action = MainFragmentDirections.actionMainFragmentToNaviProfileEdit()
        navController.navigate(action)
    }

    private fun navigateToSettings() {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        val action = MainFragmentDirections.actionMainFragmentToNaviSetting()
        navController.navigate(action)
    }

    private fun navigateToMyReview() {
        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
        val action = MainFragmentDirections.actionMainFragmentToNaviProfileMyReview()
        navController.navigate(action)
    }

    private fun setupProfileReviewRecyclerView(list: List<ReviewItem>) {
        val adapter = ProfileReviewAdapter(list)

        with(binding.rvProfileMyReview) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            addItemDecoration(ProfileReviewItemDecoration(context,list))

            ItemTouchHelper(SwipeHelper()).attachToRecyclerView(this)
        }
    }
}
