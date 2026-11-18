package de.hartz.software.chatkitcompose

import de.hartz.software.chatkitcompose.dialogs.DialogsListState
import de.hartz.software.chatkitcompose.messages.MessagesListState
import de.hartz.software.chatkitcompose.models.ChatDialog
import de.hartz.software.chatkitcompose.models.ChatMessage
import de.hartz.software.chatkitcompose.models.ChatUser
import de.hartz.software.chatkitcompose.models.MessageStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class MigrationModelsTest {

    private val me = ChatUser(id = "me", name = "Kai")
    private val bob = ChatUser(id = "bob", name = "Bob", isOnline = true)

    @Test
    fun `MessagesListState supports addToStart addToEnd update delete and selection`() {
        val older1 = ChatMessage("1", "Older 1", bob, Date(1_000L))
        val older2 = ChatMessage("2", "Older 2", bob, Date(2_000L))
        val newest = ChatMessage("3", "Newest", me, Date(3_000L))

        val state = MessagesListState(senderId = me.id, initialMessages = listOf(older2))
        state.addToStart(newest, scroll = true)
        state.addToEnd(listOf(older1), reverse = false)

        assertEquals(listOf("3", "2", "1"), state.messages.map { it.id })

        // Duplicate ID in addToEnd should be ignored
        state.addToEnd(listOf(older1), reverse = false)
        assertEquals(3, state.messages.size)

        // Update existing message
        assertTrue(state.updateById("3") { it.copy(status = MessageStatus.READ) })
        assertEquals(MessageStatus.READ, state.messages.first().status)

        // Selection lifecycle
        state.toggleSelection("1")
        state.toggleSelection("2")
        assertEquals(2, state.selectedCount)
        assertTrue(state.isSelectionMode)

        assertEquals(2, state.deleteSelected())
        assertEquals(0, state.selectedCount)
        assertFalse(state.isSelectionMode)
        assertEquals(listOf("3"), state.messages.map { it.id })

        assertTrue(state.deleteById("3"))
        assertTrue(state.messages.isEmpty())
    }

    @Test
    fun `DialogsListState supports upsert updateDialogWithMessage and sorting`() {
        val oldMsg = ChatMessage("m1", "Old ping", bob, Date(1_000L))
        val midMsg = ChatMessage("m2", "Mid ping", me, Date(2_000L))
        val newMsg = ChatMessage("m3", "Brand new!", bob, Date(5_000L))

        val d1 = ChatDialog(id = "d1", dialogName = "Bob", users = listOf(me, bob), lastMessage = oldMsg, unreadCount = 0)
        val d2 = ChatDialog(id = "d2", dialogName = "Team", users = listOf(me, bob), lastMessage = midMsg, unreadCount = 1)

        val state = DialogsListState<ChatMessage, ChatDialog<ChatMessage>>(listOf(d1, d2))
        state.sortByLastMessage()
        assertEquals(listOf("d2", "d1"), state.dialogs.map { it.id })

        val updated = state.updateDialogWithMessage("d1", newMsg) { dialog, msg ->
            dialog.copy(lastMessage = msg, unreadCount = dialog.unreadCount + 1)
        }
        assertTrue(updated)
        assertEquals(listOf("d1", "d2"), state.dialogs.map { it.id })
        assertEquals(1, state.dialogs.first().unreadCount)

        assertTrue(state.deleteById("d2"))
        assertEquals(listOf("d1"), state.dialogs.map { it.id })
    }
}

