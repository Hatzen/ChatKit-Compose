package de.hartz.software.chatkitcompose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Granular animation configuration for ChatKit-Compose components.
 *
 * Allows teams to tune individual animations or disable all animations globally via
 * [Disabled] (e.g., for battery saving, accessibility reduced motion, or deterministic UI tests).
 */
@Immutable
data class ChatKitAnimationConfig(
    val enabled: Boolean = true,
    val animateItemPlacement: Boolean = true,
    val animateContentSize: Boolean = true,
    val animateTransitions: Boolean = true,
    val animateTypingIndicator: Boolean = true,
    val animateVoiceWaveform: Boolean = true,
    val animateStatusRotation: Boolean = true,
) {
    val shouldAnimateItemPlacement: Boolean
        get() = enabled && animateItemPlacement

    val shouldAnimateContentSize: Boolean
        get() = enabled && animateContentSize

    val shouldAnimateTransitions: Boolean
        get() = enabled && animateTransitions

    val shouldAnimateTypingIndicator: Boolean
        get() = enabled && animateTypingIndicator

    val shouldAnimateVoiceWaveform: Boolean
        get() = enabled && animateVoiceWaveform

    val shouldAnimateStatusRotation: Boolean
        get() = enabled && animateStatusRotation

    companion object {
        val Default = ChatKitAnimationConfig()
        val Disabled = ChatKitAnimationConfig(enabled = false)
    }
}

/**
 * CompositionLocal controlling animations across `MessagesList`, `DialogsList`, `MessageInput`,
 * and `ChatProfileSection`.
 */
val LocalChatKitAnimationConfig = staticCompositionLocalOf { ChatKitAnimationConfig.Default }

/**
 * Provides [config] to nested ChatKit-Compose components.
 */
@Composable
fun ProvideChatKitAnimationConfig(
    config: ChatKitAnimationConfig,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalChatKitAnimationConfig provides config, content = content)
}

