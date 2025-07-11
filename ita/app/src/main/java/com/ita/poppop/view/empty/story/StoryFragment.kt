package com.ita.poppop.view.empty.story

import android.content.Context
import android.util.Log
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.stories.StoryData
import com.ita.poppop.data.remote.repository.stories.StoriesRepository
import com.ita.poppop.data.remote.repository.stories.StoriesRepositoryImpl
import com.ita.poppop.databinding.FragmentStoryBinding
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.loaction.LocationMapFragmentArgs
import com.ita.poppop.view.empty.story.sub.StoryViewAdapter
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.home.waiting.HomeWaitingAdapter
import com.ita.poppop.view.main.home.waiting.HomeWaitingItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import com.ita.poppop.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class StoryFragment : BaseFragment<FragmentStoryBinding>(R.layout.fragment_story) {
    private val args: StoryFragmentArgs by navArgs()
    private lateinit var mainViewModel: MainViewModel
    //args.location

    override fun initView() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        Log.d("StoryFragment", "waitingList:\n" + mainViewModel.storyList.value.joinToString("\n") {
            "writerName: ${it.writerName}, estimatedWaitCount: ${it.estimatedWaitCount}"
        })
        setupViewPager(getStoriesFromStoryIdInOrder(args.reviewId,mainViewModel.storyList.value!!))
    }

    private fun setupViewPager(list: List<StoryData>) {
        binding.vpStory.adapter = StoryViewAdapter(list,this)
    }

    open fun changeViewPager() {
        binding.vpStory.setCurrentItem(binding.vpStory.currentItem + 1, true)
    }

    private fun setSystemBarAppearance(isLight: Boolean) {
        WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        ).apply {
            isAppearanceLightStatusBars = isLight
            isAppearanceLightNavigationBars = isLight
        }
    }

    fun getStoriesFromStoryIdInOrder(storyId: Int,waitingList: List<StoryData>): List<StoryData> {
        val index = waitingList.indexOfFirst { it.storyId == storyId }

        return if (index != -1) {
            waitingList.subList(index, waitingList.size)
        } else {
            emptyList()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setSystemBarAppearance(isLight = false)
    }

    override fun onDetach() {
        super.onDetach()
        setSystemBarAppearance(isLight = true)
    }
}
