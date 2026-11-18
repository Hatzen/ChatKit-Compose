package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Immutable
import java.util.Date

/**
 * Delivery and read-receipt state for an outgoing message.
 */
enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED,
}

/**
 * Contract representing an individual chat message.
 *
 * Mirrors `com.stfalcon.chatkit.commons.models.IMessage` from legacy ChatKit while
 * adding optional modern defaults for delivery status, replies, and emoji reactions.
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

    /** Delivery state displayed for outgoing messages. */
    val status: MessageStatus
        get() = MessageStatus.SENT

    /** Optional referenced message when this message is a reply. */
    val replyTo: IMessage?
        get() = null

    /** Optional map of emoji reaction -> count displayed beneath the bubble. */
    val reactions: Map<String, Int>
        get() = emptyMap()
}

/**
 * Ready-to-use immutable implementation of [IMessage] supporting optional image attachments,
 * quoted replies, delivery statuses, and reactions.
 */
@Immutable
data class ChatMessage(
    override val id: String,
    override val text: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val imageUrl: String? = null,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Image

