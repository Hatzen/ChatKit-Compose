package de.hartz.software.chatkitcompose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * Styling configuration for [de.hartz.software.chatkitcompose.messages.MessagesList],
 * replacing legacy XML `MessagesList` attributes with Compose-first Material 3 tokens.
 */
@Immutable
data class MessagesListStyle(
    val incomingBubbleColor: Color,
    val incomingTextColor: Color,
    val incomingTimeColor: Color,
    val incomingAuthorNameColor: Color,
    val outgoingBubbleColor: Color,
    val outgoingTextColor: Color,
    val outgoingTimeColor: Color,
    val selectionHighlightColor: Color,
    val dateHeaderContainerColor: Color,
    val dateHeaderTextColor: Color,
    val dateHeaderTextStyle: TextStyle,
    val messageTextStyle: TextStyle,
    val timeTextStyle: TextStyle,
    val authorNameTextStyle: TextStyle,
    val replyContainerColor: Color,
    val replyAccentColor: Color,
    val statusReadTint: Color,
    val statusErrorTint: Color,
    val avatarSize: Dp,
    val showIncomingAvatar: Boolean,
    val showIncomingAuthorName: Boolean,
    val bubbleCornerRadius: Dp,
    val groupedCornerRadius: Dp,
    val maxBubbleWidthFraction: Float,
)

/**
 * Styling configuration for [de.hartz.software.chatkitcompose.dialogs.DialogsList],
 * replacing legacy XML `DialogsList` attributes.
 */
@Immutable
data class DialogsListStyle(
    val itemBackgroundColor: Color,
    val unreadItemBackgroundColor: Color,
    val titleTextColor: Color,
    val unreadTitleTextColor: Color,
    val lastMessageTextColor: Color,
    val unreadLastMessageTextColor: Color,
    val dateTextColor: Color,
    val unreadDateTextColor: Color,
    val unreadBadgeContainerColor: Color,
    val unreadBadgeContentColor: Color,
    val titleTextStyle: TextStyle,
    val lastMessageTextStyle: TextStyle,
    val dateTextStyle: TextStyle,
    val unreadBadgeTextStyle: TextStyle,
    val avatarSize: Dp,
    val onlineIndicatorColor: Color,
    val showDivider: Boolean,
    val dividerColor: Color,
)

/**
 * Styling configuration for [de.hartz.software.chatkitcompose.messages.MessageInput],
 * replacing legacy XML `MessageInput` attributes.
 */
@Immutable
data class MessageInputStyle(
    val containerColor: Color,
    val fieldContainerColor: Color,
    val fieldTextColor: Color,
    val hintTextColor: Color,
    val sendButtonContainerColor: Color,
    val sendButtonContentColor: Color,
    val disabledSendButtonContainerColor: Color,
    val disabledSendButtonContentColor: Color,
    val attachmentIconTint: Color,
    val textStyle: TextStyle,
    val fieldShape: Shape,
    val tonalElevation: Dp,
)

