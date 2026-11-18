package de.hartz.software.chatkitcompose.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.theme.ChatKitDefaults
import de.hartz.software.chatkitcompose.theme.ChatKitFeatureConfig
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import de.hartz.software.chatkitcompose.theme.LocalChatKitAnimationConfig
import de.hartz.software.chatkitcompose.theme.LocalChatKitFeatureConfig
import de.hartz.software.chatkitcompose.theme.MessageInputStyle
import kotlinx.coroutines.delay

private const val TYPING_IDLE_TIMEOUT_MILLIS = 1500L

/**
 * Stateless Material 3 chat composer replacing `com.stfalcon.chatkit.messages.MessageInput`.
 *
 * Supports hoisted text drafts, an expandable attachment picker tray controlled by
 * [LocalChatKitFeatureConfig], interactive voice note recording via [VoiceRecorderState],
 * optional message chunking indicators (`maxMessageChunkSize`), and quoted reply banners.
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
    onAttachmentOptionSelected: ((AttachmentKind) -> Unit)? = null,
    onVoiceMessageRecorded: ((durationSeconds: Int, waveform: List<Float>) -> Unit)? = null,
    voiceRecorderState: VoiceRecorderState = rememberVoiceRecorderState(),
    onTypingChanged: ((Boolean) -> Unit)? = null,
    replyingTo: IMessage? = null,
    onCancelReply: (() -> Unit)? = null,
    maxLines: Int = 5,
    enabled: Boolean = true,
    sendContentDescription: String = "Send message",
    attachmentContentDescription: String = "Add attachment",
) {
    val animConfig = LocalChatKitAnimationConfig.current
    val featureConfig = LocalChatKitFeatureConfig.current

    TypingEffect(value = value, onTypingChanged = onTypingChanged)

    LaunchedEffect(voiceRecorderState.isRecording) {
        if (!voiceRecorderState.isRecording) return@LaunchedEffect
        var ticks = 0
        while (voiceRecorderState.isRecording) {
            delay(250L)
            voiceRecorderState.recordTick()
            ticks += 1
            if (ticks % 4 == 0) {
                voiceRecorderState.incrementSecond()
            }
        }
    }

    var attachmentTrayExpanded by rememberSaveable { mutableStateOf(false) }

    val canSendText = enabled && value.isNotBlank()
    val showMicButton = enabled &&
        !canSendText &&
        featureConfig.enableVoiceMessages &&
        onVoiceMessageRecorded != null

    val hasAttachmentTrigger = showAttachmentButton &&
        featureConfig.hasAnyAttachmentEnabled &&
        (onAttachmentOptionSelected != null || onAttachmentClick != null)

    val chunkCount = featureConfig.maxMessageChunkSize?.let { limit ->
        if (limit > 0 && value.length > limit) {
            ((value.length - 1) / limit) + 1
        } else {
            1
        }
    } ?: 1

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .let { if (animConfig.shouldAnimateContentSize) it.animateContentSize() else it },
        color = style.containerColor,
        tonalElevation = style.tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            if (animConfig.shouldAnimateTransitions) {
                AnimatedVisibility(visible = replyingTo != null) {
                    replyingTo?.let { replyMessage ->
                        ReplyingToBanner(
                            message = replyMessage,
                            onCancelReply = onCancelReply,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                        )
                    }
                }
            } else if (replyingTo != null) {
                ReplyingToBanner(
                    message = replyingTo,
                    onCancelReply = onCancelReply,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                )
            }

            if (animConfig.shouldAnimateTransitions) {
                AnimatedVisibility(visible = attachmentTrayExpanded && onAttachmentOptionSelected != null) {
                    AttachmentPickerTray(
                        featureConfig = featureConfig,
                        onSelect = { kind ->
                            attachmentTrayExpanded = false
                            onAttachmentOptionSelected?.invoke(kind)
                        },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            } else if (attachmentTrayExpanded && onAttachmentOptionSelected != null) {
                AttachmentPickerTray(
                    featureConfig = featureConfig,
                    onSelect = { kind ->
                        attachmentTrayExpanded = false
                        onAttachmentOptionSelected(kind)
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            if (chunkCount > 1) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 6.dp, end = 6.dp),
                ) {
                    Text(
                        text = "$chunkCount messages (${value.length} chars)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    )
                }
            }

            if (voiceRecorderState.isRecording) {
                VoiceRecordingRow(
                    state = voiceRecorderState,
                    style = style,
                    onCancel = { voiceRecorderState.cancel() },
                    onSend = {
                        val (duration, waveform) = voiceRecorderState.stop()
                        onVoiceMessageRecorded?.invoke(duration, waveform)
                    },
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (hasAttachmentTrigger) {
                        IconButton(
                            onClick = {
                                if (onAttachmentOptionSelected != null) {
                                    attachmentTrayExpanded = !attachmentTrayExpanded
                                } else {
                                    onAttachmentClick?.invoke()
                                }
                            },
                            enabled = enabled,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = if (attachmentTrayExpanded) ChatKitIcons.Close else ChatKitIcons.AttachFile,
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
                                    if (canSendText) {
                                        onSendClick()
                                    }
                                },
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    if (showMicButton) {
                        FilledIconButton(
                            onClick = {
                                attachmentTrayExpanded = false
                                voiceRecorderState.start()
                            },
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = style.sendButtonContainerColor,
                                contentColor = style.sendButtonContentColor,
                            ),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = ChatKitIcons.Mic,
                                contentDescription = "Record voice message",
                            )
                        }
                    } else {
                        FilledIconButton(
                            onClick = onSendClick,
                            enabled = canSendText,
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
    onAttachmentOptionSelected: ((AttachmentKind) -> Unit)? = null,
    onVoiceMessageRecorded: ((durationSeconds: Int, waveform: List<Float>) -> Unit)? = null,
    voiceRecorderState: VoiceRecorderState = rememberVoiceRecorderState(),
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
        onAttachmentOptionSelected = onAttachmentOptionSelected,
        onVoiceMessageRecorded = onVoiceMessageRecorded,
        voiceRecorderState = voiceRecorderState,
        onTypingChanged = onTypingChanged,
        replyingTo = replyingTo,
        onCancelReply = onCancelReply,
        maxLines = maxLines,
        enabled = enabled,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttachmentPickerTray(
    featureConfig: ChatKitFeatureConfig,
    onSelect: (AttachmentKind) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = buildList {
        if (featureConfig.enableImages) add(Triple(AttachmentKind.IMAGE, "Photo", ChatKitIcons.ImagePlaceholder))
        if (featureConfig.enableGifs) add(Triple(AttachmentKind.GIF, "GIF", ChatKitIcons.Gif))
        if (featureConfig.enableVideos) add(Triple(AttachmentKind.VIDEO, "Video", ChatKitIcons.VideoCall))
        if (featureConfig.enableFiles) add(Triple(AttachmentKind.FILE, "File", ChatKitIcons.Description))
        if (featureConfig.enablePolls) add(Triple(AttachmentKind.POLL, "Poll", ChatKitIcons.Poll))
        if (featureConfig.enableLocations) add(Triple(AttachmentKind.LOCATION, "Location", ChatKitIcons.LocationOn))
        if (featureConfig.enableContacts) add(Triple(AttachmentKind.CONTACT, "Contact", ChatKitIcons.ContactPage))
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (kind, label, icon) ->
            AttachmentOptionTile(
                label = label,
                icon = icon,
                onClick = { onSelect(kind) },
            )
        }
    }
}

@Composable
private fun AttachmentOptionTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun VoiceRecordingRow(
    state: VoiceRecorderState,
    style: MessageInputStyle,
    onCancel: () -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Recording voice message" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = ChatKitIcons.Delete,
                contentDescription = "Discard recording",
                tint = MaterialTheme.colorScheme.error,
            )
        }

        Surface(
            color = style.fieldContainerColor,
            shape = style.fieldShape,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                )
                Text(
                    text = formatDuration(state.elapsedSeconds),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    val bars = state.liveWaveform.takeLast(18)
                    bars.forEach { amp ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height((amp * 20f).coerceAtLeast(4f).dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary),
                        )
                    }
                }
            }
        }

        FilledIconButton(
            onClick = onSend,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = style.sendButtonContainerColor,
                contentColor = style.sendButtonContentColor,
            ),
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = ChatKitIcons.Send,
                contentDescription = "Send voice message",
            )
        }
    }
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
