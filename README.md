# ChatKit-Compose

A modern, **100% clean-room Jetpack Compose** and **Material 3** chat UI library inspired by and succeeding [stfalcon-studio/ChatKit](https://github.com/stfalcon-studio/ChatKit).

> [!NOTE]
> **Independent Community Project & Deep Gratitude**
> **ChatKit-Compose** is an independent, community-built Jetpack Compose successor—not an official Stfalcon product. We owe huge thanks to [Stfalcon](https://github.com/stfalcon-studio/ChatKit) for designing one of the cleanest, most model-agnostic chat UI libraries in Android history. Our goal is to help every developer move forward with Jetpack Compose and Material 3 effortlessly, **without throwing away existing data models or doing heavy rewrites**.

---

## Why ChatKit-Compose?

- **Zero Data-Layer Rewrite:** Keep your existing `IUser`, `IMessage`, `IDialog`, and `MessageContentType` implementations.
- **Effortless Adapter Migration:** Prefer incremental migration? Use `rememberMessagesListState` and `rememberDialogsListState` with familiar methods like `addToStart`, `addToEnd`, `update`, `deleteById`, `unselectAllItems`, and `updateDialogWithMessage`.
- **Pure Jetpack Compose Idioms:** Prefer Unidirectional Data Flow (UDF)? Every component (`MessagesList`, `DialogsList`, `MessageInput`) also exposes a pure stateless overload with strict state hoisting and `Modifier` conventions.
- **Material 3 Out of the Box:** Automatic light/dark theme and dynamic color support via `ChatKitDefaults` (`messagesListStyle`, `dialogsListStyle`, `messageInputStyle`).
- **Modern UX Built-In:**
  - Smooth `LazyColumn` item placement animations (`Modifier.animateItem()`).
  - Automatic calendar date headers (`Today`, `Yesterday`, `d MMMM`) and consecutive same-author message grouping.
  - Built-in support for quoted replies (`replyTo`), emoji reactions (`reactions`), delivery status icons (`MessageStatus`), image attachments, voice notes, and multi-selection mode.
  - Pluggable `CustomContentConfig` DSL replacing RecyclerView `ViewHolder` boilerplate.
  - Library-agnostic `ChatKitImageLoader` via `LocalChatKitImageLoader` (works out of the box with initials avatars or plugs into Coil / Glide).

---

## Installation (JitPack)

Add the JitPack repository in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency in your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Hatzen:ChatKit-Compose:1.0.0")
}
```

---

## Quick Start

### 1. Plug In Your Own Data Models (or Use `ChatUser` / `ChatMessage` / `ChatDialog`)

```kotlin
val currentUser = ChatUser(id = "me", name = "Kai", isOnline = true)
val peer = ChatUser(id = "alice", name = "Alice", isOnline = true)

val messagesState = rememberMessagesListState(
    senderId = currentUser.id,
    initialMessages = listOf(
        ChatMessage(
            id = "1",
            text = "Welcome to ChatKit-Compose!",
            user = peer,
            createdAt = Date(),
        ),
    ),
)
```

### 2. Render `MessagesList` + `MessageInput`

```kotlin
Scaffold(
    bottomBar = {
        MessageInput(
            onSendMessage = { text ->
                messagesState.addToStart(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = text,
                        user = currentUser,
                        createdAt = Date(),
                    ),
                    scroll = true,
                )
                true
            },
            onAttachmentClick = { /* open media picker */ },
        )
    },
) { innerPadding ->
    MessagesList(
        state = messagesState,
        onSelectionChanged = { selectedCount ->
            // Toggle contextual TopAppBar actions when selectedCount > 0
        },
        onLoadMore = { page, totalItemsCount ->
            // Fetch older history and call messagesState.addToEnd(olderMessages)
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    )
}
```

### 3. Render `DialogsList`

```kotlin
DialogsList(
    dialogs = dialogs,
    onDialogClick = { dialog -> openThread(dialog.id) },
    onDialogLongClick = { dialog -> showDialogOptions(dialog) },
)
```

### 4. Plug In Coil / Glide (Optional)

```kotlin
val coilImageLoader = ChatKitImageLoader { url, contentDescription, modifier ->
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

ProvideChatKitImageLoader(coilImageLoader) {
    ChatScreen()
}
```

---

## Migrating from `stfalcon-studio/ChatKit`?

We wrote a complete side-by-side migration guide comparing old XML + RecyclerView adapters with their Compose equivalents:

👉 **[Read the Side-by-Side Migration Guide (`MIGRATION.md`)](./MIGRATION.md)**

---

## Development, Code Style & Agentic Harness

This repository follows Google's official Android & Jetpack Compose guidelines and includes an **Agentic Coding Harness** for AI-assisted development:

- [`AGENTS.md`](./AGENTS.md) — Architecture, Compose API guidelines, and Git workflow rules.
- [`.agents/rules/`](./.agents/rules/) — Contextual coding and Git rules.
- [`.agents/skills/`](./.agents/skills/) — Reusable `verify-build` and `migrate-chatkit` workflows.
- [`.editorconfig`](./.editorconfig) & [`config/detekt/detekt.yml`](./config/detekt/detekt.yml) — Kotlin & Compose static analysis rules.

Run tests and build the library + sample app locally:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

---

## License

Licensed under the **Apache License, Version 2.0**. See [`LICENSE`](./LICENSE) and [`NOTICE`](./NOTICE) for full details and attribution to `stfalcon-studio/ChatKit`.

