package de.hartz.software.chatkitcompose.sample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.dialogs.DialogsList
import de.hartz.software.chatkitcompose.dialogs.rememberDialogsListState
import de.hartz.software.chatkitcompose.messages.MessageInput
import de.hartz.software.chatkitcompose.messages.MessagesList
import de.hartz.software.chatkitcompose.messages.rememberMessagesListState
import de.hartz.software.chatkitcompose.models.ChatDialog
import de.hartz.software.chatkitcompose.models.ChatMessage
import de.hartz.software.chatkitcompose.models.ChatUser
import de.hartz.software.chatkitcompose.models.CustomContentConfig
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.models.IUser
import de.hartz.software.chatkitcompose.models.MessageContentType
import de.hartz.software.chatkitcompose.models.MessageStatus
import de.hartz.software.chatkitcompose.theme.ChatAvatar
import de.hartz.software.chatkitcompose.theme.ChatKitDefaults
import de.hartz.software.chatkitcompose.theme.ChatKitImageLoader
import de.hartz.software.chatkitcompose.theme.ProvideChatKitImageLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
                ChatKitSampleApp()
            }
        }
    }
}

/**
 * Custom [MessageContentType] example demonstrating how easily domain-specific message layouts
 * plug into [MessagesList] without RecyclerView ViewHolder boilerplate.
 */
@Immutable
data class PollMessage(
    override val id: String,
    override val text: String,
    override val user: IUser,
    override val createdAt: Date,
    val question: String,
    val options: List<String>,
) : IMessage, MessageContentType

