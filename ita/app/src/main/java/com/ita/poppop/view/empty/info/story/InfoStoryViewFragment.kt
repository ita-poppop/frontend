    package com.ita.poppop.view.empty.info.story

    import android.os.Build
    import android.os.Bundle
    import android.os.CountDownTimer
    import android.util.Log
    import androidx.annotation.RequiresApi
    import androidx.fragment.app.activityViewModels
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
    import androidx.navigation.fragment.navArgs
    import com.bumptech.glide.Glide
    import com.ita.poppop.R
    import com.ita.poppop.base.BaseFragment
    import com.ita.poppop.data.remote.dto.story.StoryData
    import com.ita.poppop.data.remote.repository.story.StoryRepository
    import com.ita.poppop.data.remote.repository.story.StoryRepositoryImpl
    import com.ita.poppop.databinding.FragmentInfoStoryViewBinding
    import com.ita.poppop.databinding.FragmentStoryViewBinding
    import com.ita.poppop.util.ConvertTimeUtil
    import com.ita.poppop.util.bottomsheet.WaitingBottomSheet
    import com.ita.poppop.util.remote.RetrofitClient
    import com.ita.poppop.view.empty.story.StoryFragment
    import com.ita.poppop.view.empty.story.StoryFragmentArgs
    import com.ita.poppop.viewmodel.MainAViewModel
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import retrofit2.HttpException

    class InfoStoryViewFragment : BaseFragment<FragmentInfoStoryViewBinding>(R.layout.fragment_info_story_view) {
        private val repository: StoryRepository = StoryRepositoryImpl(RetrofitClient.storyApi)
        private val args: InfoStoryViewFragmentArgs by navArgs()
        val mainViewModel: MainAViewModel by activityViewModels()
        companion object {
            private const val ARG_DATA = "data"
            private const val DURATION = 15_000L
            private const val INTERVAL = 10L
            private const val MAX_PROGRESS = 1000

        }

        //private var storyData: StoryData? = null
        private var progressTimer: CountDownTimer? = null
        private var storyData: StoryData? = null
        private val popupId by lazy { args.popupId }
        private val popupTitle by lazy { args.popupTitle }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun initView() {
            storyData = args.data

            loadArgs()
            loadData()
            initListeners()

            val timeUtil = ConvertTimeUtil()
            val relativeTime = storyData?.createdAt?.let { timeUtil.convertRelativeTime(it) }

            binding.tvStoryUser.text = storyData?.writerName?.replace("\"", "")
            binding.tvStoryDate.text = relativeTime
            binding.tvStoryContent.text = popupTitle
            Glide.with(binding.root)
                .load(storyData?.profileUrl)
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
                        repository.getStoryDetail(mainViewModel.tokenPair.value.first.toString(),popupId,storyData!!.storyId)
                    }
                    if (result.isSuccessful) {
                        Log.d("checkDataClick","${popupId}.....${storyData!!.storyId}")
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
                WaitingBottomSheet(popupId,storyData!!.storyId).show(parentFragmentManager, "WaitingBottomSheet")
            }
        }

        private fun startProgressTimer() {
            progressTimer?.cancel()

            progressTimer = object : CountDownTimer(DURATION, INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = ((DURATION - millisUntilFinished) * MAX_PROGRESS / DURATION).toInt()
                    binding.pb.progress = progress
                }

                override fun onFinish() { //화면 종료
                    binding.pb.progress = MAX_PROGRESS
                    Log.d("ProgressBar", "Timer finished")
                    closeFragment()
                }
            }.also {
                it.start()
            }
        }

        private fun closeFragment() {
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            } else {
                findNavController(requireParentFragment()).popBackStack()
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
