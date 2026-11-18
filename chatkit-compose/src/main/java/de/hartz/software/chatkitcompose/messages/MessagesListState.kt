package de.hartz.software.chatkitcompose.messages

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.models.MessageContentType
import de.hartz.software.chatkitcompose.utils.ChatDateFormatter
import java.util.Date

private const val DEFAULT_GROUPING_WINDOW_MILLIS = 5 * 60 * 1000L

/**
 * Visual position of a message inside a sequence of consecutive messages from the same author.
 */
enum class MessageGroupPosition {
    SINGLE,
    FIRST,
    MIDDLE,
    LAST,
}

/**
 * Renderable item inside the chat timeline produced by [buildTimelineItems].
 */
sealed interface TimelineItem {
    val key: String

    data class DateHeader(
        val id: String,
        val date: Date,
    ) : TimelineItem {
        override val key: String = "date_header_$id"
    }

    data class SystemItem<out M : IMessage>(
        val message: M,
        val event: MessageContentType.SystemEvent,
    ) : TimelineItem {
        override val key: String = "system_${message.id}"
    }

    data class MessageItem<out M : IMessage>(
        val message: M,
        val isIncoming: Boolean,
        val groupPosition: MessageGroupPosition,
        val showAvatar: Boolean,
        val showAuthorName: Boolean,
    ) : TimelineItem {
        override val key: String = "message_${message.id}"
    }
}

/**
 * Transforms [messages] into timeline items ordered newest-first for `LazyColumn(reverseLayout = true)`,
 * automatically inserting [TimelineItem.DateHeader] entries at day boundaries, centering
 * [MessageContentType.SystemEvent] notices, and grouping consecutive bubbles from the same author.
 */
fun <M : IMessage> buildTimelineItems(
    messages: List<M>,
    senderId: String,
    showIncomingAvatar: Boolean = true,
    showIncomingAuthorName: Boolean = false,
    groupingWindowMillis: Long = DEFAULT_GROUPING_WINDOW_MILLIS,
): List<TimelineItem> {
    if (messages.isEmpty()) return emptyList()

    val newestFirst = messages.sortedByDescending { it.createdAt.time }
    val result = ArrayList<TimelineItem>(newestFirst.size + 4)

    for (index in newestFirst.indices) {
        val current = newestFirst[index]
        val newerBelow = newestFirst.getOrNull(index - 1)
        val olderAbove = newestFirst.getOrNull(index + 1)

        if (current is MessageContentType.SystemEvent) {
            result.add(TimelineItem.SystemItem(message = current, event = current))
        } else {
            val sameAsAbove = olderAbove != null &&
                olderAbove !is MessageContentType.SystemEvent &&
                canGroupMessages(
                    first = olderAbove,
                    second = current,
                    groupingWindowMillis = groupingWindowMillis,
                )
            val sameAsBelow = newerBelow != null &&
                newerBelow !is MessageContentType.SystemEvent &&
                canGroupMessages(
                    first = current,
                    second = newerBelow,
                    groupingWindowMillis = groupingWindowMillis,
                )

            val groupPosition = when {
                !sameAsAbove && !sameAsBelow -> MessageGroupPosition.SINGLE
                !sameAsAbove && sameAsBelow -> MessageGroupPosition.FIRST
                sameAsAbove && sameAsBelow -> MessageGroupPosition.MIDDLE
                else -> MessageGroupPosition.LAST
            }

            val isIncoming = current.user.id != senderId
            val showAvatar = isIncoming &&
                showIncomingAvatar &&
                (groupPosition == MessageGroupPosition.SINGLE || groupPosition == MessageGroupPosition.LAST)
            val showAuthorName = isIncoming &&
                showIncomingAuthorName &&
                (groupPosition == MessageGroupPosition.SINGLE || groupPosition == MessageGroupPosition.FIRST)

            result.add(
                TimelineItem.MessageItem(
                    message = current,
                    isIncoming = isIncoming,
                    groupPosition = groupPosition,
                    showAvatar = showAvatar,
                    showAuthorName = showAuthorName,
                ),
            )
        }

        val isDayBoundary = olderAbove == null || !ChatDateFormatter.isSameDay(current.createdAt, olderAbove.createdAt)
        if (isDayBoundary) {
            result.add(
                TimelineItem.DateHeader(
                    id = ChatDateFormatter.dayKey(current.createdAt),
                    date = current.createdAt,
                ),
            )
        }
    }

    return result
}

