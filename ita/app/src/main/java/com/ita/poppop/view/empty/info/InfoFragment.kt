package com.ita.poppop.view.empty.info

import android.os.Bundle
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
import com.ita.poppop.data.remote.dto.popups.PopupDetailData
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.data.remote.repository.popup.BookmarkRepositoryImpl
import com.ita.poppop.data.remote.repository.popups.PopupsRepositoryImpl
import com.ita.poppop.data.remote.repository.story.StoryRepositoryImpl
import com.ita.poppop.databinding.FragmentInfoBinding
import com.ita.poppop.util.ViewModelFactory
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.info.detail.InfoDetailFragment
import com.ita.poppop.view.empty.info.review.InfoReviewFragment
import com.ita.poppop.view.empty.info.story.InfoStoryRVAdapter
import com.ita.poppop.view.empty.info.story.InfoStoryViewModel
import com.ita.poppop.view.main.favorites.FavoritesRVAdapter
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

    private var popupItem: PopupDetailData? = null

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
                val searchData = popupItem?.let {
                    SearchData(
                        latitude = null,
                        longitude= null,
                        id = it.id,
                        imageUrl = it.imageUrl ?: "",
                        title = it.title ?: "",
                        date = null,
                        location = it.location ?: "",
                    )
                }

                val parentNavController = requireParentFragment().findNavController()
                val action = InfoFragmentDirections.actionInfoFragmentToUploadFragment(searchData, popupId)
                parentNavController.navigate(action)
            }

            //val popupId = arguments?.getInt("popupId") ?: return
            //val popupId = 1325

            val repository = PopupsRepositoryImpl(RetrofitClient.popupApi)
            val bookmarkRepository = BookmarkRepositoryImpl(RetrofitClient.bookmarkApi)
            val factory = ViewModelFactory { InfoViewModel(mainViewModel.tokenPair.value.first.toString(), repository, bookmarkRepository) }
            infoViewModel = ViewModelProvider(this@InfoFragment, factory)[InfoViewModel::class.java]

            infoViewHolder = InfoViewHolder(binding)

            infoViewModel.getInfo(popupId)

            infoViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                showSampleData(isLoading)
            })

            infoViewModel.infoData.observe(viewLifecycleOwner, Observer { info ->

                infoViewHolder.bind(info, infoViewModel)
                popupItem = info

                infoViewHolder.bind(info, infoViewModel)
                popupItem = info

                isFavorite = info.bookmarked  // 초기 상태 저장
                val icStar = if (isFavorite) {
                    R.drawable.info_favorites_star_icon_filled
                } else {
                    R.drawable.info_favorites_star_icon_outlined
                }
                binding.acbFavorites.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(requireContext(), icStar),
                    null, null, null
                )
            })

            val storyRepository = StoryRepositoryImpl(RetrofitClient.storyApi)
            val storyFactory = ViewModelFactory { InfoStoryViewModel(mainViewModel.tokenPair.value.first.toString(),storyRepository) }
            infoStoryViewModel = ViewModelProvider(this@InfoFragment, storyFactory)[InfoStoryViewModel::class.java]
            // 스토리
            rvInfoStory.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = infoStoryRVAdapter
            }
            infoStoryViewModel.getInfoStory(popupId)
            infoStoryViewModel.infostoryList.observe(viewLifecycleOwner, Observer { response ->
                infoStoryRVAdapter.submitList(response)

                //binding.emptyStateLayout.root.run { if(response.isNullOrEmpty()) show() else hide()}
            })

            infoStoryRVAdapter.setInfoStoryItemClickListener(object : InfoStoryRVAdapter.InfoStoryItemClickListener{
                override fun onItemClick(position: Int) {
                    TODO("Not yet implemented")
                }
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

    fun recommendItemClicked(popupId: Int) {

        binding.icInfoTablayout.tlInfo.getTabAt(0)?.select()

        // 팝업 아이템 갱신
        infoViewModel.getInfo(popupId)
        binding.svInfo.smoothScrollTo(0, 0)
        infoStoryViewModel.getInfoStory(popupId)
        val currentFragment = childFragmentManager.findFragmentById(R.id.fl_info_tab)
        if (currentFragment is InfoDetailFragment) {
            currentFragment.updatePopup(popupId)
        }
    }

    private fun loadFragment(fragment: Fragment, popupId: Int): Boolean {
        fragment.arguments = Bundle().apply {
            putInt("popupId", popupId)
            popupItem?.let { putParcelable("popupItem", it) }
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_info_tab, fragment)
            .commit()
        return true
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflInfo.startShimmer()
            binding.sflInfo.visibility = View.VISIBLE
            binding.clInfo.visibility = View.GONE
        } else {
            binding.sflInfo.stopShimmer()
            binding.sflInfo.visibility = View.GONE
            binding.clInfo.visibility = View.VISIBLE
        }
    }
}