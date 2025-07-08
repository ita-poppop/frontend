package com.ita.poppop.view.main.profile

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.databinding.FragmentProfileBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.profile.holder.ProfileReviewAdapter
import com.ita.poppop.view.main.profile.holder.ProfileReviewItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val repository: MemberRepository = MemberRepositoryImpl(RetrofitClient.memberApi)
    val mainAViewModel: MainAViewModel by activityViewModels()
    override fun initView() {

        setupProfileReviewRecyclerView()
        setupClickListeners()

    }

    private fun setupClickListeners() = with(binding) {
        txEditProfile.setOnClickListener { navigateToEditProfile() }
        ibSetting.setOnClickListener { navigateToSettings() }
        ibMyReview.setOnClickListener { navigateToMyReview() }
    }

    private fun navigateToEditProfile() {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getMemberInfo(mainAViewModel.tokenPair.value.first.toString())
                }

                if (result.isSuccessful) {
                    Log.d("checkLoginEx","user : ${result.body()}")
                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
//        val navController = requireActivity().findNavController(R.id.fcv_main_activity_container)
//        val action = MainFragmentDirections.actionMainFragmentToNaviProfileEdit()
//        navController.navigate(action)
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

    private fun setupProfileReviewRecyclerView() {
        val profileReviewList = (0..9).toMutableList()
        val adapter = ProfileReviewAdapter(profileReviewList)

        with(binding.rvProfileMyReview) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            addItemDecoration(ProfileReviewItemDecoration())

            ItemTouchHelper(SwipeHelper()).attachToRecyclerView(this)
        }
    }
}
