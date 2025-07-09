package com.ita.poppop.view.empty.info

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.repository.popup.BookmarkRepositoryImpl
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.detail.InfoDetailFragment
import com.ita.poppop.view.empty.info.review.InfoReviewFragment
import com.ita.poppop.view.empty.info.story.InfoStoryRVAdapter
import com.ita.poppop.view.empty.info.story.InfoStoryViewModel
import com.ita.poppop.viewmodel.MainAViewModel


class InfoFragment: BaseFragment<FragmentInfoBinding>(R.layout.fragment_info) {

    private val args: InfoFragmentArgs by navArgs()
    val mainViewModel: MainAViewModel by activityViewModels()
    private lateinit var infoViewModel: InfoViewModel

    private lateinit var infoStoryViewModel: InfoStoryViewModel

    private val infoStoryRVAdapter by lazy {
        InfoStoryRVAdapter()
    }
    private lateinit var infoViewHolder: InfoViewHolder


    private var isFavorite = false

    override fun initView() {
        setupWindowInsets()
        binding.apply {

            val popupId = args.popupId

            // 상단 제목 상태 제어
            svInfo.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                val showTopbar = scrollY > 0
                cvInfoTopbar.alpha = if (showTopbar) 1f else 0f
                ibInfoBack.visibility = if (showTopbar) View.GONE else View.VISIBLE
            }

            ibInfoBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            acbFavorites.setOnClickListener {
                isFavorite = !isFavorite

                val icStar = if (isFavorite) {
                    R.drawable.info_favorites_star_icon_filled
                } else {
                    R.drawable.info_favorites_star_icon_outlined
                }

                // drawable 교체
                acbFavorites.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(requireContext(), icStar),
                    null, null, null
                )
                infoViewModel.postBookmark(popupId)
            }

            acbUploadReview.setOnClickListener{
                val parentNavController = requireParentFragment().findNavController()
                val action = InfoFragmentDirections.actionInfoFragmentToUploadFragment(popupId)
                parentNavController.navigate(action)
            }

            //val popupId = arguments?.getInt("popupId") ?: return
            //val popupId = 1325

            val repository = PopupsRepositoryImpl(RetrofitClient.popupApi)
            val bookmarkRepository = BookmarkRepositoryImpl(RetrofitClient.bookmarkApi)
            val factory = ViewModelFactory { InfoViewModel(mainViewModel.tokenPair.value.first.toString(), repository, bookmarkRepository) }
            infoViewModel = ViewModelProvider(this@InfoFragment, factory)[InfoViewModel::class.java]

            Log.d("InfoFragment", "Received popupId from args: $popupId")
            infoViewHolder = InfoViewHolder(binding)

            infoViewModel.getInfo(popupId)


            infoViewModel.infoData.observe(viewLifecycleOwner, Observer { info ->
                infoViewHolder.bind(info, infoViewModel)
            })

            infoStoryViewModel = ViewModelProvider(this@InfoFragment).get(InfoStoryViewModel::class.java)

            // 스토리
            rvInfoStory.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = infoStoryRVAdapter
            }
            infoStoryViewModel.getInfoStory()
            infoStoryViewModel.infostoryList.observe(viewLifecycleOwner, Observer { response ->
                infoStoryRVAdapter.submitList(response)

                //binding.emptyStateLayout.root.run { if(response.isNullOrEmpty()) show() else hide()}
            })

            // 탭 화면
            loadFragment(InfoDetailFragment(), popupId)
            icInfoTablayout.tlInfo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {loadFragment(InfoDetailFragment(), popupId)}
                        else -> {loadFragment(InfoReviewFragment(), popupId)}
                   }
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

            })

            // 리뷰 상세에서 뒤로가기시 리뷰탭
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Int>("reviewTab")
                ?.observe(viewLifecycleOwner) { tabIndex ->
                    if (tabIndex != null) {
                        icInfoTablayout.tlInfo.getTabAt(tabIndex)?.select()
                    }
                }
        }

    }
    private fun loadFragment(fragment: Fragment, popupId: Int): Boolean {
        fragment.arguments = Bundle().apply {
            putInt("popupId", popupId)
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_info_tab, fragment)
            .commit()
        return true
    }
}