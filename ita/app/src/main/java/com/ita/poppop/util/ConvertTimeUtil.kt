package com.ita.poppop.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ConvertTimeUtil {
    fun convertRelativeTime(isoString: String): String {
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val createdTime = LocalDateTime.parse(isoString, formatter)
            val now = LocalDateTime.now(ZoneId.systemDefault())

            val minutes = ChronoUnit.MINUTES.between(createdTime, now)
            when {
                minutes < 1 -> "방금 전"
                minutes < 60 -> "${minutes}분 전"
                else -> {
                    val hours = ChronoUnit.HOURS.between(createdTime, now)
                    if (hours < 24) return "${hours}시간 전"

                    val days = ChronoUnit.DAYS.between(createdTime, now)
                    if (days < 7) return "${days}일 전"

                    val weeks = ChronoUnit.WEEKS.between(createdTime, now)
                    if (weeks < 4) return "${weeks}주 전"

                    val months = ChronoUnit.MONTHS.between(createdTime, now)
                    if (months < 12) return "${months}개월 전"

                    createdTime.format(DateTimeFormatter.ofPattern("MM/dd"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}