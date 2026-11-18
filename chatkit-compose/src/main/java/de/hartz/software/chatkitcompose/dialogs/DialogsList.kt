package de.hartz.software.chatkitcompose.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.models.IDialog
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.models.MessageContentType
import de.hartz.software.chatkitcompose.theme.ChatAvatar
import de.hartz.software.chatkitcompose.theme.ChatKitDefaults
import de.hartz.software.chatkitcompose.theme.DialogsListStyle
import de.hartz.software.chatkitcompose.utils.ChatDateFormatter

/**
 * Stateless Jetpack Compose replacement for `com.stfalcon.chatkit.dialogs.DialogsList`.
 *
 * Displays conversation previews with avatars, online presence indicators, formatted timestamps,
 * last-message previews, unread count badges, and animated list reordering.
 */
@Composable
fun <M : IMessage, D : IDialog<M>> DialogsList(
    dialogs: List<D>,
    onDialogClick: (D) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onDialogLongClick: ((D) -> Unit)? = null,
    style: DialogsListStyle = ChatKitDefaults.dialogsListStyle(),
    dateFormatter: ChatDateFormatter.Formatter = ChatDateFormatter.Formatter {
        ChatDateFormatter.formatDialogTimestamp(it)
    },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    emptyContent: @Composable () -> Unit = { DefaultEmptyDialogsContent() },
    itemContent: (@Composable (dialog: D) -> Unit)? = null,
) {
    if (dialogs.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            emptyContent()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = contentPadding,
    ) {
        items(
            items = dialogs,
            key = { it.id },
            contentType = { "dialog_item" },
        ) { dialog ->
            Column(modifier = Modifier.animateItem()) {
                if (itemContent != null) {
                    itemContent(dialog)
                } else {
                    DefaultDialogRow(
                        dialog = dialog,
                        style = style,
                        dateFormatter = dateFormatter,
                        onClick = { onDialogClick(dialog) },
                        onLongClick = onDialogLongClick?.let { callback -> { callback(dialog) } },
                    )
                }

                if (style.showDivider) {
                    HorizontalDivider(
                        color = style.dividerColor,
                        modifier = Modifier.padding(start = style.avatarSize + 28.dp),
                    )
                }
            }
        }
    }
}

/**
 * Stateful overload of [DialogsList] powered by [DialogsListState] for effortless migration
 * from legacy `DialogsListAdapter`.
 */
@Composable
fun <M : IMessage, D : IDialog<M>> DialogsList(
    state: DialogsListState<M, D>,
    onDialogClick: (D) -> Unit,
    modifier: Modifier = Modifier,
    onDialogLongClick: ((D) -> Unit)? = null,
    style: DialogsListStyle = ChatKitDefaults.dialogsListStyle(),
    dateFormatter: ChatDateFormatter.Formatter = ChatDateFormatter.Formatter {
        ChatDateFormatter.formatDialogTimestamp(it)
    },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    emptyContent: @Composable () -> Unit = { DefaultEmptyDialogsContent() },
    itemContent: (@Composable (dialog: D) -> Unit)? = null,
) {
    DialogsList(
        dialogs = state.dialogs,
        onDialogClick = onDialogClick,
        modifier = modifier,
        lazyListState = state.lazyListState,
        onDialogLongClick = onDialogLongClick,
        style = style,
        dateFormatter = dateFormatter,
        contentPadding = contentPadding,
        emptyContent = emptyContent,
        itemContent = itemContent,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <M : IMessage, D : IDialog<M>> DefaultDialogRow(
    dialog: D,
    style: DialogsListStyle,
    dateFormatter: ChatDateFormatter.Formatter,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
) {
    val hasUnread = dialog.unreadCount > 0
    val backgroundColor = if (hasUnread) style.unreadItemBackgroundColor else style.itemBackgroundColor
    val isGroup = dialog.users.size > 2
    val showOnline = !isGroup && dialog.users.any { it.isOnline }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChatAvatar(
            name = dialog.dialogName,
            imageUrl = dialog.dialogPhoto ?: dialog.users.firstOrNull()?.avatar,
            size = style.avatarSize,
            isOnline = showOnline,
            onlineIndicatorColor = style.onlineIndicatorColor,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = dialog.dialogName,
                    style = if (hasUnread) {
                        style.titleTextStyle.copy(fontWeight = FontWeight.SemiBold)
                    } else {
                        style.titleTextStyle
                    },
                    color = if (hasUnread) style.unreadTitleTextColor else style.titleTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                dialog.lastMessage?.let { lastMessage ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateFormatter.format(lastMessage.createdAt),
                        style = style.dateTextStyle,
                        color = if (hasUnread) style.unreadDateTextColor else style.dateTextColor,
                        maxLines = 1,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = formatLastMessagePreview(dialog, isGroup),
                    style = if (hasUnread) {
                        style.lastMessageTextStyle.copy(fontWeight = FontWeight.Medium)
                    } else {
                        style.lastMessageTextStyle
                    },
                    color = if (hasUnread) style.unreadLastMessageTextColor else style.lastMessageTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (hasUnread) {
                    Spacer(modifier = Modifier.width(8.dp))
                    UnreadBadge(
                        count = dialog.unreadCount,
                        style = style,
                    )
                }
            }
        }
    }
}

private fun <M : IMessage> formatLastMessagePreview(
    dialog: IDialog<M>,
    isGroup: Boolean,
): String {
    val lastMessage = dialog.lastMessage ?: return "No messages yet"
    val body = when {
        lastMessage.text.isNotBlank() -> lastMessage.text
        lastMessage is MessageContentType.Image && !lastMessage.imageUrl.isNullOrBlank() -> "Photo"
        lastMessage is MessageContentType.Voice -> "Voice message"
        else -> "Attachment"
    }
    return if (isGroup) "${lastMessage.user.name}: $body" else body
}

@Composable
private fun UnreadBadge(
    count: Int,
    style: DialogsListStyle,
) {
    val label = if (count > 99) "99+" else count.toString()
    Surface(
        color = style.unreadBadgeContainerColor,
        contentColor = style.unreadBadgeContentColor,
        shape = CircleShape,
        modifier = Modifier.defaultMinSize(minWidth = 22.dp, minHeight = 22.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                text = label,
                style = style.unreadBadgeTextStyle,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DefaultEmptyDialogsContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(24.dp),
    ) {
        Text(
            text = "No conversations yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Your active chats will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

