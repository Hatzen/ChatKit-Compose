package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Marker interface for extending [IMessage] with rich or custom message payloads.
 *
 * Mirrors `com.stfalcon.chatkit.commons.models.MessageContentType` from legacy ChatKit while
 * eliminating ViewHolder boilerplate and reflection registration.
 */
interface MessageContentType : IMessage {

    /**
     * Built-in content contract for image attachments inside a message bubble.
     */
    interface Image : MessageContentType {
        /** URL or URI of the attached image, or `null` if the message has no image. */
        val imageUrl: String?

        /** Optional aspect ratio override for the rendered image preview. */
        val aspectRatio: Float?
            get() = null

        /** Extra bundled files indicator (e.g., `+3 files` for zipped media bundles). */
        val additionalFilesCount: Int
            get() = 0

        /** Whether the underlying image file is still available on disk or server. */
        val isAvailable: Boolean
            get() = true
    }

    /**
     * Built-in content contract for animated or static GIF messages.
     */
    interface Gif : MessageContentType {
        val gifUrl: String
        val aspectRatio: Float
            get() = 4f / 3f
    }

    /**
     * Built-in content contract for video attachments.
     */
    interface Video : MessageContentType {
        val videoUrl: String
        val thumbnailUrl: String?
            get() = null
        val durationSeconds: Int
            get() = 0
        val fileSizeText: String?
            get() = null
        val additionalFilesCount: Int
            get() = 0
        val isAvailable: Boolean
            get() = true
    }

    /**
     * Built-in content contract for voice or audio note messages.
     */
    interface Voice : MessageContentType {
        /** URL or URI of the audio recording. */
        val audioUrl: String

        /** Duration of the audio clip in seconds. */
        val durationSeconds: Int

        /** Normalized waveform bar amplitudes in `[0f..1f]`. */
        val waveform: List<Float>
            get() = DEFAULT_WAVEFORM
    }

    /**
     * Built-in content contract for interactive polls and surveys.
     */
    interface Poll : MessageContentType {
        val question: String
        val options: List<PollOption>
        val selectedOptionIds: Set<String>
            get() = emptySet()
        val allowMultipleAnswers: Boolean
            get() = false
    }

    /**
     * Built-in content contract for shared locations and map pins.
     */
    interface Location : MessageContentType {
        val latitude: Double
        val longitude: Double
        val placeName: String
        val address: String?
            get() = null
    }

    /**
     * Built-in content contract for shared contact cards (e.g., vCard / cryptographic identity).
     */
    interface Contact : MessageContentType {
        val contactName: String
        val phoneNumber: String?
            get() = null
        val email: String?
            get() = null
        val organization: String?
            get() = null
        val contactAvatarUrl: String?
            get() = null
        val verificationHash: String?
            get() = null
    }

    /**
     * Built-in content contract for downloadable documents or arbitrary file attachments.
     */
    interface Document : MessageContentType {
        val fileName: String
        val filePathOrUri: String
        val fileSizeBytes: Long
            get() = 0L
        val mimeType: String?
            get() = null
        val isAvailable: Boolean
            get() = true
        val additionalFilesCount: Int
            get() = 0
    }

    /**
     * Built-in content contract for centered timeline system or security notices
     * (e.g. key refresh/reset, member joined).
     */
    interface SystemEvent : MessageContentType {
        val eventText: String
        val isSecurityEvent: Boolean
            get() = false
    }

    companion object {
        val DEFAULT_WAVEFORM: List<Float> = listOf(
            0.25f, 0.55f, 0.85f, 0.45f, 0.90f, 0.65f, 0.35f, 0.75f,
            0.95f, 0.50f, 0.40f, 0.80f, 0.60f, 0.30f, 0.70f, 0.50f,
            0.85f, 0.40f, 0.60f, 0.30f,
        )
    }
}

/**
 * Registry mapping custom [IMessage] subtypes or predicates to Composable content renderers,
 * replacing legacy `MessageHolders.registerContentType(...)` without ViewHolder boilerplate.
 */
@Immutable
class CustomContentConfig<M : IMessage> private constructor(
    private val renderers: List<Entry<M>>,
) {
    internal data class Entry<M : IMessage>(
        val matcher: (M) -> Boolean,
        val content: @Composable (message: M, isIncoming: Boolean) -> Unit,
    )

    @Composable
    internal fun RenderIfMatched(message: M, isIncoming: Boolean): Boolean {
        val match = renderers.firstOrNull { it.matcher(message) } ?: return false
        match.content(message, isIncoming)
        return true
    }

    /**
     * Builder for registering custom Composable renderers for specialized message types.
     */
    class Builder<M : IMessage> {
        private val entries = mutableListOf<Entry<M>>()

        /**
         * Registers a composable [content] block rendered whenever [matcher] returns `true`.
         */
        fun register(
            matcher: (M) -> Boolean,
            content: @Composable (message: M, isIncoming: Boolean) -> Unit,
        ): Builder<M> = apply {
            entries.add(Entry(matcher, content))
        }

        /**
         * Registers a composable [content] block for messages implementing subtype [T].
         */
        inline fun <reified T : MessageContentType> registerType(
            noinline content: @Composable (message: T, isIncoming: Boolean) -> Unit,
        ): Builder<M> = register(
            matcher = { it is T },
            content = { message, isIncoming ->
                if (message is T) {
                    content(message, isIncoming)
                }
            },
        )

        fun build(): CustomContentConfig<M> = CustomContentConfig(entries.toList())
    }

    companion object {
        /**
         * Returns an empty [CustomContentConfig] with no custom renderers.
         */
        fun <M : IMessage> empty(): CustomContentConfig<M> = CustomContentConfig(emptyList())
    }
}
