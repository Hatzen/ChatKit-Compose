package de.hartz.software.chatkitcompose.sample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.dialogs.DialogsList
import de.hartz.software.chatkitcompose.dialogs.rememberDialogsListState
import de.hartz.software.chatkitcompose.messages.AttachmentKind
import de.hartz.software.chatkitcompose.messages.MessageInput
import de.hartz.software.chatkitcompose.messages.MessagesList
import de.hartz.software.chatkitcompose.messages.rememberChatAttachmentLaunchers
import de.hartz.software.chatkitcompose.messages.rememberMessagesListState
import de.hartz.software.chatkitcompose.models.ChatDialog
import de.hartz.software.chatkitcompose.models.ChatMessage
import de.hartz.software.chatkitcompose.models.ChatUser
import de.hartz.software.chatkitcompose.models.ContactMessage
import de.hartz.software.chatkitcompose.models.DocumentMessage
import de.hartz.software.chatkitcompose.models.GifMessage
import de.hartz.software.chatkitcompose.models.IMessage
import de.hartz.software.chatkitcompose.models.LocationMessage
import de.hartz.software.chatkitcompose.models.MessageStatus
import de.hartz.software.chatkitcompose.models.PollMessage
import de.hartz.software.chatkitcompose.models.PollOption
import de.hartz.software.chatkitcompose.models.SystemEventMessage
import de.hartz.software.chatkitcompose.models.VideoMessage
import de.hartz.software.chatkitcompose.models.VoiceMessage
import de.hartz.software.chatkitcompose.profile.ChatProfileSection
import de.hartz.software.chatkitcompose.theme.ChatAvatar
import de.hartz.software.chatkitcompose.theme.ChatKitAnimationConfig
import de.hartz.software.chatkitcompose.theme.ChatKitDefaults
import de.hartz.software.chatkitcompose.theme.ChatKitFeatureConfig
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import de.hartz.software.chatkitcompose.theme.ChatKitImageLoader
import de.hartz.software.chatkitcompose.theme.ProvideChatKitAnimationConfig
import de.hartz.software.chatkitcompose.theme.ProvideChatKitFeatureConfig
import de.hartz.software.chatkitcompose.theme.ProvideChatKitImageLoader
import de.hartz.software.chatkitcompose.utils.ChatFileTypeDetector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

enum class SampleThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(EXTRA_SHOW_WHEN_LOCKED, false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            }
        }
        enableEdgeToEdge()
        setContent {
            ChatKitSampleApp()
        }
    }

    companion object {
        const val EXTRA_SHOW_WHEN_LOCKED = "EXTRA_SHOW_WHEN_LOCKED"
    }
}

