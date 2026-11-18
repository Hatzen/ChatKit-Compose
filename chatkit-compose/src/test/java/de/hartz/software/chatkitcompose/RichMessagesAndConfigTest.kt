package de.hartz.software.chatkitcompose

import de.hartz.software.chatkitcompose.messages.MessagesListState
import de.hartz.software.chatkitcompose.messages.TimelineItem
import de.hartz.software.chatkitcompose.messages.buildTimelineItems
import de.hartz.software.chatkitcompose.models.ChatMessage
import de.hartz.software.chatkitcompose.models.ChatUser
import de.hartz.software.chatkitcompose.models.PollMessage
import de.hartz.software.chatkitcompose.models.PollOption
import de.hartz.software.chatkitcompose.models.SystemEventMessage
import de.hartz.software.chatkitcompose.theme.ChatKitAnimationConfig
import de.hartz.software.chatkitcompose.theme.ChatKitFeatureConfig
import de.hartz.software.chatkitcompose.utils.ChatFileTypeDetector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class RichMessagesAndConfigTest {

    private val me = ChatUser(id = "me", name = "Kai")
    private val alice = ChatUser(id = "alice", name = "Alice")

    @Test
    fun `ChatFileTypeDetector classifies extensions including wrapped txt suffixes`() {
        assertEquals(ChatFileTypeDetector.Kind.IMAGE, ChatFileTypeDetector.detectKind("photo.jpg"))
        assertEquals(ChatFileTypeDetector.Kind.IMAGE, ChatFileTypeDetector.detectKind("secret_photo.png.txt"))
        assertEquals(ChatFileTypeDetector.Kind.GIF, ChatFileTypeDetector.detectKind("anim.gif"))
        assertEquals(ChatFileTypeDetector.Kind.VIDEO, ChatFileTypeDetector.detectKind("clip.mp4"))
        assertEquals(ChatFileTypeDetector.Kind.VOICE, ChatFileTypeDetector.detectKind("note.opus"))
        assertEquals(ChatFileTypeDetector.Kind.CONTACT, ChatFileTypeDetector.detectKind("alice.vcf"))
        assertEquals(ChatFileTypeDetector.Kind.LOCATION, ChatFileTypeDetector.detectKind("meetup.geojson"))
        assertEquals(ChatFileTypeDetector.Kind.POLL, ChatFileTypeDetector.detectKind("lunch.poll"))
        assertEquals(ChatFileTypeDetector.Kind.DOCUMENT, ChatFileTypeDetector.detectKind("archive.zip"))

        assertEquals(ChatFileTypeDetector.Kind.GIF, ChatFileTypeDetector.detectKind("unknown", "image/gif"))
        assertEquals(ChatFileTypeDetector.Kind.VOICE, ChatFileTypeDetector.detectKind("unknown", "audio/ogg"))
        assertEquals("PDF", ChatFileTypeDetector.extractExtensionBadge("design_spec.pdf"))
        assertEquals("1.50 MB", ChatFileTypeDetector.formatFileSize(1_572_864L))
    }

    @Test
    fun `ChatKitAnimationConfig Disabled turns off every animation flag`() {
        val defaultCfg = ChatKitAnimationConfig.Default
        assertTrue(defaultCfg.shouldAnimateItemPlacement)
        assertTrue(defaultCfg.shouldAnimateContentSize)
        assertTrue(defaultCfg.shouldAnimateTransitions)
        assertTrue(defaultCfg.shouldAnimateTypingIndicator)
        assertTrue(defaultCfg.shouldAnimateVoiceWaveform)
        assertTrue(defaultCfg.shouldAnimateStatusRotation)

        val disabledCfg = ChatKitAnimationConfig.Disabled
        assertFalse(disabledCfg.shouldAnimateItemPlacement)
        assertFalse(disabledCfg.shouldAnimateContentSize)
        assertFalse(disabledCfg.shouldAnimateTransitions)
        assertFalse(disabledCfg.shouldAnimateTypingIndicator)
        assertFalse(disabledCfg.shouldAnimateVoiceWaveform)
        assertFalse(disabledCfg.shouldAnimateStatusRotation)

        val partialCfg = ChatKitAnimationConfig(enabled = true, animateVoiceWaveform = false)
        assertTrue(partialCfg.shouldAnimateItemPlacement)
        assertFalse(partialCfg.shouldAnimateVoiceWaveform)
    }

    @Test
    fun `ChatKitFeatureConfig controls attachment availability`() {
        assertTrue(ChatKitFeatureConfig.AllEnabled.hasAnyAttachmentEnabled)
        assertFalse(ChatKitFeatureConfig.TextOnly.hasAnyAttachmentEnabled)

        val pollOnly = ChatKitFeatureConfig.TextOnly.copy(enablePolls = true)
        assertTrue(pollOnly.hasAnyAttachmentEnabled)
    }

    @Test
    fun `buildTimelineItems separates SystemEvent and computes exact timeline indices`() {
        val m1 = ChatMessage("m1", "Hello", alice, Date(1_000L))
        val sys = SystemEventMessage("sys1", "Keys refreshed", alice, Date(2_000L), isSecurityEvent = true)
        val poll = PollMessage(
            id = "p1",
            question = "Theme?",
            options = listOf(PollOption("1", "Light", 3), PollOption("2", "Dark", 5)),
            user = me,
            createdAt = Date(3_000L),
        )

        val items = buildTimelineItems(listOf(m1, sys, poll), senderId = me.id)
        assertTrue(items[0] is TimelineItem.MessageItem<*>)
        assertTrue(items[1] is TimelineItem.SystemItem<*>)
        assertTrue(items[2] is TimelineItem.MessageItem<*>)
        assertTrue(items[3] is TimelineItem.DateHeader)

        val state = MessagesListState(senderId = me.id, initialMessages = listOf(m1, sys, poll))
        assertEquals(0, state.getMessageTimelineIndex("p1"))
        assertEquals(1, state.getMessageTimelineIndex("sys1"))
        assertEquals(2, state.getMessageTimelineIndex("m1"))
        assertEquals(-1, state.getMessageTimelineIndex("missing"))
    }
}

