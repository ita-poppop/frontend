package com.ita.poppop.view.empty.story

import android.content.Context
import android.util.Log
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.activityViewModels
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class StoryFragment : BaseFragment<FragmentStoryBinding>(R.layout.fragment_story) {
    private val args: StoryFragmentArgs by navArgs()
    val mainViewModel: MainAViewModel by activityViewModels()
    //args.location

    private val repository: StoriesRepository = StoriesRepositoryImpl(RetrofitClient.storiesApi)
    override fun initView() {
        var waitingList = listOf<StoryData>()
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getStories(mainViewModel.tokenPair.value.first.toString(),1, 10)
                }

                if (result.isSuccessful) {
                    waitingList = result.body()?.data!!
                    Log.d("checkDatata","waitingList : ${waitingList}")
                    setupViewPager(waitingList)


                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
//                showToast("WaitingHTTP 에러: ${e.code()} - ${e.message()}")
                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
//                showToast("Waiting에러 발생: ${e.localizedMessage ?: "알 수 없는 오류"}")
                Log.e("API_ERROR", "Exception: ${e.message}", e)
            }
        }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setSystemBarAppearance(isLight = false)
    }

    override fun onDetach() {
        super.onDetach()
        setSystemBarAppearance(isLight = true)
    }
}
