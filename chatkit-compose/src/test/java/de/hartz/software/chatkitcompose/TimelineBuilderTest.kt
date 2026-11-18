package de.hartz.software.chatkitcompose

import de.hartz.software.chatkitcompose.messages.MessageGroupPosition
import de.hartz.software.chatkitcompose.messages.TimelineItem
import de.hartz.software.chatkitcompose.messages.buildTimelineItems
import de.hartz.software.chatkitcompose.models.ChatMessage
import de.hartz.software.chatkitcompose.models.ChatUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date

class TimelineBuilderTest {

    private val me = ChatUser(id = "me", name = "Kai")
    private val alice = ChatUser(id = "alice", name = "Alice")

    @Test
    fun `buildTimelineItems inserts date headers per day and orders newest first`() {
        val day1Msg = ChatMessage("m1", "Yesterday msg", alice, dateOf(2026, Calendar.NOVEMBER, 17, 15, 0))
        val day2Msg1 = ChatMessage("m2", "Today morning", me, dateOf(2026, Calendar.NOVEMBER, 18, 9, 0))
        val day2Msg2 = ChatMessage("m3", "Today afternoon", alice, dateOf(2026, Calendar.NOVEMBER, 18, 14, 0))

        val items = buildTimelineItems(
            messages = listOf(day1Msg, day2Msg1, day2Msg2),
            senderId = me.id,
        )

        // Reverse-layout order: index 0 is bottom (newest), last index is top (oldest header)
        assertEquals(5, items.size)

        val item0 = items[0] as TimelineItem.MessageItem<*>
        assertEquals("m3", item0.message.id)
        assertTrue(item0.isIncoming)
        assertEquals(MessageGroupPosition.SINGLE, item0.groupPosition)

        val item1 = items[1] as TimelineItem.MessageItem<*>
        assertEquals("m2", item1.message.id)
        assertFalse(item1.isIncoming)
        assertEquals(MessageGroupPosition.SINGLE, item1.groupPosition)

        assertTrue(items[2] is TimelineItem.DateHeader)
        assertEquals(day2Msg1.createdAt, (items[2] as TimelineItem.DateHeader).date)

        val item3 = items[3] as TimelineItem.MessageItem<*>
        assertEquals("m1", item3.message.id)
        assertEquals(MessageGroupPosition.SINGLE, item3.groupPosition)

        assertTrue(items[4] is TimelineItem.DateHeader)
        assertEquals(day1Msg.createdAt, (items[4] as TimelineItem.DateHeader).date)
    }

    @Test
    fun `buildTimelineItems groups consecutive incoming messages within window and shows avatar only on last`() {
        val m1 = ChatMessage("1", "Hey", alice, dateOf(2026, Calendar.NOVEMBER, 18, 10, 0))
        val m2 = ChatMessage("2", "Quick question", alice, dateOf(2026, Calendar.NOVEMBER, 18, 10, 1))
        val m3 = ChatMessage("3", "About Compose!", alice, dateOf(2026, Calendar.NOVEMBER, 18, 10, 2))

        val items = buildTimelineItems(
            messages = listOf(m1, m2, m3),
            senderId = me.id,
            showIncomingAvatar = true,
            showIncomingAuthorName = true,
        )

        val msg3 = items[0] as TimelineItem.MessageItem<*> // bottom-most (visually LAST)
        val msg2 = items[1] as TimelineItem.MessageItem<*> // middle
        val msg1 = items[2] as TimelineItem.MessageItem<*> // top-most (visually FIRST)

        assertEquals(MessageGroupPosition.LAST, msg3.groupPosition)
        assertTrue(msg3.showAvatar)
        assertFalse(msg3.showAuthorName)

        assertEquals(MessageGroupPosition.MIDDLE, msg2.groupPosition)
        assertFalse(msg2.showAvatar)
        assertFalse(msg2.showAuthorName)

        assertEquals(MessageGroupPosition.FIRST, msg1.groupPosition)
        assertFalse(msg1.showAvatar)
        assertTrue(msg1.showAuthorName)
    }

    @Test
    fun `buildTimelineItems splits groups when messages exceed grouping window`() {
        val m1 = ChatMessage("1", "First", alice, dateOf(2026, Calendar.NOVEMBER, 18, 10, 0))
        val m2 = ChatMessage("2", "Ten mins later", alice, dateOf(2026, Calendar.NOVEMBER, 18, 10, 10))

        val items = buildTimelineItems(listOf(m1, m2), senderId = me.id)
        val newest = items[0] as TimelineItem.MessageItem<*>
        val older = items[1] as TimelineItem.MessageItem<*>

        assertEquals(MessageGroupPosition.SINGLE, newest.groupPosition)
        assertEquals(MessageGroupPosition.SINGLE, older.groupPosition)
    }

    private fun dateOf(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date =
        Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
}

