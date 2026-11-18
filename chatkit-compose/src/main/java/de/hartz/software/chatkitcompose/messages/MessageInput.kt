package de.hartz.software.chatkitcompose.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.theme.ChatKitDefaults
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import de.hartz.software.chatkitcompose.theme.MessageInputStyle
import kotlinx.coroutines.delay

private const val TYPING_IDLE_TIMEOUT_MILLIS = 1500L

/**
 * Stateless Material 3 chat composer replacing `com.stfalcon.chatkit.messages.MessageInput`.
 *
 * Follows Google's state hoisting pattern: the caller owns [value] and updates it via
 * [onValueChange], while [onSendClick] submits the drafted message.
 */
@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Message",
    style: MessageInputStyle = ChatKitDefaults.messageInputStyle(),
    showAttachmentButton: Boolean = true,
    onAttachmentClick: (() -> Unit)? = null,
    onTypingChanged: ((Boolean) -> Unit)? = null,
    replyingTo: IMessage? = null,
    onCancelReply: (() -> Unit)? = null,
    maxLines: Int = 5,
    enabled: Boolean = true,
    sendContentDescription: String = "Send message",
    attachmentContentDescription: String = "Add attachment",
) {
    TypingEffect(value = value, onTypingChanged = onTypingChanged)

    val canSend = enabled && value.isNotBlank()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = style.containerColor,
        tonalElevation = style.tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            AnimatedVisibility(visible = replyingTo != null) {
                replyingTo?.let { replyMessage ->
                    ReplyingToBanner(
                        message = replyMessage,
                        onCancelReply = onCancelReply,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (showAttachmentButton && onAttachmentClick != null) {
                    IconButton(
                        onClick = onAttachmentClick,
                        enabled = enabled,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = ChatKitIcons.AttachFile,
                            contentDescription = attachmentContentDescription,
                            tint = style.attachmentIconTint,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .background(color = style.fieldContainerColor, shape = style.fieldShape)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = style.textStyle,
                            color = style.hintTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        textStyle = style.textStyle.copy(color = style.fieldTextColor),
                        cursorBrush = SolidColor(style.sendButtonContainerColor),
                        maxLines = maxLines,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Send,
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (canSend) {
                                    onSendClick()
                                }
                            },
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                FilledIconButton(
                    onClick = onSendClick,
                    enabled = canSend,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = style.sendButtonContainerColor,
                        contentColor = style.sendButtonContentColor,
                        disabledContainerColor = style.disabledSendButtonContainerColor,
                        disabledContentColor = style.disabledSendButtonContentColor,
                    ),
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = ChatKitIcons.Send,
                        contentDescription = sendContentDescription,
                    )
                }
            }
        }
    }
}

/**
 * Stateful convenience overload of [MessageInput] matching legacy `MessageInput.InputListener`:
 * clears the text field automatically whenever [onSendMessage] returns `true`.
 */
@Composable
fun MessageInput(
    onSendMessage: (String) -> Boolean,
    modifier: Modifier = Modifier,
    hint: String = "Message",
    style: MessageInputStyle = ChatKitDefaults.messageInputStyle(),
    showAttachmentButton: Boolean = true,
    onAttachmentClick: (() -> Unit)? = null,
    onTypingChanged: ((Boolean) -> Unit)? = null,
    replyingTo: IMessage? = null,
    onCancelReply: (() -> Unit)? = null,
    maxLines: Int = 5,
    enabled: Boolean = true,
) {
    var draft by rememberSaveable { mutableStateOf("") }

    MessageInput(
        value = draft,
        onValueChange = { draft = it },
        onSendClick = {
            val trimmed = draft.trim()
            if (trimmed.isNotEmpty() && onSendMessage(trimmed)) {
                draft = ""
            }
        },
        modifier = modifier,
        hint = hint,
        style = style,
        showAttachmentButton = showAttachmentButton,
        onAttachmentClick = onAttachmentClick,
        onTypingChanged = onTypingChanged,
        replyingTo = replyingTo,
        onCancelReply = onCancelReply,
        maxLines = maxLines,
        enabled = enabled,
    )
}

@Composable
private fun TypingEffect(
    value: String,
    onTypingChanged: ((Boolean) -> Unit)?,
) {
    if (onTypingChanged == null) return

    var isCurrentlyTyping by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            if (!isCurrentlyTyping) {
                isCurrentlyTyping = true
                onTypingChanged(true)
            }
            delay(TYPING_IDLE_TIMEOUT_MILLIS)
            if (isCurrentlyTyping) {
                isCurrentlyTyping = false
                onTypingChanged(false)
            }
        } else if (isCurrentlyTyping) {
            isCurrentlyTyping = false
            onTypingChanged(false)
        }
    }
}

@Composable
private fun ReplyingToBanner(
    message: IMessage,
    onCancelReply: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ChatKitIcons.Reply,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Replying to ${message.user.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = message.text.ifBlank { "Attachment" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (onCancelReply != null) {
                IconButton(
                    onClick = onCancelReply,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = ChatKitIcons.Close,
                        contentDescription = "Cancel reply",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

