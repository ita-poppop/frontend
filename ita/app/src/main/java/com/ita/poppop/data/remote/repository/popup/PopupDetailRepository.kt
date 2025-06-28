package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.dto.PopupDetailData

interface PopupDetailRepository {
    suspend fun getPopupDetail(popupId: Int): PopupDetailData?
}