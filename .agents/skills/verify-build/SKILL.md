---
name: verify-build
description: >-
  Run Gradle unit tests and debug APK/AAR builds to verify compilation, Compose compiler checks,
  and test suites across :chatkit-compose and :sample before committing.
---

# Verify Build & Tests

Use this runbook whenever you modify Kotlin source code, Gradle build scripts, or resources in `:chatkit-compose` or `:sample`.

## Steps

1. Run unit tests and debug assembly from the repository root:
   ```powershell
   .\gradlew.bat testDebugUnitTest assembleDebug
   ```
2. Confirm all unit tests in `:chatkit-compose:testDebugUnitTest` pass with zero failures or skipped assertions.
3. Confirm both `:chatkit-compose:assembleDebug` (AAR) and `:sample:assembleDebug` (APK) succeed cleanly.
4. Optionally verify publishing artifacts locally when changing Maven/JitPack config:
   ```powershell
   .\gradlew.bat :chatkit-compose:publishToMavenLocal
   ```

