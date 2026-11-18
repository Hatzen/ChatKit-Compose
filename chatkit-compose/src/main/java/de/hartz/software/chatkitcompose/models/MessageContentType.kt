package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Marker interface for extending [IMessage] with rich or custom message payloads.
 *
 * Mirrors `com.stfalcon.chatkit.commons.models.MessageContentType` from legacy ChatKit,
 * allowing applications to attach images, audio clips, or domain-specific widgets inside
 * `MessagesList` bubbles.
 */
interface MessageContentType : IMessage {

    /**
     * Built-in content contract for image attachments inside a message bubble.
     */
    interface Image : MessageContentType {
        /** URL or URI of the attached image, or `null` if the message has no image. */
        val imageUrl: String?
    }

    /**
     * Built-in content contract for voice or audio note messages.
     */
    interface Voice : MessageContentType {
        /** URL or URI of the audio recording. */
        val audioUrl: String

        /** Duration of the audio clip in seconds. */
        val durationSeconds: Int
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

