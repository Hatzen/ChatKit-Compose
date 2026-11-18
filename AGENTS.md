# AGENTS.md — ChatKit-Compose Agentic Coding Harness

Welcome to **ChatKit-Compose**, an independent, 100% clean-room Jetpack Compose successor to [stfalcon-studio/ChatKit](https://github.com/stfalcon-studio/ChatKit).

This repository owes immense gratitude to Stfalcon for pioneering simple, model-agnostic chat UIs on Android. Our mission is to give Android teams an effortless, low-friction migration path from legacy XML Views (`MessagesList`, `DialogsList`, `MessageInput`) to modern Jetpack Compose and Material 3 without rewriting their domain models.

---

## Repository Structure

| Path | Purpose |
| :--- | :--- |
| `:chatkit-compose` | Core Jetpack Compose library module (`de.hartz.software.chatkitcompose`) |
| `:sample` | Interactive Material 3 showcase app (`de.hartz.software.chatkitcompose.sample`) |
| `.agents/rules/` | Always-active architectural, Compose API, and Git workflow rules |
| `.agents/skills/` | On-demand agent runbooks for build verification and legacy ChatKit migration |
| `config/detekt/detekt.yml` | Detekt static analysis configuration tuned for Jetpack Compose |
| `.editorconfig` | Android Studio & Ktlint formatting rules |
| `MIGRATION.md` | Side-by-side legacy `stfalcon-studio/ChatKit` -> `ChatKit-Compose` guide |

---

## Core Engineering Principles

1. **Google Android & Jetpack Compose API Guidelines**
   - Every UI-emitting `@Composable` returns `Unit`, uses `PascalCase`, and takes `modifier: Modifier = Modifier` as its first optional parameter.
   - Follow strict **state hoisting**: expose stateless composables (`MessagesList`, `DialogsList`, `MessageInput`) alongside optional state-holder overloads (`MessagesListState`, `DialogsListState`) created via `remember*State()`.
   - Mark public immutable model types and style classes with `@Immutable` or `@Stable`.
   - Use Material 3 color, typography, and shape tokens via `ChatKitDefaults` (`MaterialTheme.colorScheme`, `MaterialTheme.typography`, `MaterialTheme.shapes`).
   - Add `semantics` / `contentDescription` for TalkBack accessibility and ensure touch targets meet the `48.dp` minimum where interactive.

2. **Effortless Migration from `stfalcon-studio/ChatKit`**
   - Keep familiar interface contracts: `IUser`, `IMessage`, `IDialog`, `MessageContentType`.
   - Keep familiar state-holder method ergonomics (`addToStart`, `addToEnd`, `update`, `deleteById`, `unselectAllItems`, `sortByLastMessage`, `updateDialogWithMessage`) so teams migrating from `MessagesListAdapter` and `DialogsListAdapter` can adopt Compose incrementally.
   - Keep image loading library-agnostic via `ChatKitImageLoader` and `LocalChatKitImageLoader` (works out of the box with initials/placeholders, or plugs cleanly into Coil/Glide).

3. **Verification & Quality Gates**
   - Before every commit or pull request, run:
     ```powershell
     .\gradlew.bat testDebugUnitTest assembleDebug
     ```
   - Code formatting must conform to `.editorconfig` and `kotlin.code.style=official`.

---

## Git Workflow

- Branch from `main` using descriptive prefixes: `feat/<topic>`, `fix/<topic>`, `docs/<topic>`, `refactor/<topic>`, `test/<topic>`, `chore/<topic>`.
- Write atomic commits using **Conventional Commits** (`feat(chatkit): ...`, `fix(messages): ...`, `docs(migration): ...`, `test(chatkit): ...`).
- Never commit `local.properties`, `.gradle/`, `build/`, or generated APKs.
- Keep license headers and attribution accurate (`Apache-2.0`, clean-room implementation inspired by `stfalcon-studio/ChatKit`).

