package com.ita.poppop.view.main.profile

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.databinding.FragmentProfileBinding
import com.ita.poppop.util.RetrofitClient
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.profile.holder.ProfileReviewAdapter
import com.ita.poppop.view.main.profile.holder.ProfileReviewItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val repository: MemberRepository = MemberRepositoryImpl(RetrofitClient.memberApi)

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
                    repository.getMemberInfo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnoTspIDtmJUiLCJpZCI6IjQzMjQ0NzEzMTQiLCJuaWNrTmFtZSI6IuyehOykgO2YlSIsImVtYWlsIjoibGltanVuaHllbmdAZ21haWwuY29tIiwiUHJvZmlsZUltYWdlIjoiaHR0cHM6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHBzOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzUxODY3NDYyfQ.84sTRKySacPuKTTgZZXM0SUmxPD_8ujFobzpDx0HUSU")
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
