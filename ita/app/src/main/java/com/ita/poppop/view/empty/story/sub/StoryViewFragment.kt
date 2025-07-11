package com.ita.poppop.view.empty.story.sub

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.dto.stories.StoryData
import com.ita.poppop.data.remote.repository.stories.StoriesRepository
import com.ita.poppop.data.remote.repository.stories.StoriesRepositoryImpl
import com.ita.poppop.data.remote.repository.story.StoryRepository
import com.ita.poppop.data.remote.repository.story.StoryRepositoryImpl
import com.ita.poppop.databinding.FragmentStoryViewBinding
import com.ita.poppop.util.bottomsheet.WaitingBottomSheet
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.view.empty.story.StoryFragment
import com.ita.poppop.view.main.MainFragmentDirections
import com.ita.poppop.view.main.home.waiting.HomeWaitingAdapter
import com.ita.poppop.view.main.home.waiting.HomeWaitingItemDecoration
import com.ita.poppop.viewmodel.MainAViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class StoryViewFragment : BaseFragment<FragmentStoryViewBinding>(R.layout.fragment_story_view) {
    private val repository: StoryRepository = StoryRepositoryImpl(RetrofitClient.storyApi)
    val mainViewModel: MainAViewModel by activityViewModels()
    companion object {
        private const val ARG_DATA = "data"
        private const val DURATION = 15_000L
        private const val INTERVAL = 10L
        private const val MAX_PROGRESS = 1000

        fun newInstance(storyItem: StoryData): StoryViewFragment {
            return StoryViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, storyItem)
                }
            }
        }

    }

    private var storyData: StoryData? = null
    private var progressTimer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView() {
        loadArgs()
        loadData()
        initListeners()
        binding.tvStoryUser.text = storyData?.writerName?.replace("\"", "")
        binding.tvStoryDate.text = storyData?.createdAt
        binding.tvStoryContent.text = storyData?.popupTitle
        Glide.with(binding.root)
            .load(storyData?.writerProfileUrl)
            .centerCrop()
            .into(binding.ivUserThumbnail)
        Glide.with(binding.root)
            .load(storyData?.photoUrl)
            .centerCrop()
            .into(binding.ivStory)
    }

    private fun loadData(){
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getStoryDetail(mainViewModel.tokenPair.value.first.toString(),storyData!!.popupId,storyData!!.storyId)
                }
                if (result.isSuccessful) {
                    Log.d("checkDataClick","${storyData!!.popupId}.....${storyData!!.storyId}")
                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {

                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loadArgs() {
        storyData = arguments?.getParcelable(ARG_DATA, StoryData::class.java)
        Log.d("checkStory","${storyData}")
    }

    private fun initListeners() = binding.run {
        ibStoryCancel.setOnClickListener {
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            } else {
                findNavController(requireParentFragment()).popBackStack()
            }
        }

        ibStory3dot.setOnClickListener {
            cancelProgressTimer()
            WaitingBottomSheet(storyData!!.popupId,storyData!!.storyId).show(parentFragmentManager, "WaitingBottomSheet")
        }
    }

    private fun startProgressTimer() {
        progressTimer?.cancel()

        progressTimer = object : CountDownTimer(DURATION, INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((DURATION - millisUntilFinished) * MAX_PROGRESS / DURATION).toInt()
                binding.pb.progress = progress
            }

            override fun onFinish() {
                binding.pb.progress = MAX_PROGRESS
                Log.d("ProgressBar", "Timer finished")
                (parentFragment as? StoryFragment)?.changeViewPager()
            }
        }.also {
            it.start()
        }
    }

    private fun cancelProgressTimer() {
        progressTimer?.cancel()
        binding.pb.progress = 0
        progressTimer = null
    }

    override fun onResume() {
        super.onResume()
        startProgressTimer()
    }

    override fun onPause() {
        super.onPause()
        cancelProgressTimer()
    }
}
