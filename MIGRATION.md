# Migration Guide: `stfalcon-studio/ChatKit` (XML) -> `ChatKit-Compose`

This guide helps you move smoothly from legacy XML Views & RecyclerView adapters in [stfalcon-studio/ChatKit](https://github.com/stfalcon-studio/ChatKit) to **ChatKit-Compose**—without rewriting your domain or database models.

---

## 1. Package & Interface Mapping

| Legacy `com.stfalcon.chatkit` | `de.hartz.software.chatkitcompose` | Notes |
| :--- | :--- | :--- |
| `commons.models.IUser` | `models.IUser` | `id`, `name`, `avatar` stay identical (+ optional `isOnline`) |
| `commons.models.IMessage` | `models.IMessage` | `id`, `text`, `user`, `createdAt` stay identical (+ optional `status`, `replyTo`, `reactions`) |
| `commons.models.IDialog<M>` | `models.IDialog<M>` | `id`, `dialogPhoto`, `dialogName`, `users`, `lastMessage`, `unreadCount` |
| `commons.models.MessageContentType` | `models.MessageContentType` | Includes `Image`, `Voice`, and `CustomContentConfig` |
| `commons.ImageLoader` | `theme.ChatKitImageLoader` | Provided via `LocalChatKitImageLoader` |
| `utils.DateFormatter` | `utils.ChatDateFormatter` | Drop-in date formatting helpers & templates |
| `messages.MessagesList` + `MessagesListAdapter` | `messages.MessagesList` + `MessagesListState` | Supports both stateless Compose UDF and adapter-like state |
| `dialogs.DialogsList` + `DialogsListAdapter` | `dialogs.DialogsList` + `DialogsListState` | Supports animated item reordering via `LazyColumn` |
| `messages.MessageInput` | `messages.MessageInput` | Supports hoisted state (`value`/`onValueChange`) or `onSendMessage` |

---

## 2. Data Models (Side-by-Side)

### Before (`stfalcon-studio/ChatKit`)
```kotlin
class MyMessage(
    private val id: String,
    private val text: String,
    private val author: MyUser,
    private val createdAt: Date,
) : com.stfalcon.chatkit.commons.models.IMessage {
    override fun getId(): String = id
    override fun getText(): String = text
    override fun getUser(): IUser = author
    override fun getCreatedAt(): Date = createdAt
}
```

### After (`ChatKit-Compose`)
```kotlin
data class MyMessage(
    override val id: String,
    override val text: String,
    override val user: MyUser,
    override val createdAt: Date,
) : de.hartz.software.chatkitcompose.models.IMessage
```
> Or skip boilerplate altogether and use the built-in immutable `ChatUser`, `ChatMessage`, and `ChatDialog` classes.

---

## 3. `MessagesList` + `MessagesListAdapter`

### Before (XML + Adapter)
```xml
<com.stfalcon.chatkit.messages.MessagesList
    android:id="@+id/messagesList"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
```kotlin
val adapter = MessagesListAdapter<MyMessage>(senderId, imageLoader)
adapter.setLoadMoreListener { page, totalItemsCount -> loadHistory(page) }
adapter.enableSelectionMode { count -> updateToolbar(count) }
messagesList.setAdapter(adapter)

adapter.addToStart(newMessage, true)
adapter.addToEnd(olderMessages, false)
```

### After (Compose State-Holder Migration Path)
```kotlin
val messagesState = rememberMessagesListState<MyMessage>(senderId = senderId)

MessagesList(
    state = messagesState,
    onSelectionChanged = { count -> updateToolbar(count) },
    onLoadMore = { page, totalItemsCount -> loadHistory(page) },
    modifier = Modifier.fillMaxSize(),
)

// Familiar imperative calls still work!
messagesState.addToStart(newMessage, scroll = true)
messagesState.addToEnd(olderMessages, reverse = false)
```

### After (Pure Stateless Compose UDF — Recommended for New Screens)
```kotlin
MessagesList(
    messages = uiState.messages,
    senderId = uiState.currentUserId,
    selectedMessageIds = uiState.selectedIds,
    onMessageClick = { msg -> viewModel.onMessageClick(msg) },
    onMessageLongClick = { msg -> viewModel.toggleSelection(msg.id) },
    onLoadMore = { page, _ -> viewModel.loadMore(page) },
    modifier = Modifier.fillMaxSize(),
)
```

---

## 4. `DialogsList` + `DialogsListAdapter`

### Before (XML + Adapter)
```kotlin
val adapter = DialogsListAdapter<MyDialog>(imageLoader)
adapter.setItems(dialogs)
adapter.setOnDialogClickListener { dialog -> openChat(dialog) }
dialogsList.setAdapter(adapter)
```

### After (`ChatKit-Compose`)
```kotlin
val dialogsState = rememberDialogsListState<MyMessage, MyDialog>(initialDialogs = dialogs)

DialogsList(
    state = dialogsState,
    onDialogClick = { dialog -> openChat(dialog) },
    onDialogLongClick = { dialog -> showOptions(dialog) },
)
```

---

## 5. `MessageInput`

### Before (XML Listeners)
```kotlin
messageInput.setInputListener { input ->
    sendMessage(input.toString())
    true
}
messageInput.setAttachmentsListener { openPicker() }
messageInput.setTypingListener(object : MessageInput.TypingListener {
    override fun onStartTyping() = setTyping(true)
    override fun onStopTyping() = setTyping(false)
})
```

### After (`ChatKit-Compose`)
```kotlin
MessageInput(
    onSendMessage = { input ->
        sendMessage(input)
        true
    },
    onAttachmentClick = { openPicker() },
    onTypingChanged = { isTyping -> setTyping(isTyping) },
    replyingTo = activeReply,
    onCancelReply = { activeReply = null },
)
```

---

## 6. `ImageLoader` -> `LocalChatKitImageLoader`

### Before
```kotlin
val imageLoader = ImageLoader { imageView, url, _ ->
    Picasso.get().load(url).into(imageView)
}
```

### After (with Coil Compose)
```kotlin
val imageLoader = ChatKitImageLoader { url, contentDescription, modifier ->
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

ProvideChatKitImageLoader(imageLoader) {
    // All nested MessagesList and DialogsList composables automatically use Coil!
    ChatScreen()
}
```

---

## 7. Custom Content Types (`ViewHolder` -> Composable DSL)

### Before (`MessageHolders.registerContentType`)
Required subclassing `MessageHolders.BaseIncomingMessageViewHolder`, inflating XML layouts, and wiring view IDs manually.

### After (`CustomContentConfig`)
```kotlin
val customContent = remember {
    CustomContentConfig.Builder<IMessage>()
        .registerType<PollMessage> { poll, isIncoming ->
            PollCard(poll = poll, isIncoming = isIncoming)
        }
        .build()
}

MessagesList(
    state = messagesState,
    customContentConfig = customContent,
)
```

---

## 8. XML Style Attributes -> `ChatKitDefaults`

### Before (`attrs.xml`)
```xml
<com.stfalcon.chatkit.messages.MessagesList
    app:incomingDefaultBubbleColor="@color/gray"
    app:outcomingDefaultBubbleColor="@color/blue"
    app:incomingTextSize="16sp" />
```

### After (Material 3 Tokens)
```kotlin
val style = ChatKitDefaults.messagesListStyle(
    incomingBubbleColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    outgoingBubbleColor = MaterialTheme.colorScheme.primaryContainer,
    showIncomingAuthorName = true,
)

MessagesList(
    state = messagesState,
    style = style,
)
```

