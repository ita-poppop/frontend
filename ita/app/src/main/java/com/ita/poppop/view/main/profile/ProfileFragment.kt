package com.ita.poppop.view.main.profile

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentHomeBinding
import com.ita.poppop.databinding.FragmentProfileBinding
import com.ita.poppop.util.SwipeHelper
import com.ita.poppop.view.empty.upcoming.holder.UpcomingAdapter
import com.ita.poppop.view.empty.upcoming.holder.UpcomingItemDecoration
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.profile.holder.ProfileReviewAdapter
import com.ita.poppop.view.main.profile.holder.ProfileReviewItemDecoration
import com.ita.poppop.viewmodel.empty.upload.UploadViewModel
import com.ita.poppop.viewmodel.main.MainViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

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
