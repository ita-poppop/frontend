package com.ita.poppop.view.empty.info.detail

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoDetailBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.InfoFragment
import com.ita.poppop.view.empty.info.detail.recommend.InfoRecommendRVAdapter
import com.ita.poppop.view.empty.info.detail.recommend.InfoRecommendViewModel
import com.ita.poppop.viewmodel.MainAViewModel

class InfoDetailFragment: BaseFragment<FragmentInfoDetailBinding>(R.layout.fragment_info_detail) {

    private lateinit var infoDetailViewModel: InfoDetailViewModel
    val mainViewModel: MainAViewModel by activityViewModels()
    private lateinit var infoRecommendViewModel: InfoRecommendViewModel

    private val infoRecommendRVAdapter by lazy {
        InfoRecommendRVAdapter()
    }

    override fun initView() {
        binding.apply {

            //val popupId = arguments?.getInt("popupId") ?: return
            //val popupId = 1325
            val popupId = arguments?.getInt("popupId") ?: 0

            val repository = PopupsRepositoryImpl(RetrofitClient.popupApi)
            val factory = ViewModelFactory { InfoDetailViewModel(mainViewModel.tokenPair.value.first.toString(),repository) }
            infoDetailViewModel = ViewModelProvider(this@InfoDetailFragment, factory)[InfoDetailViewModel::class.java]

            infoDetailViewModel.getInfoDetail(popupId)

            infoDetailViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                showSampleData(isLoading)
            })

            infoDetailViewModel.infoDetail.observe(viewLifecycleOwner) { combinedText ->
                tvInfoDetail.text = combinedText
                //tvInfoDetailComment.text = response.comment
            }


            val repository2 = PopupsRepositoryImpl(RetrofitClient.popupApi)
            val factory2 = ViewModelFactory { InfoRecommendViewModel(repository2) }
            infoRecommendViewModel = ViewModelProvider(this@InfoDetailFragment, factory2)[InfoRecommendViewModel::class.java]

            // 추천
            rvInfoRecommend.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = infoRecommendRVAdapter
            }
            infoRecommendViewModel.getInfoTrends()
            infoRecommendViewModel.infoTrendList.observe(viewLifecycleOwner, Observer { response ->
                infoRecommendRVAdapter.submitList(response)

                //binding.emptyStateLayout.root.run { if(response.isNullOrEmpty()) show() else hide()}
            })

            infoRecommendRVAdapter.setInfoRecommendItemClickListener(object : InfoRecommendRVAdapter.InfoRecommendItemClickListener{
                override fun onItemClick(position: Int) {
                    //val popupId = 1
                    val item = infoRecommendRVAdapter.currentList.getOrNull(position) ?: return
                    val popupId = item.itemId

                    (parentFragment as? InfoFragment)?.recommendItemClicked(popupId)
                }
            })
        }
    }
    fun updatePopup(popupId: Int) {
        infoDetailViewModel.getInfoDetail(popupId)
    }
    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflInfoDetail.startShimmer()
            binding.sflInfoDetail.visibility = View.VISIBLE
            binding.clInfoDetail.visibility = View.GONE
        } else {
            binding.sflInfoDetail.stopShimmer()
            binding.sflInfoDetail.visibility = View.GONE
            binding.clInfoDetail.visibility = View.VISIBLE
        }
    }
}