package de.hartz.software.chatkitcompose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Feature flag configuration enabling granular activation of built-in rich message types,
 * composer actions, security masking, and chunking counters across ChatKit-Compose.
 */
@Immutable
data class ChatKitFeatureConfig(
    val enableImages: Boolean = true,
    val enableGifs: Boolean = true,
    val enableVideos: Boolean = true,
    val enableVoiceMessages: Boolean = true,
    val enableFiles: Boolean = true,
    val enablePolls: Boolean = true,
    val enableLocations: Boolean = true,
    val enableContacts: Boolean = true,
    val enableReplies: Boolean = true,
    val enableReactions: Boolean = true,
    val enableMultiSelection: Boolean = true,
    val enableProfileHeaderClick: Boolean = true,
    val maskMessagesByDefault: Boolean = false,
    val maxMessageChunkSize: Int? = null,
) {
    /** Whether at least one attachment picker entry should be available in `MessageInput`. */
    val hasAnyAttachmentEnabled: Boolean
        get() = enableImages ||
            enableGifs ||
            enableVideos ||
            enableFiles ||
            enablePolls ||
            enableLocations ||
            enableContacts

    companion object {
        val AllEnabled = ChatKitFeatureConfig()
        val TextOnly = ChatKitFeatureConfig(
            enableImages = false,
            enableGifs = false,
            enableVideos = false,
            enableVoiceMessages = false,
            enableFiles = false,
            enablePolls = false,
            enableLocations = false,
            enableContacts = false,
        )
    }
}

/**
 * CompositionLocal providing the active [ChatKitFeatureConfig] to ChatKit-Compose components.
 */
val LocalChatKitFeatureConfig = staticCompositionLocalOf { ChatKitFeatureConfig.AllEnabled }

/**
 * Provides [config] to nested ChatKit-Compose components.
 */
@Composable
fun ProvideChatKitFeatureConfig(
    config: ChatKitFeatureConfig,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalChatKitFeatureConfig provides config, content = content)
}

