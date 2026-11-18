package de.hartz.software.chatkitcompose

import de.hartz.software.chatkitcompose.utils.ChatDateFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatDateFormatterTest {

    @Test
    fun `isSameDay returns true for same calendar day and false across midnight`() {
        val morning = dateOf(2026, Calendar.NOVEMBER, 18, 9, 15)
        val night = dateOf(2026, Calendar.NOVEMBER, 18, 23, 59)
        val nextDay = dateOf(2026, Calendar.NOVEMBER, 19, 0, 1)

        assertTrue(ChatDateFormatter.isSameDay(morning, night))
        assertFalse(ChatDateFormatter.isSameDay(night, nextDay))
    }

    @Test
    fun `isToday and isYesterday distinguish reference days accurately`() {
        val now = dateOf(2026, Calendar.NOVEMBER, 18, 14, 0)
        val todayEarlier = dateOf(2026, Calendar.NOVEMBER, 18, 8, 30)
        val yesterday = dateOf(2026, Calendar.NOVEMBER, 17, 22, 45)
        val twoDaysAgo = dateOf(2026, Calendar.NOVEMBER, 16, 12, 0)

        assertTrue(ChatDateFormatter.isToday(todayEarlier, now))
        assertFalse(ChatDateFormatter.isToday(yesterday, now))

        assertTrue(ChatDateFormatter.isYesterday(yesterday, now))
        assertFalse(ChatDateFormatter.isYesterday(todayEarlier, now))
        assertFalse(ChatDateFormatter.isYesterday(twoDaysAgo, now))
    }

    @Test
    fun `formatChatDateHeader returns localized relative and absolute labels`() {
        val now = dateOf(2026, Calendar.NOVEMBER, 18, 12, 0)
        val today = dateOf(2026, Calendar.NOVEMBER, 18, 9, 0)
        val yesterday = dateOf(2026, Calendar.NOVEMBER, 17, 18, 0)
        val earlierThisYear = dateOf(2026, Calendar.MARCH, 5, 10, 0)
        val lastYear = dateOf(2025, Calendar.DECEMBER, 24, 20, 0)

        assertEquals(
            "Today",
            ChatDateFormatter.formatChatDateHeader(today, now = now, locale = Locale.US),
        )
        assertEquals(
            "Yesterday",
            ChatDateFormatter.formatChatDateHeader(yesterday, now = now, locale = Locale.US),
        )
        assertEquals(
            "5 March",
            ChatDateFormatter.formatChatDateHeader(earlierThisYear, now = now, locale = Locale.US),
        )
        assertEquals(
            "24 December 2025",
            ChatDateFormatter.formatChatDateHeader(lastYear, now = now, locale = Locale.US),
        )
    }

    @Test
    fun `format supports standard templates`() {
        val target = dateOf(2026, Calendar.JULY, 4, 16, 5)
        assertEquals("16:05", ChatDateFormatter.format(target, ChatDateFormatter.Template.TIME, Locale.US))
        assertEquals("4 July 2026", ChatDateFormatter.format(target, ChatDateFormatter.Template.STRING_DAY_MONTH_YEAR, Locale.US))
    }

    private fun dateOf(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date =
        Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
}

