package com.ita.poppop.view.empty.story.sub


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ita.poppop.data.remote.dto.stories.StoryData


class StoryViewAdapter(private val storyList: List<StoryData>,fragment: Fragment,) : FragmentStateAdapter(fragment) {



    override fun getItemCount(): Int = storyList.size

    override fun createFragment(position: Int): Fragment =
        StoryViewFragment.newInstance(storyList[position])
}

