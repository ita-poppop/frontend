package com.ita.poppop.view.empty.info.review.detail.reply

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.R
import com.ita.poppop.view.empty.info.review.InfoReviewDetailReplyRVItem
import com.ita.poppop.view.empty.info.review.InfoReviewRVItem
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVItem
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVItem

class InfoReviewDetailReplyViewModel : ViewModel() {
    private val _inforeviewdetailreplyList =
        MutableLiveData<MutableList<InfoReviewDetailReplyRVItem>>()
    val inforeviewdetailreplyList: LiveData<MutableList<InfoReviewDetailReplyRVItem>> =
        _inforeviewdetailreplyList

    // 댓글 화면에서 대댓글 추가
    fun addReply(reply: String) {
        val currentList = _inforeviewdetailreplyList.value ?: mutableListOf()
        val newId = (currentList.maxOfOrNull { it.itemId } ?: 0) + 1
        val newReply = InfoReviewDetailReplyRVItem(
            itemId = newId,
            username = "hello",
            profileImage = R.drawable._profile_load_icon,
            reply = reply,
            time = "방금 전"
        )
        val updatedList = currentList.toMutableList()
        updatedList.add(newReply)
        _inforeviewdetailreplyList.value = updatedList

    }

    // 댓글 화면에서 대댓글 삭제
    fun deleteReply(replyItemId: Int) {
        val currentList = _inforeviewdetailreplyList.value ?: return
        val updatedList = currentList.filterNot { it.itemId == replyItemId }.toMutableList()
        _inforeviewdetailreplyList.value = updatedList
    }

    fun getInfoReviewDetailReply() {
        val list = mutableListOf<InfoReviewDetailReplyRVItem>()
        /*list.clear()*/
        list.add(
            InfoReviewDetailReplyRVItem(
                1,
                R.drawable.main_btn_favorites_icon,
                "wild_zeal",
                "2시간 전",
                "한번 방문해보세요."
            )
        )
        list.add(
            InfoReviewDetailReplyRVItem(
                2,
                R.drawable.main_btn_favorites_icon,
                "wild_zeal",
                "6일전",
                "몇시쯤에 가셨어요?"
            )
        )

        _inforeviewdetailreplyList.value = list
    }
}