@Composable
fun ChatKitSampleApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser = remember { ChatUser(id = "me", name = "Kai", isOnline = true) }
    val alice = remember { ChatUser(id = "alice", name = "Alice Meyer", isOnline = true) }
    val bob = remember { ChatUser(id = "bob", name = "Bob Tanaka", isOnline = false) }
    val carol = remember { ChatUser(id = "carol", name = "Carol Vance", isOnline = true) }

    val dialogsState = rememberDialogsListState<IMessage, ChatDialog<IMessage>>(
        initialDialogs = remember { sampleDialogs(currentUser, alice, bob, carol) },
    )
    var activeDialog by remember { mutableStateOf<ChatDialog<IMessage>?>(null) }
    var incomingSimCount by remember { mutableIntStateOf(0) }

    val demoImageLoader = remember {
        ChatKitImageLoader { url, contentDescription, modifier ->
            Box(
                modifier = modifier.background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1565C0), Color(0xFF26A69A)),
                    ),
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = contentDescription ?: (url ?: "Image"),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }

    ProvideChatKitImageLoader(demoImageLoader) {
        val currentDialog = activeDialog
        if (currentDialog == null) {
            DialogsScreen(
                dialogs = dialogsState.dialogs,
                onDialogClick = { clicked ->
                    dialogsState.upsertItem(clicked.copy(unreadCount = 0), sortByNewest = false)
                    activeDialog = clicked.copy(unreadCount = 0)
                },
                onSimulateIncoming = {
                    incomingSimCount += 1
                    val incoming = ChatMessage(
                        id = "sim_${UUID.randomUUID()}",
                        text = "New message #$incomingSimCount from Bob via Compose!",
                        user = bob,
                        createdAt = Date(),
                    )
                    dialogsState.updateDialogWithMessage("dialog_bob", incoming) { dialog, msg ->
                        dialog.copy(lastMessage = msg, unreadCount = dialog.unreadCount + 1)
                    }
                },
            )
        } else {
            BackHandler { activeDialog = null }
            ChatThreadScreen(
                dialog = currentDialog,
                currentUser = currentUser,
                onBack = { activeDialog = null },
                onLatestMessageChanged = { latest ->
                    dialogsState.updateDialogWithMessage(currentDialog.id, latest) { dialog, msg ->
                        dialog.copy(lastMessage = msg, unreadCount = 0)
                    }
                },
                onShowToast = { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                onCopyText = { text ->
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Copied message", text))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                simulateReply = { replyText, onReplyReady ->
                    scope.launch {
                        delay(900L)
                        onReplyReady(
                            ChatMessage(
                                id = "reply_${UUID.randomUUID()}",
                                text = "Got \"$replyText\" — Jetpack Compose makes this effortless!",
                                user = currentDialog.users.firstOrNull { it.id != currentUser.id } ?: alice,
                                createdAt = Date(),
                            ),
                        )
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogsScreen(
    dialogs: List<ChatDialog<IMessage>>,
    onDialogClick: (ChatDialog<IMessage>) -> Unit,
    onSimulateIncoming: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChatKit-Compose") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSimulateIncoming) {
                Text("+Ping", modifier = Modifier.padding(horizontal = 12.dp))
            }
        },
    ) { padding ->
        DialogsList(
            dialogs = dialogs,
            onDialogClick = onDialogClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatThreadScreen(
    dialog: ChatDialog<IMessage>,
    currentUser: ChatUser,
    onBack: () -> Unit,
    onLatestMessageChanged: (IMessage) -> Unit,
    onShowToast: (String) -> Unit,
    onCopyText: (String) -> Unit,
    simulateReply: (String, (IMessage) -> Unit) -> Unit,
) {
    val messagesState = rememberMessagesListState(
        senderId = currentUser.id,
        initialMessages = remember(dialog.id) { sampleThreadMessages(dialog, currentUser) },
    )
    var selectionCount by remember { mutableIntStateOf(0) }
    var replyingTo by remember { mutableStateOf<IMessage?>(null) }
    var peerTyping by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var loadedPages by remember { mutableIntStateOf(0) }
    val pollSelections = remember { mutableStateMapOf<String, Int>() }
    val scope = rememberCoroutineScope()

    val customContentConfig = remember {
        CustomContentConfig.Builder<IMessage>()
            .registerType<PollMessage> { poll, _ ->
                val selectedOption = pollSelections[poll.id]
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = poll.question,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                    poll.options.forEachIndexed { idx, option ->
                        val isPicked = selectedOption == idx
                        Surface(
                            color = if (isPicked) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (isPicked) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { pollSelections[poll.id] = idx },
                        ) {
                            Text(
                                text = if (isPicked) "✓ $option" else option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }
            .build()
    }

    val isGroup = dialog.users.size > 2
    val style = ChatKitDefaults.messagesListStyle(
        showIncomingAuthorName = isGroup,
    )

    Scaffold(
        topBar = {
            if (selectionCount > 0) {
                TopAppBar(
                    navigationIcon = {
                        TextButton(onClick = { messagesState.unselectAllItems() }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("$selectionCount selected") },
                    actions = {
                        TextButton(
                            onClick = {
                                val text = messagesState.getSelectedMessages()
                                    .joinToString("\n") { it.text }
                                onCopyText(text)
                                messagesState.unselectAllItems()
                            },
                        ) {
                            Text("Copy")
                        }
                        TextButton(
                            onClick = {
                                messagesState.deleteSelected()
                            },
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                )
            } else {
                TopAppBar(
                    navigationIcon = {
                        TextButton(onClick = onBack) {
                            Text("Back")
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ChatAvatar(
                                name = dialog.dialogName,
                                imageUrl = dialog.dialogPhoto,
                                size = 36.dp,
                                isOnline = !isGroup && dialog.users.any { it.id != currentUser.id && it.isOnline },
                            )
                            Column {
                                Text(
                                    text = dialog.dialogName,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                if (peerTyping) {
                                    Text(
                                        text = "typing...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    },
                )
            }
        },
        bottomBar = {
            MessageInput(
                onSendMessage = { text ->
                    val outgoing = ChatMessage(
                        id = "out_${UUID.randomUUID()}",
                        text = text,
                        user = currentUser,
                        createdAt = Date(),
                        status = MessageStatus.SENT,
                        replyTo = replyingTo,
                    )
                    replyingTo = null
                    messagesState.addToStart(outgoing, scroll = true)
                    onLatestMessageChanged(outgoing)

                    peerTyping = true
                    simulateReply(text) { reply ->
                        peerTyping = false
                        messagesState.updateById(outgoing.id) { msg ->
                            if (msg is ChatMessage) msg.copy(status = MessageStatus.READ) else msg
                        }
                        messagesState.addToStart(reply, scroll = true)
                        onLatestMessageChanged(reply)
                    }
                    true
                },
                onAttachmentClick = {
                    val photoMsg = ChatMessage(
                        id = "img_${UUID.randomUUID()}",
                        text = "Shared a Compose layout preview",
                        user = currentUser,
                        createdAt = Date(),
                        imageUrl = "demo://compose_preview.png",
                        status = MessageStatus.DELIVERED,
                    )
                    messagesState.addToStart(photoMsg, scroll = true)
                    onLatestMessageChanged(photoMsg)
                },
                onTypingChanged = { isTyping ->
                    if (isTyping) {
                        onShowToast("Typing started")
                    }
                },
                replyingTo = replyingTo,
                onCancelReply = { replyingTo = null },
                modifier = Modifier.imePadding(),
            )
        },
    ) { padding ->
        MessagesList(
            state = messagesState,
            style = style,
            customContentConfig = customContentConfig,
            onMessageClick = { msg -> replyingTo = msg },
            onSelectionChanged = { count -> selectionCount = count },
            onLoadMore = { page, _ ->
                if (!isLoadingMore && loadedPages < 2) {
                    isLoadingMore = true
                    scope.launch {
                        delay(500L)
                        val peer = dialog.users.firstOrNull { it.id != currentUser.id } ?: currentUser
                        val older = (1..3).map { index ->
                            ChatMessage(
                                id = "hist_${page}_$index",
                                text = "Archived history message #$index (page $page)",
                                user = if (index % 2 == 0) currentUser else peer,
                                createdAt = daysAgo(page + 1, hour = 10 + index),
                            )
                        }
                        messagesState.addToEnd(older, reverse = false)
                        loadedPages += 1
                        isLoadingMore = false
                    }
                }
            },
            isLoadingMore = isLoadingMore,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}

@Composable
private fun SampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

private fun sampleDialogs(
    me: ChatUser,
    alice: ChatUser,
    bob: ChatUser,
    carol: ChatUser,
): List<ChatDialog<IMessage>> = listOf(
    ChatDialog(
        id = "dialog_alice",
        dialogName = alice.name,
        users = listOf(me, alice),
        lastMessage = ChatMessage(
            id = "d_alice_last",
            text = "Look how smooth Compose LazyColumn animations feel!",
            user = alice,
            createdAt = minutesAgo(3),
        ),
        unreadCount = 2,
    ),
    ChatDialog(
        id = "dialog_team",
        dialogName = "Android Architecture Team",
        users = listOf(me, alice, bob, carol),
        lastMessage = ChatMessage(
            id = "d_team_last",
            text = "Vote on the Material 3 theme tokens above",
            user = carol,
            createdAt = minutesAgo(42),
        ),
        unreadCount = 5,
    ),
    ChatDialog(
        id = "dialog_bob",
        dialogName = bob.name,
        users = listOf(me, bob),
        lastMessage = ChatMessage(
            id = "d_bob_last",
            text = "Migrated our RecyclerView adapter in 10 minutes",
            user = bob,
            createdAt = daysAgo(1, hour = 18),
        ),
        unreadCount = 0,
    ),
)

private fun sampleThreadMessages(
    dialog: ChatDialog<IMessage>,
    me: ChatUser,
): List<IMessage> {
    val peer = dialog.users.firstOrNull { it.id != me.id } ?: me
    val firstMsg = ChatMessage(
        id = "${dialog.id}_1",
        text = "Hey! Ready to migrate from XML ChatKit to Jetpack Compose?",
        user = peer,
        createdAt = daysAgo(1, hour = 14),
    )
    val secondMsg = ChatMessage(
        id = "${dialog.id}_2",
        text = "Yes! Same IUser, IMessage, and IDialog models — zero database changes needed.",
        user = me,
        createdAt = daysAgo(1, hour = 14, minute = 2),
        status = MessageStatus.READ,
        replyTo = firstMsg,
        reactions = mapOf("🚀" to 2, "👍" to 1),
    )
    val imageMsg = ChatMessage(
        id = "${dialog.id}_3",
        text = "Here is a preview of the Material 3 bubble grouping:",
        user = peer,
        createdAt = minutesAgo(8),
        imageUrl = "demo://material3_bubbles.png",
    )
    val pollMsg = PollMessage(
        id = "${dialog.id}_4",
        text = "Quick poll",
        user = peer,
        createdAt = minutesAgo(6),
        question = "Favorite ChatKit-Compose upgrade?",
        options = listOf("Material 3 Theming", "Drop-in State Holders", "Custom Content DSL"),
    )
    val latest = ChatMessage(
        id = "${dialog.id}_5",
        text = dialog.lastMessage?.text ?: "Tap any message to reply, or long-press to multi-select.",
        user = peer,
        createdAt = minutesAgo(2),
    )
    return listOf(firstMsg, secondMsg, imageMsg, pollMsg, latest)
}

private fun minutesAgo(minutes: Int): Date =
    Calendar.getInstance().apply {
        add(Calendar.MINUTE, -minutes)
    }.time

private fun daysAgo(days: Int, hour: Int = 12, minute: Int = 0): Date =
    Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -days)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }.time

@Preview(showBackground = true, name = "Dialogs Light")
@Composable
private fun DialogsPreviewLight() {
    val me = ChatUser("me", "Kai")
    val alice = ChatUser("alice", "Alice Meyer", isOnline = true)
    val bob = ChatUser("bob", "Bob Tanaka")
    val carol = ChatUser("carol", "Carol Vance", isOnline = true)
    MaterialTheme(colorScheme = lightColorScheme()) {
        DialogsList(
            dialogs = sampleDialogs(me, alice, bob, carol),
            onDialogClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Messages Dark")
@Composable
private fun MessagesPreviewDark() {
    val me = ChatUser("me", "Kai")
    val alice = ChatUser("alice", "Alice Meyer", isOnline = true)
    val dialog = ChatDialog<IMessage>("d1", dialogName = "Alice", users = listOf(me, alice))
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                MessagesList(
                    messages = sampleThreadMessages(dialog, me),
                    senderId = me.id,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                MessageInput(
                    value = "",
                    onValueChange = {},
                    onSendClick = {},
                    onAttachmentClick = {},
                )
            }
        }
    }
}

