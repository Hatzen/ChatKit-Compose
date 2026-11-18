package de.hartz.software.chatkitcompose.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.models.CustomContentConfig
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.models.IUser
import de.hartz.software.chatkitcompose.models.MessageContentType
import de.hartz.software.chatkitcompose.models.MessageStatus
import de.hartz.software.chatkitcompose.theme.ChatAvatar
import de.hartz.software.chatkitcompose.theme.ChatKitDefaults
import de.hartz.software.chatkitcompose.theme.ChatKitFeatureConfig
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import de.hartz.software.chatkitcompose.theme.LocalChatKitAnimationConfig
import de.hartz.software.chatkitcompose.theme.LocalChatKitFeatureConfig
import de.hartz.software.chatkitcompose.theme.MessagesListStyle
import de.hartz.software.chatkitcompose.utils.ChatDateFormatter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

/**
 * Stateless Jetpack Compose replacement for `com.stfalcon.chatkit.messages.MessagesList`.
 *
 * Displays incoming and outgoing bubbles with automatic calendar date headers, consecutive bubble
 * grouping, all built-in rich content types (Image, GIF, Video, Voice, Poll, Location, Contact,
 * Document, SystemEvent), typing indicators, privacy spoiler masking, reactions, delivery states,
 * multi-selection, and infinite-scroll pagination.
 */