@Composable
fun ChatKitSampleApp(
    initialThemeMode: SampleThemeMode = SampleThemeMode.SYSTEM,
    initialAnimationsEnabled: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var themeMode by remember { mutableStateOf(initialThemeMode) }
    var animationsEnabled by remember { mutableStateOf(initialAnimationsEnabled) }
    var featureConfig by remember {
        mutableStateOf(ChatKitFeatureConfig(maxMessageChunkSize = 140))
    }
    var showConfigDialog by remember { mutableStateOf(false) }

    val dialogsState = rememberDialogsListState<IMessage, ChatDialog<IMessage>>(
        initialDialogs = remember { SampleData.initialDialogs() },
    )
    var activeDialog by remember { mutableStateOf<ChatDialog<IMessage>?>(null) }
    var profileFocusUser by remember { mutableStateOf<ChatUser?>(null) }
    var showingProfile by remember { mutableStateOf(false) }
    var incomingSimCount by remember { mutableIntStateOf(0) }

    val threadStates = remember {
        mutableStateMapOf<String, List<IMessage>>()
    }

    val demoImageLoader = remember {
        ChatKitImageLoader { url, contentDescription, modifier ->
            val gradient = when {
                url?.contains("video") == true -> listOf(Color(0xFF1A237E), Color(0xFF00838F))
                url?.contains("gif") == true -> listOf(Color(0xFF6A1B9A), Color(0xFFD81B60))
                else -> listOf(Color(0xFF1565C0), Color(0xFF26A69A))
            }
            Box(
                modifier = modifier.background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(16.dp),
                ) {
                    if (url?.contains("video") != true) {
                        Icon(
                            imageVector = if (url?.contains("gif") == true) {
                                ChatKitIcons.Gif
                            } else {
                                ChatKitIcons.ImagePlaceholder
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = contentDescription ?: (url ?: "Media"),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }

    val isDark = when (themeMode) {
        SampleThemeMode.SYSTEM -> isSystemInDarkTheme()
        SampleThemeMode.LIGHT -> false
        SampleThemeMode.DARK -> true
    }
    val animationConfig = if (animationsEnabled) {
        ChatKitAnimationConfig.Default
    } else {
        ChatKitAnimationConfig.Disabled
    }

    SampleTheme(darkTheme = isDark) {
        ProvideChatKitImageLoader(demoImageLoader) {
            ProvideChatKitAnimationConfig(animationConfig) {
                ProvideChatKitFeatureConfig(featureConfig) {
                    val currentDialog = activeDialog
                    if (currentDialog == null) {
                        DialogsScreen(
                            dialogs = dialogsState.dialogs,
                            isDark = isDark,
                            animationsEnabled = animationsEnabled,
                            onToggleTheme = {
                                themeMode = if (isDark) SampleThemeMode.LIGHT else SampleThemeMode.DARK
                            },
                            onToggleAnimations = { animationsEnabled = !animationsEnabled },
                            onOpenConfig = { showConfigDialog = true },
                            onDialogClick = { clicked ->
                                dialogsState.upsertItem(clicked.copy(unreadCount = 0), sortByNewest = false)
                                activeDialog = clicked.copy(unreadCount = 0)
                                showingProfile = false
                                profileFocusUser = null
                            },
                            onSimulateIncoming = {
                                incomingSimCount += 1
                                val incoming = ChatMessage(
                                    id = "sim_${UUID.randomUUID()}",
                                    text = "Ping #$incomingSimCount from Bob via Compose!",
                                    user = SampleData.bob,
                                    createdAt = Date(),
                                )
                                dialogsState.updateDialogWithMessage("dialog_bob", incoming) { dialog, msg ->
                                    dialog.copy(lastMessage = msg, unreadCount = dialog.unreadCount + 1)
                                }
                            },
                        )
                    } else if (showingProfile) {
                        BackHandler { showingProfile = false }
                        ProfileScreen(
                            dialog = currentDialog,
                            focusUser = profileFocusUser,
                            onBack = {
                                showingProfile = false
                                profileFocusUser = null
                            },
                            onOpenChat = {
                                showingProfile = false
                                profileFocusUser = null
                            },
                            onRefreshKeys = {
                                val notice = SystemEventMessage(
                                    id = "sys_${UUID.randomUUID()}",
                                    eventText = "Encryption keys refreshed by user",
                                    user = SampleData.me,
                                    createdAt = Date(),
                                    isSecurityEvent = true,
                                )
                                val existing = threadStates[currentDialog.id]
                                    ?: SampleData.initialMessagesFor(currentDialog)
                                threadStates[currentDialog.id] = listOf(notice) + existing
                                showingProfile = false
                                Toast.makeText(context, "Keys refreshed", Toast.LENGTH_SHORT).show()
                            },
                            onResetKeys = {
                                Toast.makeText(context, "Session keys reset", Toast.LENGTH_SHORT).show()
                            },
                            onSelectMember = { member -> profileFocusUser = member },
                        )
                    } else {
                        BackHandler { activeDialog = null }
                        val initialList = threadStates[currentDialog.id]
                            ?: SampleData.initialMessagesFor(currentDialog)
                        ChatThreadScreen(
                            dialog = currentDialog,
                            initialMessages = initialList,
                            isDark = isDark,
                            animationsEnabled = animationsEnabled,
                            onToggleTheme = {
                                themeMode = if (isDark) SampleThemeMode.LIGHT else SampleThemeMode.DARK
                            },
                            onToggleAnimations = { animationsEnabled = !animationsEnabled },
                            onOpenConfig = { showConfigDialog = true },
                            onOpenProfile = { member ->
                                profileFocusUser = member
                                showingProfile = true
                            },
                            onBack = { activeDialog = null },
                            onMessagesChanged = { updatedList ->
                                threadStates[currentDialog.id] = updatedList
                                updatedList.maxByOrNull { it.createdAt.time }?.let { latest ->
                                    dialogsState.updateDialogWithMessage(currentDialog.id, latest) { d, msg ->
                                        d.copy(lastMessage = msg, unreadCount = 0)
                                    }
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
                                    delay(750L)
                                    val peer = currentDialog.users.firstOrNull { it.id != SampleData.me.id }
                                        ?: SampleData.alice
                                    onReplyReady(
                                        ChatMessage(
                                            id = "reply_${UUID.randomUUID()}",
                                            text = "Received \"$replyText\" cleanly in Compose!",
                                            user = peer,
                                            createdAt = Date(),
                                        ),
                                    )
                                }
                            },
                        )
                    }

                    if (showConfigDialog) {
                        FeatureConfigDialog(
                            config = featureConfig,
                            animationsEnabled = animationsEnabled,
                            onConfigChange = { featureConfig = it },
                            onAnimationsChange = { animationsEnabled = it },
                            onDismiss = { showConfigDialog = false },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogsScreen(
    dialogs: List<ChatDialog<IMessage>>,
    isDark: Boolean,
    animationsEnabled: Boolean,
    onToggleTheme: () -> Unit,
    onToggleAnimations: () -> Unit,
    onOpenConfig: () -> Unit,
    onDialogClick: (ChatDialog<IMessage>) -> Unit,
    onSimulateIncoming: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChatKit-Compose") },
                actions = {
                    ThemeAndConfigActions(
                        isDark = isDark,
                        animationsEnabled = animationsEnabled,
                        onToggleTheme = onToggleTheme,
                        onToggleAnimations = onToggleAnimations,
                        onOpenConfig = onOpenConfig,
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSimulateIncoming,
                modifier = Modifier.testTag("simulate_ping_fab"),
            ) {
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
private fun ProfileScreen(
    dialog: ChatDialog<IMessage>,
    focusUser: ChatUser?,
    onBack: () -> Unit,
    onOpenChat: () -> Unit,
    onRefreshKeys: () -> Unit,
    onResetKeys: () -> Unit,
    onSelectMember: (ChatUser) -> Unit,
) {
    val context = LocalContext.current
    val title = focusUser?.name ?: dialog.dialogName
    val isOnline = focusUser?.isOnline ?: (dialog.users.size == 2 && dialog.users.any {
        it.id != SampleData.me.id && it.isOnline
    })

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button"),
                    ) {
                        Icon(imageVector = ChatKitIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Profile & Security") },
            )
        },
    ) { padding ->
        ChatProfileSection(
            title = title,
            avatarUrl = focusUser?.avatar ?: dialog.dialogPhoto,
            subtitle = if (focusUser == null && dialog.users.size > 2) {
                "${dialog.users.size} participants"
            } else if (isOnline) {
                "Online • End-to-end encrypted"
            } else {
                "Offline • Verified contact"
            },
            isOnline = isOnline,
            verificationBadge = "Verified Identity",
            quickActions = SampleData.buildQuickActions(
                onMessage = onOpenChat,
                onRefreshKeys = onRefreshKeys,
                onInfoToast = {
                    Toast.makeText(context, "Fingerprint verified", Toast.LENGTH_SHORT).show()
                },
            ),
            items = SampleData.buildProfileItems(
                dialog = dialog,
                focusUser = focusUser,
                onOpenChat = onOpenChat,
                onRefreshKeys = onRefreshKeys,
                onResetKeys = onResetKeys,
                onShowToast = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onSelectMember = onSelectMember,
            ),
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
    initialMessages: List<IMessage>,
    isDark: Boolean,
    animationsEnabled: Boolean,
    onToggleTheme: () -> Unit,
    onToggleAnimations: () -> Unit,
    onOpenConfig: () -> Unit,
    onOpenProfile: (ChatUser?) -> Unit,
    onBack: () -> Unit,
    onMessagesChanged: (List<IMessage>) -> Unit,
    onShowToast: (String) -> Unit,
    onCopyText: (String) -> Unit,
    simulateReply: (String, (IMessage) -> Unit) -> Unit,
) {
    val messagesState = rememberMessagesListState(
        senderId = SampleData.me.id,
        initialMessages = initialMessages,
    )
    var selectionCount by remember { mutableIntStateOf(0) }
    var replyingTo by remember { mutableStateOf<IMessage?>(null) }
    var peerTyping by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var loadedPages by remember { mutableIntStateOf(0) }
    var showSystemFilePrompt by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val attachmentLaunchers = rememberChatAttachmentLaunchers { files ->
        files.forEach { file ->
            val msg: IMessage = when (file.kind) {
                ChatFileTypeDetector.Kind.GIF -> GifMessage(
                    id = "gif_${UUID.randomUUID()}",
                    gifUrl = file.uri,
                    user = SampleData.me,
                    createdAt = Date(),
                    text = file.displayName,
                )
                ChatFileTypeDetector.Kind.VIDEO -> VideoMessage(
                    id = "vid_${UUID.randomUUID()}",
                    videoUrl = file.uri,
                    fileSizeText = ChatFileTypeDetector.formatFileSize(file.sizeBytes),
                    user = SampleData.me,
                    createdAt = Date(),
                    text = file.displayName,
                )
                ChatFileTypeDetector.Kind.IMAGE -> ChatMessage(
                    id = "img_${UUID.randomUUID()}",
                    text = file.displayName,
                    user = SampleData.me,
                    createdAt = Date(),
                    imageUrl = file.uri,
                )
                else -> DocumentMessage(
                    id = "doc_${UUID.randomUUID()}",
                    fileName = file.displayName,
                    filePathOrUri = file.uri,
                    fileSizeBytes = file.sizeBytes,
                    mimeType = file.mimeType,
                    user = SampleData.me,
                    createdAt = Date(),
                )
            }
            messagesState.addToStart(msg, scroll = true)
        }
        onMessagesChanged(messagesState.messages.toList())
    }

    val isGroup = dialog.users.size > 2
    val style = ChatKitDefaults.messagesListStyle(showIncomingAuthorName = isGroup)
    val typingUsers = if (peerTyping) {
        listOfNotNull(dialog.users.firstOrNull { it.id != SampleData.me.id })
    } else {
        emptyList()
    }

    Scaffold(
        topBar = {
            if (selectionCount > 0) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { messagesState.unselectAllItems() }) {
                            Icon(imageVector = ChatKitIcons.Close, contentDescription = "Cancel selection")
                        }
                    },
                    title = { Text("$selectionCount selected") },
                    actions = {
                        IconButton(
                            onClick = {
                                val text = messagesState.getSelectedMessages().joinToString("\n") { it.text }
                                onCopyText(text)
                                messagesState.unselectAllItems()
                            },
                        ) {
                            Icon(imageVector = ChatKitIcons.ContentCopy, contentDescription = "Copy")
                        }
                        IconButton(
                            onClick = {
                                messagesState.deleteSelected()
                                onMessagesChanged(messagesState.messages.toList())
                            },
                        ) {
                            Icon(
                                imageVector = ChatKitIcons.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )
            } else {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("chat_back_button"),
                        ) {
                            Icon(imageVector = ChatKitIcons.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenProfile(null) }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .testTag("profile_header_button"),
                        ) {
                            ChatAvatar(
                                name = dialog.dialogName,
                                imageUrl = dialog.dialogPhoto,
                                size = 36.dp,
                                isOnline = !isGroup && dialog.users.any {
                                    it.id != SampleData.me.id && it.isOnline
                                },
                            )
                            Column {
                                Text(
                                    text = dialog.dialogName,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = if (peerTyping) "typing..." else "Tap for profile & keys",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (peerTyping) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                        }
                    },
                    actions = {
                        ThemeAndConfigActions(
                            isDark = isDark,
                            animationsEnabled = animationsEnabled,
                            onToggleTheme = onToggleTheme,
                            onToggleAnimations = onToggleAnimations,
                            onOpenConfig = onOpenConfig,
                        )
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
                        user = SampleData.me,
                        createdAt = Date(),
                        status = MessageStatus.SENDING,
                        replyTo = replyingTo,
                    )
                    replyingTo = null
                    messagesState.addToStart(outgoing, scroll = true)
                    onMessagesChanged(messagesState.messages.toList())

                    peerTyping = true
                    simulateReply(text) { reply ->
                        peerTyping = false
                        messagesState.updateById(outgoing.id) { msg ->
                            if (msg is ChatMessage) msg.copy(status = MessageStatus.READ) else msg
                        }
                        messagesState.addToStart(reply, scroll = true)
                        onMessagesChanged(messagesState.messages.toList())
                    }
                    true
                },
                onAttachmentOptionSelected = { kind ->
                    when (kind) {
                        AttachmentKind.IMAGE -> {
                            val img = ChatMessage(
                                id = "img_${UUID.randomUUID()}",
                                text = "Shared image preview",
                                user = SampleData.me,
                                createdAt = Date(),
                                imageUrl = "demo://attached_photo.png",
                            )
                            messagesState.addToStart(img, scroll = true)
                        }
                        AttachmentKind.GIF -> {
                            val gif = GifMessage(
                                id = "gif_${UUID.randomUUID()}",
                                gifUrl = "demo://party.gif",
                                user = SampleData.me,
                                createdAt = Date(),
                                text = "Sent a GIF",
                            )
                            messagesState.addToStart(gif, scroll = true)
                        }
                        AttachmentKind.VIDEO -> {
                            val vid = VideoMessage(
                                id = "vid_${UUID.randomUUID()}",
                                videoUrl = "demo://demo_clip.mp4",
                                durationSeconds = 19,
                                fileSizeText = "6.8 MB",
                                user = SampleData.me,
                                createdAt = Date(),
                                text = "Compose demo video",
                            )
                            messagesState.addToStart(vid, scroll = true)
                        }
                        AttachmentKind.FILE -> showSystemFilePrompt = true
                        AttachmentKind.POLL -> {
                            val poll = PollMessage(
                                id = "poll_${UUID.randomUUID()}",
                                question = "Ship ChatKit-Compose 1.0?",
                                options = listOf(
                                    PollOption("yes", "Yes, ship it!", 4),
                                    PollOption("now", "Ship right now!", 7),
                                ),
                                user = SampleData.me,
                                createdAt = Date(),
                            )
                            messagesState.addToStart(poll, scroll = true)
                        }
                        AttachmentKind.LOCATION -> {
                            val loc = LocationMessage(
                                id = "loc_${UUID.randomUUID()}",
                                latitude = 48.1351,
                                longitude = 11.5820,
                                placeName = "Marienplatz",
                                address = "80331 Munich, Germany",
                                user = SampleData.me,
                                createdAt = Date(),
                            )
                            messagesState.addToStart(loc, scroll = true)
                        }
                        AttachmentKind.CONTACT -> {
                            val contact = ContactMessage(
                                id = "cnt_${UUID.randomUUID()}",
                                contactName = "Carol Vance",
                                organization = "Mobile UI Lead",
                                email = "carol@chatkit-compose.dev",
                                verificationHash = "A4C9E01B2D77",
                                user = SampleData.me,
                                createdAt = Date(),
                            )
                            messagesState.addToStart(contact, scroll = true)
                        }
                    }
                    onMessagesChanged(messagesState.messages.toList())
                },
                onVoiceMessageRecorded = { duration, waveform ->
                    val voice = VoiceMessage(
                        id = "voice_${UUID.randomUUID()}",
                        audioUrl = "demo://recorded_$duration.opus",
                        durationSeconds = duration,
                        waveform = waveform,
                        user = SampleData.me,
                        createdAt = Date(),
                    )
                    messagesState.addToStart(voice, scroll = true)
                    onMessagesChanged(messagesState.messages.toList())
                },
                replyingTo = replyingTo,
                onCancelReply = { replyingTo = null },
                modifier = Modifier.imePadding(),
            )
        },
    ) { padding ->
        MessagesList(
            state = messagesState,
            typingUsers = typingUsers,
            style = style,
            authorNameFormatter = if (isGroup) {
                { msg ->
                    if (msg.user.id == SampleData.bob.id) {
                        "MAYBE: ${msg.user.name}"
                    } else {
                        msg.user.name
                    }
                }
            } else {
                null
            },
            onMessageClick = { msg -> replyingTo = msg },
            onSelectionChanged = { count -> selectionCount = count },
            onAvatarClick = { user ->
                (user as? ChatUser)?.let { onOpenProfile(it) }
            },
            onPollVote = { msg, optionId ->
                if (msg is PollMessage) {
                    val alreadySelected = optionId in msg.selectedOptionIds
                    val nextSelected = if (alreadySelected) {
                        msg.selectedOptionIds - optionId
                    } else if (msg.allowMultipleAnswers) {
                        msg.selectedOptionIds + optionId
                    } else {
                        setOf(optionId)
                    }
                    val updatedOptions = msg.options.map { opt ->
                        val wasChosen = opt.id in msg.selectedOptionIds
                        val isChosen = opt.id in nextSelected
                        val delta = when {
                            !wasChosen && isChosen -> 1
                            wasChosen && !isChosen -> -1
                            else -> 0
                        }
                        opt.copy(votes = (opt.votes + delta).coerceAtLeast(0))
                    }
                    messagesState.update(msg.copy(options = updatedOptions, selectedOptionIds = nextSelected))
                }
            },
            onMediaClick = { msg -> onShowToast("Opened media: ${msg.text.ifBlank { msg.id }}") },
            onLocationClick = { loc -> onShowToast("Location: ${loc.placeName}") },
            onContactClick = { contact -> onShowToast("Contact: ${contact.contactName}") },
            onDocumentClick = { doc ->
                val status = if (doc.isAvailable) doc.fileName else "${doc.fileName} is unavailable"
                onShowToast(status)
            },
            onLoadMore = { page, _ ->
                if (!isLoadingMore && loadedPages < 2) {
                    isLoadingMore = true
                    scope.launch {
                        delay(450L)
                        val peer = dialog.users.firstOrNull { it.id != SampleData.me.id } ?: SampleData.me
                        val older = (1..2).map { index ->
                            ChatMessage(
                                id = "hist_${page}_$index",
                                text = "Archived history message #$index (page $page)",
                                user = if (index % 2 == 0) SampleData.me else peer,
                                createdAt = SampleData.daysAgo(page + 1, hour = 10 + index),
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

    if (showSystemFilePrompt) {
        AlertDialog(
            onDismissRequest = { showSystemFilePrompt = false },
            title = { Text("Attach Document") },
            text = { Text("Choose between the Android system file picker or inserting a sample ZIP/PDF document.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSystemFilePrompt = false
                        attachmentLaunchers.pickDocuments()
                    },
                ) {
                    Text("System File Picker")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSystemFilePrompt = false
                        val sampleDoc = DocumentMessage(
                            id = "doc_${UUID.randomUUID()}",
                            fileName = "Release_Bundle_v1.zip",
                            filePathOrUri = "demo://release_bundle.zip",
                            fileSizeBytes = 4_820_000L,
                            additionalFilesCount = 4,
                            user = SampleData.me,
                            createdAt = Date(),
                        )
                        messagesState.addToStart(sampleDoc, scroll = true)
                        onMessagesChanged(messagesState.messages.toList())
                    },
                ) {
                    Text("Sample ZIP")
                }
            },
        )
    }
}

@Composable
private fun ThemeAndConfigActions(
    isDark: Boolean,
    animationsEnabled: Boolean,
    onToggleTheme: () -> Unit,
    onToggleAnimations: () -> Unit,
    onOpenConfig: () -> Unit,
) {
    IconButton(
        onClick = onToggleTheme,
        modifier = Modifier.testTag("theme_toggle_button"),
    ) {
        Icon(
            imageVector = if (isDark) ChatKitIcons.LightMode else ChatKitIcons.DarkMode,
            contentDescription = if (isDark) "Switch to light theme" else "Switch to dark theme",
        )
    }
    IconButton(
        onClick = onOpenConfig,
        modifier = Modifier.testTag("features_settings_button"),
    ) {
        Icon(
            imageVector = ChatKitIcons.Tune,
            contentDescription = "Configure features and animations",
        )
    }
    TextButton(
        onClick = onToggleAnimations,
        modifier = Modifier.testTag("animation_toggle_button"),
    ) {
        Text(
            text = if (animationsEnabled) "Anim:ON" else "Anim:OFF",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun FeatureConfigDialog(
    config: ChatKitFeatureConfig,
    animationsEnabled: Boolean,
    onConfigChange: (ChatKitFeatureConfig) -> Unit,
    onAnimationsChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Features & Animations") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FeatureSwitchRow("Enable Animations", animationsEnabled, onAnimationsChange)
                FeatureSwitchRow("Images", config.enableImages) { onConfigChange(config.copy(enableImages = it)) }
                FeatureSwitchRow("GIFs", config.enableGifs) { onConfigChange(config.copy(enableGifs = it)) }
                FeatureSwitchRow("Videos", config.enableVideos) { onConfigChange(config.copy(enableVideos = it)) }
                FeatureSwitchRow("Voice Messages", config.enableVoiceMessages) {
                    onConfigChange(config.copy(enableVoiceMessages = it))
                }
                FeatureSwitchRow("Files & Documents", config.enableFiles) {
                    onConfigChange(config.copy(enableFiles = it))
                }
                FeatureSwitchRow("Polls & Surveys", config.enablePolls) {
                    onConfigChange(config.copy(enablePolls = it))
                }
                FeatureSwitchRow("Locations / Maps", config.enableLocations) {
                    onConfigChange(config.copy(enableLocations = it))
                }
                FeatureSwitchRow("Contact Cards", config.enableContacts) {
                    onConfigChange(config.copy(enableContacts = it))
                }
                FeatureSwitchRow("Mask Messages by Default", config.maskMessagesByDefault) {
                    onConfigChange(config.copy(maskMessagesByDefault = it))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
    )
}

@Composable
private fun FeatureSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
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

@Preview(showBackground = true, name = "Dialogs Light")
@Composable
private fun DialogsPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        DialogsList(
            dialogs = SampleData.initialDialogs(),
            onDialogClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Messages Dark")
@Composable
private fun MessagesPreviewDark() {
    val dialogs = SampleData.initialDialogs()
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                MessagesList(
                    messages = SampleData.initialMessagesFor(dialogs.first()),
                    senderId = SampleData.me.id,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                MessageInput(
                    value = "",
                    onValueChange = {},
                    onSendClick = {},
                )
            }
        }
    }
}
