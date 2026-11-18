package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Immutable
import java.util.Date

/**
 * Delivery, confirmation, and security status for a message.
 *
 * Includes [UNCONFIRMED] and [WARNING] to support E2E-encrypted / offline-relay
 * workflows (such as ParAnnoying Messenger's key-token confirmation model) out of the box.
 */
enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    UNCONFIRMED,
    WARNING,
    FAILED,
}

/**
 * Contract representing an individual chat message.
 *
 * Mirrors `com.stfalcon.chatkit.commons.models.IMessage` from legacy ChatKit while
 * adding optional modern defaults for delivery status, replies, reactions, and spoiler masking.
 */
interface IMessage {
    /** Unique identifier for the message. */
    val id: String

    /** Primary textual content of the message. */
    val text: String

    /** Author who sent this message. */
    val user: IUser

    /** Timestamp when the message was created or sent. */
    val createdAt: Date

    /** Delivery or verification state displayed for messages. */
    val status: MessageStatus
        get() = MessageStatus.SENT

    /** Optional label prefix displayed next to status (e.g., "NEW" for unread offline messages). */
    val statusLabel: String?
        get() = null

    /** Optional referenced message when this message is a reply. */
    val replyTo: IMessage?
        get() = null

    /** Optional map of emoji reaction -> count displayed beneath the bubble. */
    val reactions: Map<String, Int>
        get() = emptyMap()

    /** When `true`, message text is masked behind a tap-to-reveal privacy/spoiler shield. */
    val isMasked: Boolean
        get() = false
}

/**
 * Ready-to-use immutable implementation of [IMessage] supporting optional image attachments,
 * quoted replies, delivery statuses, reactions, and privacy masking.
 */
@Immutable
data class ChatMessage(
    override val id: String,
    override val text: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val imageUrl: String? = null,
    override val status: MessageStatus = MessageStatus.SENT,
    override val statusLabel: String? = null,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
    override val isMasked: Boolean = false,
    override val aspectRatio: Float? = null,
    override val additionalFilesCount: Int = 0,
    override val isAvailable: Boolean = true,
) : IMessage, MessageContentType.Image