@Composable
fun <M : IMessage> MessagesList(
    messages: List<M>,
    senderId: String,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    selectedMessageIds: Set<String> = emptySet(),
    typingUsers: List<IUser> = emptyList(),
    style: MessagesListStyle = ChatKitDefaults.messagesListStyle(),
    dateFormatter: ChatDateFormatter.Formatter = ChatDateFormatter.Formatter {
        ChatDateFormatter.formatChatDateHeader(it)
    },
    timeFormatter: ChatDateFormatter.Formatter = ChatDateFormatter.Formatter {
        ChatDateFormatter.format(it, ChatDateFormatter.Template.TIME)
    },
    customContentConfig: CustomContentConfig<M> = CustomContentConfig.empty(),
    authorNameFormatter: ((M) -> String)? = null,
    onMessageClick: ((M) -> Unit)? = null,
    onMessageLongClick: ((M) -> Unit)? = null,
    onAvatarClick: ((IUser) -> Unit)? = null,
    onPollVote: ((M, String) -> Unit)? = null,
    onMediaClick: ((M) -> Unit)? = null,
    onLocationClick: ((MessageContentType.Location) -> Unit)? = null,
    onContactClick: ((MessageContentType.Contact) -> Unit)? = null,
    onDocumentClick: ((MessageContentType.Document) -> Unit)? = null,
    onLoadMore: ((page: Int, totalItemsCount: Int) -> Unit)? = null,
    loadMoreThreshold: Int = 5,
    isLoadingMore: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    emptyContent: @Composable () -> Unit = { DefaultEmptyMessagesContent() },
) {
    val animConfig = LocalChatKitAnimationConfig.current
    val featureConfig = LocalChatKitFeatureConfig.current

    val timelineItems by remember(
        messages,
        senderId,
        style.showIncomingAvatar,
        style.showIncomingAuthorName,
    ) {
        derivedStateOf {
            buildTimelineItems(
                messages = messages,
                senderId = senderId,
                showIncomingAvatar = style.showIncomingAvatar,
                showIncomingAuthorName = style.showIncomingAuthorName,
            )
        }
    }

    PaginationEffect(
        lazyListState = lazyListState,
        totalTimelineItems = timelineItems.size,
        totalMessagesCount = messages.size,
        loadMoreThreshold = loadMoreThreshold,
        isLoadingMore = isLoadingMore,
        onLoadMore = onLoadMore,
    )

    if (timelineItems.isEmpty() && typingUsers.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            emptyContent()
        }
        return
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val maxBubbleWidth = (screenWidthDp * style.maxBubbleWidthFraction).coerceAtLeast(180.dp)
    val itemPlacementModifier: Modifier = Modifier

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        reverseLayout = true,
        contentPadding = contentPadding,
    ) {
        if (typingUsers.isNotEmpty()) {
            item(key = "typing_indicator", contentType = "typing_indicator") {
                TypingIndicatorBubble(
                    typingUsers = typingUsers,
                    style = style,
                    modifier = if (animConfig.shouldAnimateItemPlacement) itemPlacementModifier.animateItem() else itemPlacementModifier,
                )
            }
        }

        items(
            items = timelineItems,
            key = { it.key },
            contentType = { item ->
                when (item) {
                    is TimelineItem.DateHeader -> "date_header"
                    is TimelineItem.SystemItem<*> -> "system_event"
                    is TimelineItem.MessageItem<*> -> if (item.isIncoming) "incoming_msg" else "outgoing_msg"
                }
            },
        ) { item ->
            val animModifier = if (animConfig.shouldAnimateItemPlacement) {
                Modifier.animateItem()
            } else {
                Modifier
            }
            when (item) {
                is TimelineItem.DateHeader -> {
                    DateHeaderChip(
                        text = dateFormatter.format(item.date),
                        style = style,
                        modifier = animModifier,
                    )
                }

                is TimelineItem.SystemItem<*> -> {
                    SystemEventChip(
                        event = item.event,
                        style = style,
                        modifier = animModifier,
                    )
                }

                is TimelineItem.MessageItem<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val typedMessage = item.message as M
                    MessageBubbleRow(
                        message = typedMessage,
                        isIncoming = item.isIncoming,
                        groupPosition = item.groupPosition,
                        showAvatar = item.showAvatar,
                        showAuthorName = item.showAuthorName,
                        isSelected = typedMessage.id in selectedMessageIds,
                        maxBubbleWidth = maxBubbleWidth,
                        style = style,
                        featureConfig = featureConfig,
                        formattedTime = timeFormatter.format(typedMessage.createdAt),
                        customContentConfig = customContentConfig,
                        authorNameFormatter = authorNameFormatter,
                        onClick = onMessageClick?.let { callback -> { callback(typedMessage) } },
                        onLongClick = onMessageLongClick?.let { callback -> { callback(typedMessage) } },
                        onAvatarClick = onAvatarClick,
                        onPollVote = onPollVote?.let { callback -> { optId -> callback(typedMessage, optId) } },
                        onMediaClick = onMediaClick?.let { callback -> { callback(typedMessage) } },
                        onLocationClick = onLocationClick,
                        onContactClick = onContactClick,
                        onDocumentClick = onDocumentClick,
                        modifier = animModifier,
                    )
                }
            }
        }

        if (isLoadingMore) {
            item(key = "loading_more_indicator", contentType = "loading_indicator") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

/**
 * Stateful overload of [MessagesList] powered by [MessagesListState] for effortless migration
 * from legacy `MessagesListAdapter`.
 */
@Composable
fun <M : IMessage> MessagesList(
    state: MessagesListState<M>,
    modifier: Modifier = Modifier,
    typingUsers: List<IUser> = emptyList(),
    style: MessagesListStyle = ChatKitDefaults.messagesListStyle(),
    dateFormatter: ChatDateFormatter.Formatter = ChatDateFormatter.Formatter {
        ChatDateFormatter.formatChatDateHeader(it)
    },
    timeFormatter: ChatDateFormatter.Formatter = ChatDateFormatter.Formatter {
        ChatDateFormatter.format(it, ChatDateFormatter.Template.TIME)
    },
    customContentConfig: CustomContentConfig<M> = CustomContentConfig.empty(),
    authorNameFormatter: ((M) -> String)? = null,
    onMessageClick: ((M) -> Unit)? = null,
    onMessageLongClick: ((M) -> Unit)? = null,
    onSelectionChanged: ((Int) -> Unit)? = null,
    onAvatarClick: ((IUser) -> Unit)? = null,
    onPollVote: ((M, String) -> Unit)? = null,
    onMediaClick: ((M) -> Unit)? = null,
    onLocationClick: ((MessageContentType.Location) -> Unit)? = null,
    onContactClick: ((MessageContentType.Contact) -> Unit)? = null,
    onDocumentClick: ((MessageContentType.Document) -> Unit)? = null,
    onLoadMore: ((page: Int, totalItemsCount: Int) -> Unit)? = null,
    loadMoreThreshold: Int = 5,
    isLoadingMore: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    emptyContent: @Composable () -> Unit = { DefaultEmptyMessagesContent() },
) {
    val animConfig = LocalChatKitAnimationConfig.current
    val featureConfig = LocalChatKitFeatureConfig.current

    LaunchedEffect(state.scrollToLatestTrigger) {
        if (state.scrollToLatestTrigger > 0 && state.messages.isNotEmpty()) {
            if (animConfig.shouldAnimateTransitions) {
                state.lazyListState.animateScrollToItem(0)
            } else {
                state.lazyListState.scrollToItem(0)
            }
        }
    }

    LaunchedEffect(state.pendingScrollTarget) {
        val target = state.pendingScrollTarget ?: return@LaunchedEffect
        state.scrollToMessageId(target.first, animate = animConfig.shouldAnimateTransitions)
        state.clearPendingScrollTarget()
    }

    LaunchedEffect(state.selectedCount, onSelectionChanged) {
        onSelectionChanged?.invoke(state.selectedCount)
    }

    val selectionEnabled = featureConfig.enableMultiSelection && (onSelectionChanged != null || onMessageLongClick != null)

    val effectiveClick: ((M) -> Unit)? = if (onSelectionChanged != null || onMessageClick != null) {
        { message ->
            if (featureConfig.enableMultiSelection && onSelectionChanged != null && state.isSelectionMode) {
                state.toggleSelection(message.id)
            } else {
                onMessageClick?.invoke(message)
            }
        }
    } else {
        null
    }

    val effectiveLongClick: ((M) -> Unit)? = if (selectionEnabled) {
        { message ->
            if (featureConfig.enableMultiSelection && onSelectionChanged != null) {
                state.toggleSelection(message.id)
            }
            onMessageLongClick?.invoke(message)
        }
    } else {
        null
    }

    MessagesList(
        messages = state.messages,
        senderId = state.senderId,
        modifier = modifier,
        lazyListState = state.lazyListState,
        selectedMessageIds = state.selectedIds,
        typingUsers = typingUsers,
        style = style,
        dateFormatter = dateFormatter,
        timeFormatter = timeFormatter,
        customContentConfig = customContentConfig,
        authorNameFormatter = authorNameFormatter,
        onMessageClick = effectiveClick,
        onMessageLongClick = effectiveLongClick,
        onAvatarClick = onAvatarClick,
        onPollVote = onPollVote,
        onMediaClick = onMediaClick,
        onLocationClick = onLocationClick,
        onContactClick = onContactClick,
        onDocumentClick = onDocumentClick,
        onLoadMore = onLoadMore,
        loadMoreThreshold = loadMoreThreshold,
        isLoadingMore = isLoadingMore,
        contentPadding = contentPadding,
        emptyContent = emptyContent,
    )
}

@Composable
private fun PaginationEffect(
    lazyListState: LazyListState,
    totalTimelineItems: Int,
    totalMessagesCount: Int,
    loadMoreThreshold: Int,
    isLoadingMore: Boolean,
    onLoadMore: ((page: Int, totalItemsCount: Int) -> Unit)?,
) {
    if (onLoadMore == null) return

    var currentPage by remember { mutableIntStateOf(0) }
    var lastTriggeredCount by remember { mutableIntStateOf(-1) }

    LaunchedEffect(lazyListState, totalTimelineItems, totalMessagesCount, isLoadingMore) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                val nearTop = totalTimelineItems > 0 &&
                    lastVisibleIndex >= (totalTimelineItems - 1 - loadMoreThreshold).coerceAtLeast(0)
                if (nearTop && !isLoadingMore && totalMessagesCount > lastTriggeredCount) {
                    lastTriggeredCount = totalMessagesCount
                    currentPage += 1
                    onLoadMore(currentPage, totalMessagesCount)
                }
            }
    }
}

