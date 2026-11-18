# Jetpack Compose & Android Coding Guidelines

All code written in `:chatkit-compose` and `:sample` must follow Google's official Android Kotlin style guide and Jetpack Compose API guidelines:

## 1. Composable Signatures & Modifiers
- UI-emitting `@Composable` functions MUST:
  - Be named in `PascalCase` (noun or noun phrase, e.g., `MessagesList`, `MessageBubble`, `MessageInput`).
  - Return `Unit`.
  - Accept `modifier: Modifier = Modifier` as the **first optional parameter** (immediately after required parameters).
  - Apply the caller's `modifier` exactly once on the root layout composable, never inside nested children unless scoped explicitly.
- State factory functions MUST be named `remember<ComponentName>State(...)` and return a `@Stable` state holder object.

## 2. State Hoisting & Unidirectional Data Flow (UDF)
- Prefer stateless composables where events flow up (`onMessageClick`, `onValueChange`, `onSendMessage`) and state flows down (`messages`, `selectedIds`, `value`).
- Provide adapter-style state holders (`MessagesListState`, `DialogsListState`) strictly as migration convenience wrappers backed by Compose `SnapshotStateList` / `mutableStateOf` / `LazyListState`.
- Never mutate lists passed into stateless composables in place; return new immutable lists or manage mutations inside `SnapshotStateList` in state holders.

## 3. Stability & Performance
- Annotate public immutable data classes and style classes with `@Immutable`.
- Annotate stateful holders (`MessagesListState`, `DialogsListState`) with `@Stable`.
- Always provide stable `key = { ... }` lambdas in `LazyColumn` `items(...)` calls to enable smooth item animations (`Modifier.animateItem()`) and avoid unnecessary recomposition.
- Avoid heavy object allocations (such as `SimpleDateFormat` instantiation) inside composable bodies; cache or use `remember` / utility singletons safely.

## 4. Material 3 & Accessibility
- Derive all default colors, typography, and shapes in `ChatKitDefaults` from `MaterialTheme` so light/dark mode and dynamic color work automatically.
- Ensure interactive controls (`IconButton`, clickable message rows, dialogs) provide meaningful `contentDescription` or `semantics` metadata and meet `48.dp` minimum touch targets.
- Use `Icons` / `ImageVector` cleanly without forcing bloated icon dependencies on downstream library consumers.

## 5. Clean-Room & Documentation Integrity
- Every public API class, interface, parameter-rich data class, and `@Composable` must have concise KDoc describing its purpose and migration equivalent where helpful.
- All code must be 100% original Kotlin/Compose clean-room implementation under Apache License 2.0.

