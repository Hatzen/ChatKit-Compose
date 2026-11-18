---
name: migrate-chatkit
description: >-
  Step-by-step workflow for migrating Android screens and adapters from legacy stfalcon-studio/ChatKit
  XML Views to ChatKit-Compose composables and state holders.
---

# Migrate Legacy stfalcon-studio/ChatKit to ChatKit-Compose

Follow this checklist when migrating an existing Android screen from `com.stfalcon.chatkit` to `de.hartz.software.chatkitcompose`:

## 1. Keep Data Models Intact
- Replace `com.stfalcon.chatkit.commons.models.IUser` -> `de.hartz.software.chatkitcompose.models.IUser` (`id`, `name`, `avatar`).
- Replace `com.stfalcon.chatkit.commons.models.IMessage` -> `de.hartz.software.chatkitcompose.models.IMessage` (`id`, `text`, `user`, `createdAt`).
- Replace `com.stfalcon.chatkit.commons.models.IDialog` -> `de.hartz.software.chatkitcompose.models.IDialog<M>` (`id`, `dialogPhoto`, `dialogName`, `users`, `lastMessage`, `unreadCount`).
- Replace `MessageContentType.Image` -> `de.hartz.software.chatkitcompose.models.MessageContentType.Image` (`imageUrl`).

## 2. Replace ImageLoader with CompositionLocal
- Wrap your screen or app root with `CompositionLocalProvider(LocalChatKitImageLoader provides yourLoader) { ... }`.
- Inside `ChatKitImageLoader`, delegate to Coil's `AsyncImage`, Glide, or your custom loader.

## 3. Replace XML Views & Adapters
- **Messages screen**:
  - Replace XML `<com.stfalcon.chatkit.messages.MessagesList>` + `MessagesListAdapter(senderId, imageLoader)` with `val state = rememberMessagesListState<YourMessage>(senderId)` and `MessagesList(state = state, ...)`.
  - Keep `state.addToStart(...)`, `state.addToEnd(...)`, `state.update(...)`, `state.deleteById(...)`, or migrate directly to stateless `MessagesList(messages = ..., senderId = ...)`.
- **Input bar**:
  - Replace XML `<com.stfalcon.chatkit.messages.MessageInput>` with `MessageInput(onSendMessage = { text -> ... }, onAttachmentClick = { ... }, onTypingChanged = { isTyping -> ... })`.
- **Dialogs screen**:
  - Replace XML `<com.stfalcon.chatkit.dialogs.DialogsList>` + `DialogsListAdapter` with `val state = rememberDialogsListState<YourMessage, YourDialog>()` and `DialogsList(state = state, onDialogClick = { ... })`.

## 4. Verify
- Reference [MIGRATION.md](../../../MIGRATION.md) for full side-by-side snippets and style mapping.
- Run `.\gradlew.bat testDebugUnitTest assembleDebug` after migration.

