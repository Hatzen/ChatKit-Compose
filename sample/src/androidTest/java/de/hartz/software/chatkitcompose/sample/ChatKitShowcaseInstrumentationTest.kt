package de.hartz.software.chatkitcompose.sample

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class ChatKitShowcaseInstrumentationTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun launchApp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SHOW_WHEN_LOCKED, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        scenario = ActivityScenario.launch(intent)
        scenario.onActivity { }
        composeRule.waitUntil(timeoutMillis = 5_000L) {
            composeRule.onAllNodesWithText("ChatKit-Compose").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }

    @Test
    fun dialogsList_supportsLightDarkThemeAndLiveReordering() {
        composeRule.onNodeWithText("ChatKit-Compose").assertIsDisplayed()
        composeRule.onNodeWithText("Alice Meyer").assertIsDisplayed()
        composeRule.onNodeWithText("Android Architecture Team").assertIsDisplayed()

        ensureLightTheme()
        saveScreenshot("01_dialogs_light.png")

        composeRule.onNodeWithTag("theme_toggle_button").performClick()
        composeRule.waitForIdle()
        saveScreenshot("02_dialogs_dark.png")

        composeRule.onNodeWithTag("simulate_ping_fab").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Ping #1 from Bob via Compose!").assertIsDisplayed()
    }

    @Test
    fun messagesList_rendersRichContentMaskingAndThemes() {
        ensureLightTheme()
        composeRule.onNodeWithText("Alice Meyer").performClick()
        composeRule.waitForIdle()

        // Masked message tap-to-reveal improvement
        composeRule.onNodeWithText("Hidden message • Tap to reveal").assertIsDisplayed()
        saveScreenshot("03_chat_rich_light.png")

        composeRule.onNodeWithText("Hidden message • Tap to reveal").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Spoiler: Zero ViewHolder XML required anymore!").assertIsDisplayed()

        // Switch to Dark theme inside chat
        composeRule.onNodeWithTag("theme_toggle_button").performClick()
        composeRule.waitForIdle()
        saveScreenshot("04_chat_rich_dark.png")

        // Switch back to Light theme and scroll to Poll, Location, Contact, Document
        composeRule.onNodeWithTag("theme_toggle_button").performClick()
        composeRule.waitForIdle()

        composeRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Brandenburg Gate"))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Brandenburg Gate").assertIsDisplayed()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Interactive Voice Waveform"))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Interactive Voice Waveform").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("✓ Interactive Voice Waveform").assertIsDisplayed()
        saveScreenshot("05_chat_poll_location_contact.png")

        composeRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Encryption keys refreshed & verified"))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("ChatKit_Compose_Architecture.pdf").assertIsDisplayed()
        composeRule.onNodeWithText("Bob Tanaka").assertIsDisplayed()
        saveScreenshot("09_chat_document_contact_security.png")
    }

    @Test
    fun messageInput_supportsAttachmentsVoiceRecordingAndAnimationToggle() {
        ensureLightTheme()
        composeRule.onNodeWithText("Alice Meyer").performClick()
        composeRule.waitForIdle()

        // Animation toggle turns GIF AUTO -> STATIC
        composeRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("AUTO"))
        composeRule.onNodeWithText("AUTO").assertIsDisplayed()
        composeRule.onNodeWithTag("animation_toggle_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("STATIC").assertIsDisplayed()
        composeRule.onNodeWithTag("animation_toggle_button").performClick()
        composeRule.waitForIdle()

        // Expandable attachment picker tray
        composeRule.onNodeWithContentDescription("Add attachment").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Poll").assertIsDisplayed()
        composeRule.onNodeWithText("Location").assertIsDisplayed()
        composeRule.onNodeWithText("Contact").assertIsDisplayed()
        saveScreenshot("06_chat_attachments_tray.png")

        // Collapse tray and verify voice recording UI
        composeRule.onNodeWithContentDescription("Add attachment").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Record voice message").performClick()
        composeRule.mainClock.advanceTimeBy(1200L)
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Send voice message").assertIsDisplayed()
        saveScreenshot("07_chat_voice_recording.png")

        composeRule.onNodeWithContentDescription("Send voice message").performClick()
        composeRule.waitForIdle()

        // Verify >140 char chunk counter
        val longText = "A".repeat(155)
        composeRule.onNode(hasSetTextAction()).performTextInput(longText)
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("2 messages (155 chars)").onFirst().assertIsDisplayed()
    }

    @Test
    fun profileSection_displaysIdentitySecurityAndGroupPrefixes() {
        ensureLightTheme()
        composeRule.onNodeWithText("Alice Meyer").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("profile_header_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Profile & Security").assertIsDisplayed()
        composeRule.onNodeWithText("Verified Identity").assertIsDisplayed()
        composeRule.onNodeWithText("Refresh Encryption Keys").assertIsDisplayed()
        saveScreenshot("08_profile_section.png")

        composeRule.onNodeWithTag("profile_back_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("chat_back_button").performClick()
        composeRule.waitForIdle()

        // Verify group chat author name formatting ("MAYBE: Bob Tanaka")
        composeRule.onNodeWithText("Android Architecture Team").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodes(hasText("MAYBE: Bob Tanaka")).onFirst().assertIsDisplayed()
    }

    private fun ensureLightTheme() {
        val isCurrentlyDark = composeRule
            .onAllNodes(hasText("Switch to light theme", substring = false))
            .fetchSemanticsNodes()
            .isNotEmpty()
        // ContentDescription match check
        val hasLightIcon = runCatching {
            composeRule.onNodeWithContentDescription("Switch to light theme").assertIsDisplayed()
            true
        }.getOrDefault(false)
        if (hasLightIcon || isCurrentlyDark) {
            composeRule.onNodeWithTag("theme_toggle_button").performClick()
            composeRule.waitForIdle()
        }
    }

    private fun saveScreenshot(fileName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        composeRule.waitForIdle()
        val bitmap = composeRule.onRoot().captureToImage().asAndroidBitmap()
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(targetContext.filesDir, "screenshots").apply { mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        assertTrue("Screenshot $fileName should exist and be non-empty", file.exists() && file.length() > 0L)
    }
}
