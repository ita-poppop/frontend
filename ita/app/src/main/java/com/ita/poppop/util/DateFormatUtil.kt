package com.ita.poppop.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * 날짜 형식 변환 유틸리티 클래스
 * 입력: "2025-09-20 ~ 2025-09-20" 형식
 * 출력: "25.09.20 - 25.09.20" 형식
 */
class DateFormatUtil {

    companion object {
        private const val INPUT_DATE_FORMAT = "yyyy-MM-dd"
        private const val OUTPUT_DATE_FORMAT = "yy.MM.dd"
        private const val INPUT_SEPARATOR = " ~ "
        private const val OUTPUT_SEPARATOR = " - "

        /**
         * 날짜 범위 문자열을 변환합니다.
         * @param dateRange 입력 날짜 범위 (예: "2025-09-20 ~ 2025-09-20")
         * @return 변환된 날짜 범위 (예: "25.09.20 - 25.09.20")
         */
        fun convertDateRange(dateRange: String): String {
            try {
                // 입력 문자열이 비어있거나 null인 경우 처리
                if (dateRange.isBlank()) {
                    return ""
                }

                // 구분자로 날짜 분리
                val dates = dateRange.split(INPUT_SEPARATOR)

                if (dates.size != 2) {
                    throw IllegalArgumentException("Invalid date range format. Expected format: 'yyyy-MM-dd ~ yyyy-MM-dd'")
                }

                val startDate = dates[0].trim()
                val endDate = dates[1].trim()

                // 각 날짜 변환
                val convertedStartDate = convertSingleDate(startDate)
                val convertedEndDate = convertSingleDate(endDate)

                return "$convertedStartDate$OUTPUT_SEPARATOR$convertedEndDate"

            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to convert date range: ${e.message}", e)
            }
        }

        /**
         * 단일 날짜 문자열을 변환합니다.
         * @param date 입력 날짜 (예: "2025-09-20")
         * @return 변환된 날짜 (예: "25.09.20")
         */
        fun convertSingleDate(date: String): String {
            try {
                val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
                val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())

                val parsedDate = inputFormat.parse(date)
                    ?: throw IllegalArgumentException("Unable to parse date: $date")

                return outputFormat.format(parsedDate)

            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to convert single date: ${e.message}", e)
            }
        }

        /**
         * 날짜 범위가 유효한 형식인지 검증합니다.
         * @param dateRange 검증할 날짜 범위
         * @return 유효한 형식이면 true, 아니면 false
         */
        fun isValidDateRange(dateRange: String): Boolean {
            return try {
                convertDateRange(dateRange)
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 단일 날짜가 유효한 형식인지 검증합니다.
         * @param date 검증할 날짜
         * @return 유효한 형식이면 true, 아니면 false
         */
        fun isValidSingleDate(date: String): Boolean {
            return try {
                convertSingleDate(date)
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 현재 날짜를 지정된 형식으로 반환합니다.
         * @param format 날짜 형식 (기본값: "yy.MM.dd")
         * @return 형식화된 현재 날짜
         */
        fun getCurrentDate(format: String = OUTPUT_DATE_FORMAT): String {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            return dateFormat.format(Date())
        }
    }
}