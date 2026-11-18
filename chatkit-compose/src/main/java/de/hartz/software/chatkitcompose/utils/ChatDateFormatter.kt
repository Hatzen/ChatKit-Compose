package de.hartz.software.chatkitcompose.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Date and timestamp formatting utilities for chat date headers, bubble timestamps, and dialog lists.
 *
 * Replaces `com.stfalcon.chatkit.utils.DateFormatter` with deterministic, locale-aware helpers.
 */
object ChatDateFormatter {

    /**
     * Functional interface for custom date header or message time formatting.
     */
    fun interface Formatter {
        fun format(date: Date): String
    }

    /**
     * Standard display format templates matching legacy ChatKit templates.
     */
    enum class Template(val pattern: String) {
        STRING_DAY_MONTH_YEAR("d MMMM yyyy"),
        STRING_DAY_MONTH("d MMMM"),
        TIME("HH:mm"),
        STRING_DAY_MONTH_TIME("d MMMM, HH:mm"),
    }

    /**
     * Formats [date] using a predefined [template] and [locale].
     */
    fun format(
        date: Date,
        template: Template,
        locale: Locale = Locale.getDefault(),
    ): String = format(date, template.pattern, locale)

    /**
     * Formats [date] using a custom pattern string and [locale].
     */
    fun format(
        date: Date,
        pattern: String,
        locale: Locale = Locale.getDefault(),
    ): String = SimpleDateFormat(pattern, locale).format(date)

    /**
     * Formats a chat timeline date header ("Today", "Yesterday", "d MMMM", or "d MMMM yyyy").
     */
    fun formatChatDateHeader(
        date: Date,
        todayLabel: String = "Today",
        yesterdayLabel: String = "Yesterday",
        now: Date = Date(),
        locale: Locale = Locale.getDefault(),
    ): String = when {
        isToday(date, now) -> todayLabel
        isYesterday(date, now) -> yesterdayLabel
        isSameYear(date, now) -> format(date, Template.STRING_DAY_MONTH, locale)
        else -> format(date, Template.STRING_DAY_MONTH_YEAR, locale)
    }

    /**
     * Formats a compact timestamp suitable for dialog list rows (time today, "Yesterday", or short date).
     */
    fun formatDialogTimestamp(
        date: Date,
        yesterdayLabel: String = "Yesterday",
        now: Date = Date(),
        locale: Locale = Locale.getDefault(),
    ): String = when {
        isToday(date, now) -> format(date, Template.TIME, locale)
        isYesterday(date, now) -> yesterdayLabel
        isSameYear(date, now) -> format(date, "d MMM", locale)
        else -> format(date, "dd/MM/yy", locale)
    }

    /**
     * Returns `true` if [date1] and [date2] fall on the same calendar day.
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = calendarOf(date1)
        val cal2 = calendarOf(date2)
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Returns `true` if [date1] and [date2] fall in the same calendar year.
     */
    fun isSameYear(date1: Date, date2: Date): Boolean {
        val cal1 = calendarOf(date1)
        val cal2 = calendarOf(date2)
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    /**
     * Returns `true` if [date] is on the same calendar day as [now].
     */
    fun isToday(date: Date, now: Date = Date()): Boolean = isSameDay(date, now)

    /**
     * Returns `true` if [date] is the calendar day immediately preceding [now].
     */
    fun isYesterday(date: Date, now: Date = Date()): Boolean {
        val yesterday = calendarOf(now).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.time
        return isSameDay(date, yesterday)
    }

    /**
     * Returns a stable calendar-day key (`YYYY-DDD`) useful as a Compose lazy item key.
     */
    internal fun dayKey(date: Date): String {
        val cal = calendarOf(date)
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    private fun calendarOf(date: Date): Calendar = Calendar.getInstance().apply {
        time = date
    }
}

