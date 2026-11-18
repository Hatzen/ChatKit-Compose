package de.hartz.software.chatkitcompose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Default Material 3 styling values for ChatKit-Compose components.
 *
 * Follows Google's Material 3 Compose guidelines by deriving every default token from
 * [MaterialTheme.colorScheme] and [MaterialTheme.typography], supporting light/dark themes
 * and dynamic color automatically.
 */
object ChatKitDefaults {

    /**
     * Creates a [MessagesListStyle] configured with Material 3 defaults and optional overrides.
     */
    @Composable
    fun messagesListStyle(
        incomingBubbleColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        incomingTextColor: Color = MaterialTheme.colorScheme.onSurface,
        incomingTimeColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        incomingAuthorNameColor: Color = MaterialTheme.colorScheme.primary,
        outgoingBubbleColor: Color = MaterialTheme.colorScheme.primaryContainer,
        outgoingTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        outgoingTimeColor: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
        selectionHighlightColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        dateHeaderContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        dateHeaderTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        dateHeaderTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
        messageTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
        timeTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
        authorNameTextStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        replyContainerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
        replyAccentColor: Color = MaterialTheme.colorScheme.primary,
        statusReadTint: Color = MaterialTheme.colorScheme.primary,
        statusErrorTint: Color = MaterialTheme.colorScheme.error,
        avatarSize: Dp = 32.dp,
        showIncomingAvatar: Boolean = true,
        showIncomingAuthorName: Boolean = false,
        bubbleCornerRadius: Dp = 20.dp,
        groupedCornerRadius: Dp = 6.dp,
        maxBubbleWidthFraction: Float = 0.78f,
    ): MessagesListStyle = MessagesListStyle(
        incomingBubbleColor = incomingBubbleColor,
        incomingTextColor = incomingTextColor,
        incomingTimeColor = incomingTimeColor,
        incomingAuthorNameColor = incomingAuthorNameColor,
        outgoingBubbleColor = outgoingBubbleColor,
        outgoingTextColor = outgoingTextColor,
        outgoingTimeColor = outgoingTimeColor,
        selectionHighlightColor = selectionHighlightColor,
        dateHeaderContainerColor = dateHeaderContainerColor,
        dateHeaderTextColor = dateHeaderTextColor,
        dateHeaderTextStyle = dateHeaderTextStyle,
        messageTextStyle = messageTextStyle,
        timeTextStyle = timeTextStyle,
        authorNameTextStyle = authorNameTextStyle,
        replyContainerColor = replyContainerColor,
        replyAccentColor = replyAccentColor,
        statusReadTint = statusReadTint,
        statusErrorTint = statusErrorTint,
        avatarSize = avatarSize,
        showIncomingAvatar = showIncomingAvatar,
        showIncomingAuthorName = showIncomingAuthorName,
        bubbleCornerRadius = bubbleCornerRadius,
        groupedCornerRadius = groupedCornerRadius,
        maxBubbleWidthFraction = maxBubbleWidthFraction,
    )

    /**
     * Creates a [DialogsListStyle] configured with Material 3 defaults and optional overrides.
     */
    @Composable
    fun dialogsListStyle(
        itemBackgroundColor: Color = MaterialTheme.colorScheme.surface,
        unreadItemBackgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
        titleTextColor: Color = MaterialTheme.colorScheme.onSurface,
        unreadTitleTextColor: Color = MaterialTheme.colorScheme.onSurface,
        lastMessageTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unreadLastMessageTextColor: Color = MaterialTheme.colorScheme.onSurface,
        dateTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unreadDateTextColor: Color = MaterialTheme.colorScheme.primary,
        unreadBadgeContainerColor: Color = MaterialTheme.colorScheme.primary,
        unreadBadgeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        titleTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
        lastMessageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        dateTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
        unreadBadgeTextStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
        ),
        avatarSize: Dp = 52.dp,
        onlineIndicatorColor: Color = Color(0xFF2E7D32),
        showDivider: Boolean = true,
        dividerColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    ): DialogsListStyle = DialogsListStyle(
        itemBackgroundColor = itemBackgroundColor,
        unreadItemBackgroundColor = unreadItemBackgroundColor,
        titleTextColor = titleTextColor,
        unreadTitleTextColor = unreadTitleTextColor,
        lastMessageTextColor = lastMessageTextColor,
        unreadLastMessageTextColor = unreadLastMessageTextColor,
        dateTextColor = dateTextColor,
        unreadDateTextColor = unreadDateTextColor,
        unreadBadgeContainerColor = unreadBadgeContainerColor,
        unreadBadgeContentColor = unreadBadgeContentColor,
        titleTextStyle = titleTextStyle,
        lastMessageTextStyle = lastMessageTextStyle,
        dateTextStyle = dateTextStyle,
        unreadBadgeTextStyle = unreadBadgeTextStyle,
        avatarSize = avatarSize,
        onlineIndicatorColor = onlineIndicatorColor,
        showDivider = showDivider,
        dividerColor = dividerColor,
    )

    /**
     * Creates a [MessageInputStyle] configured with Material 3 defaults and optional overrides.
     */
    @Composable
    fun messageInputStyle(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        fieldContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        fieldTextColor: Color = MaterialTheme.colorScheme.onSurface,
        hintTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        sendButtonContainerColor: Color = MaterialTheme.colorScheme.primary,
        sendButtonContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        disabledSendButtonContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledSendButtonContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        attachmentIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
        fieldShape: Shape = RoundedCornerShape(24.dp),
        tonalElevation: Dp = 2.dp,
    ): MessageInputStyle = MessageInputStyle(
        containerColor = containerColor,
        fieldContainerColor = fieldContainerColor,
        fieldTextColor = fieldTextColor,
        hintTextColor = hintTextColor,
        sendButtonContainerColor = sendButtonContainerColor,
        sendButtonContentColor = sendButtonContentColor,
        disabledSendButtonContainerColor = disabledSendButtonContainerColor,
        disabledSendButtonContentColor = disabledSendButtonContentColor,
        attachmentIconTint = attachmentIconTint,
        textStyle = textStyle,
        fieldShape = fieldShape,
        tonalElevation = tonalElevation,
    )
}

