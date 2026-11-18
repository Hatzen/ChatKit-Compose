# Git Workflow & Commit Policy

Follow these Git workflow standards for all changes in `ChatKit-Compose`:

## 1. Branch Naming
- `feat/<short-kebab-case>`: New library features, composables, or sample screens.
- `fix/<short-kebab-case>`: Bug fixes in layout, state management, or build configuration.
- `docs/<short-kebab-case>`: README, migration guide, KDoc, or agent documentation.
- `test/<short-kebab-case>`: Unit or UI test additions/updates.
- `refactor/<short-kebab-case>`: Code structure improvements without behavior changes.
- `chore/<short-kebab-case>`: Build system, dependencies, CI, or harness updates.

## 2. Conventional Commits
Use the format: `<type>(<scope>): <imperative summary>`

Examples:
- `chore(build): initialize gradle wrapper, version catalog, and project structure`
- `feat(chatkit): implement core models, date formatter, Material 3 styles, and composables`
- `test(chatkit): add unit tests for date formatting, timeline grouping, and state adapters`
- `docs(migration): add side-by-side XML to Compose examples`

Rules:
- Keep the subject line under 72 characters in imperative mood ("add", "implement", "fix", not "added").
- Keep each commit focused and atomic (one logical concern per commit).
- Always run `.\gradlew.bat testDebugUnitTest assembleDebug` before creating a feature or fix commit.

