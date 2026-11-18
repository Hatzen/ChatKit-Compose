# Contributing to ChatKit-Compose

Thank you for helping build a modern, clean-room Jetpack Compose successor to [stfalcon-studio/ChatKit](https://github.com/stfalcon-studio/ChatKit)!

## Git Workflow

1. Create a branch from `main` using a descriptive prefix:
   - `feat/<feature-name>`
   - `fix/<bug-name>`
   - `docs/<topic>`
   - `refactor/<topic>`
   - `test/<topic>`
   - `chore/<topic>`
2. Follow **Conventional Commits** (`feat(messages): ...`, `fix(dialogs): ...`, `docs(migration): ...`).
3. Keep pull requests focused on a single concern and fill out the PR template.

## Code Style & Compose Standards

- Follow [Google's Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide) and [Jetpack Compose API Guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md).
- Formatting is defined in [`.editorconfig`](./.editorconfig) and static rules in [`config/detekt/detekt.yml`](./config/detekt/detekt.yml).
- Every public UI composable must accept `modifier: Modifier = Modifier` as its first optional parameter and return `Unit`.
- Ensure clean-room originality: do not copy source code verbatim from `stfalcon-studio/ChatKit`. All code is licensed under Apache License 2.0.

## Verification Checklist

Run the full unit test and build suite before opening a PR:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

