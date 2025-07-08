package com.ita.poppop.view.empty.info.detail

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoDetailBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.detail.recommend.InfoRecommendRVAdapter
import com.ita.poppop.view.empty.info.detail.recommend.InfoRecommendViewModel

class InfoDetailFragment: BaseFragment<FragmentInfoDetailBinding>(R.layout.fragment_info_detail) {

    private lateinit var infoDetailViewModel: InfoDetailViewModel

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
            val factory = ViewModelFactory { InfoDetailViewModel(repository) }
            infoDetailViewModel = ViewModelProvider(this@InfoDetailFragment, factory)[InfoDetailViewModel::class.java]

            infoDetailViewModel.getInfoDetail(popupId)

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
        }
    }
}