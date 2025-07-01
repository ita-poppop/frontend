package com.ita.poppop.view.empty.info.detail

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popups.PopupDetailRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoDetailBinding
import com.ita.poppop.util.RetrofitClient
import com.ita.poppop.view.empty.info.recommend.InfoRecommendRVAdapter
import com.ita.poppop.view.empty.info.recommend.InfoRecommendViewModel

class InfoDetailFragment: BaseFragment<FragmentInfoDetailBinding>(R.layout.fragment_info_detail) {

    private lateinit var infoDetailViewModel: InfoDetailViewModel

    private lateinit var infoRecommendViewModel: InfoRecommendViewModel

    private val infoRecommendRVAdapter by lazy {
        InfoRecommendRVAdapter()
    }

    override fun initView() {
        binding.apply {

            //val popupId = arguments?.getInt("popupId") ?: return
            val popupId = 1

            val repository = PopupDetailRepositoryImpl(RetrofitClient.popupApi)
            infoDetailViewModel = InfoDetailViewModel(repository)

            infoDetailViewModel.getInfoDetail(popupId)

            infoDetailViewModel.infoDetail.observe(viewLifecycleOwner) { response ->
                tvInfoDetail.text = response.detail
                //tvInfoDetailComment.text = response.comment
            }

            infoRecommendViewModel = ViewModelProvider(this@InfoDetailFragment).get(InfoRecommendViewModel::class.java)

            // 추천
            rvInfoRecommend.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = infoRecommendRVAdapter
            }
            infoRecommendViewModel.getInfoRecommend()
            infoRecommendViewModel.inforecommendList.observe(viewLifecycleOwner, Observer { response ->
                infoRecommendRVAdapter.submitList(response)

                //binding.emptyStateLayout.root.run { if(response.isNullOrEmpty()) show() else hide()}
            })
        }
    }
}