@Composable
private fun DateHeaderChip(
    text: String,
    style: MessagesListStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = style.dateHeaderContainerColor,
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = text,
                style = style.dateHeaderTextStyle,
                color = style.dateHeaderTextColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <M : IMessage> MessageBubbleRow(
    message: M,
    isIncoming: Boolean,
    groupPosition: MessageGroupPosition,
    showAvatar: Boolean,
    showAuthorName: Boolean,
    isSelected: Boolean,
    maxBubbleWidth: Dp,
    style: MessagesListStyle,
    featureConfig: ChatKitFeatureConfig,
    formattedTime: String,
    customContentConfig: CustomContentConfig<M>,
    authorNameFormatter: ((M) -> String)?,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onAvatarClick: ((IUser) -> Unit)?,
    onPollVote: ((String) -> Unit)?,
    onMediaClick: (() -> Unit)?,
    onLocationClick: ((MessageContentType.Location) -> Unit)?,
    onContactClick: ((MessageContentType.Contact) -> Unit)?,
    onDocumentClick: ((MessageContentType.Document) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val animConfig = LocalChatKitAnimationConfig.current
    val topPadding = when (groupPosition) {
        MessageGroupPosition.SINGLE, MessageGroupPosition.FIRST -> 6.dp
        MessageGroupPosition.MIDDLE, MessageGroupPosition.LAST -> 2.dp
    }

    val rowModifier = modifier
        .fillMaxWidth()
        .background(if (isSelected) style.selectionHighlightColor else Color.Transparent)
        .semantics { selected = isSelected }
        .let { base ->
            if (onClick != null || onLongClick != null) {
                base.combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = onLongClick,
                )
            } else {
                base
            }
        }
        .padding(top = topPadding, bottom = 2.dp, start = 4.dp, end = 4.dp)

    Row(
        modifier = rowModifier,
        horizontalArrangement = if (isIncoming) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (isIncoming && style.showIncomingAvatar) {
            if (showAvatar) {
                ChatAvatar(
                    name = message.user.name,
                    imageUrl = message.user.avatar,
                    size = style.avatarSize,
                    isOnline = message.user.isOnline,
                    modifier = if (onAvatarClick != null) {
                        Modifier
                            .clip(RoundedCornerShape(style.avatarSize))
                            .clickable { onAvatarClick(message.user) }
                    } else {
                        Modifier
                    },
                )
            } else {
                Spacer(modifier = Modifier.width(style.avatarSize))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isIncoming) Alignment.Start else Alignment.End,
            modifier = Modifier.widthIn(max = maxBubbleWidth),
        ) {
            val displayAuthorName = authorNameFormatter?.invoke(message) ?: message.user.name
            if (animConfig.shouldAnimateTransitions) {
                AnimatedVisibility(visible = showAuthorName) {
                    Text(
                        text = displayAuthorName,
                        style = style.authorNameTextStyle,
                        color = style.incomingAuthorNameColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 12.dp, bottom = 2.dp),
                    )
                }
            } else if (showAuthorName) {
                Text(
                    text = displayAuthorName,
                    style = style.authorNameTextStyle,
                    color = style.incomingAuthorNameColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 12.dp, bottom = 2.dp),
                )
            }

            MessageBubble(
                message = message,
                isIncoming = isIncoming,
                groupPosition = groupPosition,
                style = style,
                featureConfig = featureConfig,
                formattedTime = formattedTime,
                customContentConfig = customContentConfig,
                onPollVote = onPollVote,
                onMediaClick = onMediaClick,
                onLocationClick = onLocationClick,
                onContactClick = onContactClick,
                onDocumentClick = onDocumentClick,
            )

            if (featureConfig.enableReactions && message.reactions.isNotEmpty()) {
                ReactionsBar(
                    reactions = message.reactions,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun <M : IMessage> MessageBubble(
    message: M,
    isIncoming: Boolean,
    groupPosition: MessageGroupPosition,
    style: MessagesListStyle,
    featureConfig: ChatKitFeatureConfig,
    formattedTime: String,
    customContentConfig: CustomContentConfig<M>,
    onPollVote: ((String) -> Unit)?,
    onMediaClick: (() -> Unit)?,
    onLocationClick: ((MessageContentType.Location) -> Unit)?,
    onContactClick: ((MessageContentType.Contact) -> Unit)?,
    onDocumentClick: ((MessageContentType.Document) -> Unit)?,
) {
    val animConfig = LocalChatKitAnimationConfig.current
    val containerColor = if (isIncoming) style.incomingBubbleColor else style.outgoingBubbleColor
    val textColor = if (isIncoming) style.incomingTextColor else style.outgoingTextColor
    val timeColor = if (isIncoming) style.incomingTimeColor else style.outgoingTimeColor
    val bubbleShape = bubbleShapeFor(isIncoming, groupPosition, style)
    val accentColor = if (isIncoming) MaterialTheme.colorScheme.primary else textColor

    val shouldMask = message.isMasked || featureConfig.maskMessagesByDefault
    var isRevealed by remember(message.id, shouldMask) { mutableStateOf(!shouldMask) }

    Surface(
        color = containerColor,
        contentColor = textColor,
        shape = bubbleShape,
        tonalElevation = 1.dp,
        modifier = if (animConfig.shouldAnimateContentSize) Modifier.animateContentSize() else Modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            if (featureConfig.enableReplies) {
                message.replyTo?.let { replied ->
                    ReplyPreviewBlock(
                        replyTo = replied,
                        style = style,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
            }

            if (shouldMask && !isRevealed) {
                MaskedMessageShield(
                    textColor = textColor,
                    onReveal = { isRevealed = true },
                )
            } else {
                val handledByCustom = customContentConfig.RenderIfMatched(message, isIncoming)
                if (!handledByCustom) {
                    RenderBuiltInMessageContent(
                        message = message,
                        featureConfig = featureConfig,
                        style = style,
                        textColor = textColor,
                        accentColor = accentColor,
                        onPollVote = onPollVote,
                        onMediaClick = onMediaClick,
                        onLocationClick = onLocationClick,
                        onContactClick = onContactClick,
                        onDocumentClick = onDocumentClick,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (!message.statusLabel.isNullOrBlank()) {
                    Text(
                        text = message.statusLabel!!,
                        style = style.timeTextStyle.copy(fontWeight = FontWeight.Bold),
                        color = style.statusReadTint,
                    )
                }
                Text(
                    text = formattedTime,
                    style = style.timeTextStyle,
                    color = timeColor,
                )
                if (!isIncoming || message.status == MessageStatus.UNCONFIRMED || message.status == MessageStatus.WARNING) {
                    MessageStatusIcon(
                        status = message.status,
                        defaultTint = timeColor,
                        readTint = style.statusReadTint,
                        errorTint = style.statusErrorTint,
                    )
                }
            }
        }
    }
}

@Composable
private fun <M : IMessage> RenderBuiltInMessageContent(
    message: M,
    featureConfig: ChatKitFeatureConfig,
    style: MessagesListStyle,
    textColor: Color,
    accentColor: Color,
    onPollVote: ((String) -> Unit)?,
    onMediaClick: (() -> Unit)?,
    onLocationClick: ((MessageContentType.Location) -> Unit)?,
    onContactClick: ((MessageContentType.Contact) -> Unit)?,
    onDocumentClick: ((MessageContentType.Document) -> Unit)?,
) {
    var renderedRichBlock = false

    if (message is MessageContentType.Gif && featureConfig.enableGifs) {
        GifAttachmentContent(gif = message, onClick = onMediaClick)
        renderedRichBlock = true
    } else if (message is MessageContentType.Video && featureConfig.enableVideos) {
        VideoAttachmentContent(video = message, onClick = onMediaClick)
        renderedRichBlock = true
    } else if (message is MessageContentType.Image && !message.imageUrl.isNullOrBlank() && featureConfig.enableImages) {
        ImageAttachmentContent(
            image = message,
            description = message.text.ifBlank { "Image attachment" },
            onClick = onMediaClick,
        )
        renderedRichBlock = true
    } else if (message is MessageContentType.Voice && featureConfig.enableVoiceMessages) {
        VoiceMessageContent(
            voice = message,
            textColor = textColor,
            accentColor = accentColor,
        )
        renderedRichBlock = true
    } else if (message is MessageContentType.Poll && featureConfig.enablePolls) {
        PollMessageContent(
            poll = message,
            textColor = textColor,
            accentColor = accentColor,
            onVoteOption = onPollVote,
        )
        return
    } else if (message is MessageContentType.Location && featureConfig.enableLocations) {
        LocationMessageContent(
            location = message,
            textColor = textColor,
            accentColor = accentColor,
            onClick = onLocationClick?.let { callback -> { callback(message) } },
        )
        return
    } else if (message is MessageContentType.Contact && featureConfig.enableContacts) {
        ContactMessageContent(
            contact = message,
            textColor = textColor,
            accentColor = accentColor,
            onClick = onContactClick?.let { callback -> { callback(message) } },
        )
        return
    } else if (message is MessageContentType.Document && featureConfig.enableFiles) {
        DocumentMessageContent(
            document = message,
            textColor = textColor,
            accentColor = accentColor,
            onClick = onDocumentClick?.let { callback -> { callback(message) } },
        )
        renderedRichBlock = true
    }

    if (message.text.isNotBlank()) {
        if (renderedRichBlock) {
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            text = message.text,
            style = style.messageTextStyle,
            color = textColor,
        )
    } else if (!renderedRichBlock) {
        Text(
            text = "Attachment hidden by configuration",
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun MaskedMessageShield(
    textColor: Color,
    onReveal: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(textColor.copy(alpha = 0.10f))
            .clickable(onClick = onReveal)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = ChatKitIcons.VisibilityOff,
            contentDescription = "Hidden message",
            tint = textColor.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Hidden message • Tap to reveal",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor.copy(alpha = 0.85f),
        )
    }
}

@Composable
private fun ReplyPreviewBlock(
    replyTo: IMessage,
    style: MessagesListStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(style.replyContainerColor)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(style.replyAccentColor),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = replyTo.user.name,
                style = style.authorNameTextStyle,
                color = style.replyAccentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = replyTo.text.ifBlank { "Attachment" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MessageStatusIcon(
    status: MessageStatus,
    defaultTint: Color,
    readTint: Color,
    errorTint: Color,
) {
    val rotateSending = status == MessageStatus.SENDING &&
        LocalChatKitAnimationConfig.current.shouldAnimateStatusRotation

    val rotationAngle = if (rotateSending) {
        val transition = rememberInfiniteTransition(label = "sendingRotate")
        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "sendingAngle",
        )
        angle
    } else {
        0f
    }

    val (icon, tint, description) = when (status) {
        MessageStatus.SENDING -> Triple(ChatKitIcons.Schedule, defaultTint, "Sending")
        MessageStatus.SENT -> Triple(ChatKitIcons.Check, defaultTint, "Sent")
        MessageStatus.DELIVERED -> Triple(ChatKitIcons.DoneAll, defaultTint, "Delivered")
        MessageStatus.READ -> Triple(ChatKitIcons.DoneAll, readTint, "Read")
        MessageStatus.UNCONFIRMED -> Triple(ChatKitIcons.HelpOutline, defaultTint, "Awaiting confirmation")
        MessageStatus.WARNING -> Triple(ChatKitIcons.Warning, errorTint, "Security warning")
        MessageStatus.FAILED -> Triple(ChatKitIcons.ErrorOutline, errorTint, "Failed to send")
    }
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = tint,
        modifier = Modifier
            .size(14.dp)
            .rotate(rotationAngle),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReactionsBar(
    reactions: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        reactions.forEach { (emoji, count) ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = if (count > 1) "$emoji $count" else emoji,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
        }
    }
}

private fun bubbleShapeFor(
    isIncoming: Boolean,
    groupPosition: MessageGroupPosition,
    style: MessagesListStyle,
): Shape {
    val large = style.bubbleCornerRadius
    val small = style.groupedCornerRadius
    return if (isIncoming) {
        when (groupPosition) {
            MessageGroupPosition.SINGLE -> RoundedCornerShape(large)
            MessageGroupPosition.FIRST -> RoundedCornerShape(
                topStart = large,
                topEnd = large,
                bottomEnd = large,
                bottomStart = small,
            )
            MessageGroupPosition.MIDDLE -> RoundedCornerShape(
                topStart = small,
                topEnd = large,
                bottomEnd = large,
                bottomStart = small,
            )
            MessageGroupPosition.LAST -> RoundedCornerShape(
                topStart = small,
                topEnd = large,
                bottomEnd = large,
                bottomStart = large,
            )
        }
    } else {
        when (groupPosition) {
            MessageGroupPosition.SINGLE -> RoundedCornerShape(large)
            MessageGroupPosition.FIRST -> RoundedCornerShape(
                topStart = large,
                topEnd = large,
                bottomEnd = small,
                bottomStart = large,
            )
            MessageGroupPosition.MIDDLE -> RoundedCornerShape(
                topStart = large,
                topEnd = small,
                bottomEnd = small,
                bottomStart = large,
            )
            MessageGroupPosition.LAST -> RoundedCornerShape(
                topStart = large,
                topEnd = small,
                bottomEnd = large,
                bottomStart = large,
            )
        }
    }
}

@Composable
private fun DefaultEmptyMessagesContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(24.dp),
    ) {
        Text(
            text = "No messages yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Send a message to start the conversation.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