private fun canGroupMessages(
    first: IMessage,
    second: IMessage,
    groupingWindowMillis: Long,
): Boolean {
    if (first.user.id != second.user.id) return false
    if (!ChatDateFormatter.isSameDay(first.createdAt, second.createdAt)) return false
    return kotlin.math.abs(second.createdAt.time - first.createdAt.time) <= groupingWindowMillis
}

/**
 * Migration-friendly state holder for [MessagesList] that preserves the familiar imperative
 * operations of legacy `MessagesListAdapter` while exposing Compose snapshot state.
 */
@Stable
class MessagesListState<M : IMessage>(
    val senderId: String,
    initialMessages: List<M> = emptyList(),
    val lazyListState: LazyListState = LazyListState(),
) {
    private val _messages = mutableStateListOf<M>().apply {
        addAll(initialMessages.sortedByDescending { it.createdAt.time })
    }

    /** Current observable list of messages managed by this state holder. */
    val messages: List<M>
        get() = _messages

    /** Identifiers of currently selected messages in multi-selection mode. */
    var selectedIds: Set<String> by mutableStateOf(emptySet())
        private set

    internal var scrollToLatestTrigger by mutableIntStateOf(0)
        private set

    internal var pendingScrollTarget: Pair<String, Int>? by mutableStateOf(null)
        private set

    /** Number of currently selected messages. */
    val selectedCount: Int
        get() = selectedIds.size

    /** Whether at least one message is currently selected. */
    val isSelectionMode: Boolean
        get() = selectedIds.isNotEmpty()

    /**
     * Inserts a newly sent or received [message] at the newest end of the chat timeline,
     * mirroring `MessagesListAdapter.addToStart(message, scroll)`.
     */
    fun addToStart(message: M, scroll: Boolean = true) {
        val existingIndex = _messages.indexOfFirst { it.id == message.id }
        if (existingIndex >= 0) {
            _messages[existingIndex] = message
        } else {
            _messages.add(0, message)
        }
        if (scroll) {
            scrollToLatestTrigger++
        }
    }

    /**
     * Appends historical [messagesToAdd] (typically loaded during pagination),
     * mirroring `MessagesListAdapter.addToEnd(messages, reverse)`.
     */
    fun addToEnd(messagesToAdd: List<M>, reverse: Boolean = false) {
        val ordered = if (reverse) messagesToAdd.reversed() else messagesToAdd
        val existingIds = _messages.mapTo(HashSet(_messages.size)) { it.id }
        val uniqueNew = ordered.filter { existingIds.add(it.id) }
        if (uniqueNew.isNotEmpty()) {
            _messages.addAll(uniqueNew)
        }
    }

    /**
     * Replaces all current messages with [newMessages].
     */
    fun setMessages(newMessages: List<M>) {
        _messages.clear()
        _messages.addAll(newMessages.sortedByDescending { it.createdAt.time })
        selectedIds = emptySet()
    }

    /**
     * Updates an existing message matching `message.id`, returning `true` if found.
     */
    fun update(message: M): Boolean {
        val index = _messages.indexOfFirst { it.id == message.id }
        if (index < 0) return false
        _messages[index] = message
        return true
    }

    /**
     * Transforms an existing message with [id] using [transform], returning `true` if found.
     */
    fun updateById(id: String, transform: (M) -> M): Boolean {
        val index = _messages.indexOfFirst { it.id == id }
        if (index < 0) return false
        _messages[index] = transform(_messages[index])
        return true
    }

    /**
     * Computes the exact `LazyColumn` timeline index for [messageId] (accounting for injected
     * date headers), or `-1` if absent. Replaces manual `Date` wrapper filtering in legacy adapters.
     */
    fun getMessageTimelineIndex(
        messageId: String,
        showIncomingAvatar: Boolean = true,
        showIncomingAuthorName: Boolean = false,
    ): Int {
        val timeline = buildTimelineItems(
            messages = _messages,
            senderId = senderId,
            showIncomingAvatar = showIncomingAvatar,
            showIncomingAuthorName = showIncomingAuthorName,
        )
        return timeline.indexOfFirst { item ->
            (item is TimelineItem.MessageItem<*> && item.message.id == messageId) ||
                (item is TimelineItem.SystemItem<*> && item.message.id == messageId)
        }
    }

    /**
     * Smoothly scrolls directly to the message with [messageId], returning `true` if found.
     */
    suspend fun scrollToMessageId(messageId: String, animate: Boolean = true): Boolean {
        val index = getMessageTimelineIndex(messageId)
        if (index < 0) return false
        if (animate) {
            lazyListState.animateScrollToItem(index)
        } else {
            lazyListState.scrollToItem(index)
        }
        return true
    }

    /**
     * Queues a scroll to [messageId] handled automatically inside [MessagesList].
     */
    fun requestScrollToMessage(messageId: String) {
        val nextCounter = (pendingScrollTarget?.second ?: 0) + 1
        pendingScrollTarget = messageId to nextCounter
    }

    internal fun clearPendingScrollTarget() {
        pendingScrollTarget = null
    }

    /**
     * Deletes [message] by its ID, returning `true` if removed.
     */
    fun delete(message: M): Boolean = deleteById(message.id)

    /**
     * Deletes the message with [id], returning `true` if removed.
     */
    fun deleteById(id: String): Boolean {
        val removed = _messages.removeAll { it.id == id }
        if (removed && id in selectedIds) {
            selectedIds = selectedIds - id
        }
        return removed
    }

    /**
     * Deletes all currently selected messages and clears selection, returning the count removed.
     */
    fun deleteSelected(): Int {
        if (selectedIds.isEmpty()) return 0
        val toDelete = selectedIds
        val beforeSize = _messages.size
        _messages.removeAll { it.id in toDelete }
        selectedIds = emptySet()
        return beforeSize - _messages.size
    }

    /**
     * Clears all messages and active selections.
     */
    fun clear() {
        _messages.clear()
        selectedIds = emptySet()
    }

    /**
     * Toggles selection state for [messageId].
     */
    fun toggleSelection(messageId: String) {
        selectedIds = if (messageId in selectedIds) {
            selectedIds - messageId
        } else {
            selectedIds + messageId
        }
    }

    /**
     * Explicitly marks [messageId] as selected or deselected.
     */
    fun setSelection(messageId: String, selected: Boolean) {
        selectedIds = if (selected) {
            selectedIds + messageId
        } else {
            selectedIds - messageId
        }
    }

    /**
     * Clears all selected messages, mirroring `MessagesListAdapter.unselectAllItems()`.
     */
    fun unselectAllItems() {
        selectedIds = emptySet()
    }

    /**
     * Returns the list of currently selected message objects in timeline order.
     */
    fun getSelectedMessages(): List<M> = _messages.filter { it.id in selectedIds }
}

/**
 * Creates and remembers a [MessagesListState] instance across recompositions.
 */
@Composable
fun <M : IMessage> rememberMessagesListState(
    senderId: String,
    initialMessages: List<M> = emptyList(),
    lazyListState: LazyListState = rememberLazyListState(),
): MessagesListState<M> = remember(senderId, lazyListState) {
    MessagesListState(
        senderId = senderId,
        initialMessages = initialMessages,
        lazyListState = lazyListState,
    )
}
