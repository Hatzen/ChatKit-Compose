package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Immutable
import java.util.Date

/**
 * Represents one selectable option inside a [MessageContentType.Poll].
 */
@Immutable
data class PollOption(
    val id: String,
    val label: String,
    val votes: Int = 0,
)

/**
 * Ready-to-use immutable GIF message.
 */
@Immutable
data class GifMessage(
    override val id: String,
    override val gifUrl: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val text: String = "",
    override val aspectRatio: Float = 4f / 3f,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Gif

/**
 * Ready-to-use immutable video attachment message.
 */
@Immutable
data class VideoMessage(
    override val id: String,
    override val videoUrl: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val text: String = "",
    override val thumbnailUrl: String? = null,
    override val durationSeconds: Int = 0,
    override val fileSizeText: String? = null,
    override val additionalFilesCount: Int = 0,
    override val isAvailable: Boolean = true,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Video

/**
 * Ready-to-use immutable voice note message.
 */
@Immutable
data class VoiceMessage(
    override val id: String,
    override val audioUrl: String,
    override val durationSeconds: Int,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val text: String = "",
    override val waveform: List<Float> = MessageContentType.DEFAULT_WAVEFORM,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Voice

/**
 * Ready-to-use immutable poll/survey message.
 */
@Immutable
data class PollMessage(
    override val id: String,
    override val question: String,
    override val options: List<PollOption>,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val text: String = question,
    override val selectedOptionIds: Set<String> = emptySet(),
    override val allowMultipleAnswers: Boolean = false,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Poll

/**
 * Ready-to-use immutable location/map message.
 */
@Immutable
data class LocationMessage(
    override val id: String,
    override val latitude: Double,
    override val longitude: Double,
    override val placeName: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val address: String? = null,
    override val text: String = placeName,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Location

/**
 * Ready-to-use immutable contact card message.
 */
@Immutable
data class ContactMessage(
    override val id: String,
    override val contactName: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val phoneNumber: String? = null,
    override val email: String? = null,
    override val organization: String? = null,
    override val contactAvatarUrl: String? = null,
    override val verificationHash: String? = null,
    override val text: String = contactName,
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Contact

/**
 * Ready-to-use immutable document / generic file message.
 */
@Immutable
data class DocumentMessage(
    override val id: String,
    override val fileName: String,
    override val filePathOrUri: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val fileSizeBytes: Long = 0L,
    override val mimeType: String? = null,
    override val isAvailable: Boolean = true,
    override val additionalFilesCount: Int = 0,
    override val text: String = "",
    override val status: MessageStatus = MessageStatus.SENT,
    override val replyTo: IMessage? = null,
    override val reactions: Map<String, Int> = emptyMap(),
) : IMessage, MessageContentType.Document

/**
 * Centered timeline system notice (e.g., key refresh, member joined, or encryption status).
 */
@Immutable
data class SystemEventMessage(
    override val id: String,
    override val eventText: String,
    override val user: IUser,
    override val createdAt: Date = Date(),
    override val isSecurityEvent: Boolean = false,
    override val text: String = eventText,
) : IMessage, MessageContentType.SystemEvent

