package de.hartz.software.chatkitcompose.sample

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
import de.hartz.software.chatkitcompose.profile.ChatProfileItem
import de.hartz.software.chatkitcompose.profile.ChatProfileQuickAction
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import java.util.Calendar
import java.util.Date

internal object SampleData {

    val me = ChatUser(id = "me", name = "Kai Hartz", isOnline = true)
    val alice = ChatUser(id = "alice", name = "Alice Meyer", isOnline = true)
    val bob = ChatUser(id = "bob", name = "Bob Tanaka", isOnline = false)
    val carol = ChatUser(id = "carol", name = "Carol Vance", isOnline = true)

    fun initialDialogs(): List<ChatDialog<IMessage>> = listOf(
        ChatDialog(
            id = "dialog_alice",
            dialogName = alice.name,
            users = listOf(me, alice),
            lastMessage = ChatMessage(
                id = "d_alice_last",
                text = "Tap the header to inspect my customizable Profile Section!",
                user = alice,
                createdAt = minutesAgo(2),
            ),
            unreadCount = 2,
        ),
        ChatDialog(
            id = "dialog_team",
            dialogName = "Android Architecture Team",
            users = listOf(me, alice, bob, carol),
            lastMessage = PollMessage(
                id = "d_team_last",
                question = "Favorite ChatKit-Compose upgrade?",
                options = listOf(
                    PollOption("opt_1", "Material 3 Light & Dark", 12),
                    PollOption("opt_2", "Built-in Rich Message Types", 19),
                    PollOption("opt_3", "Zero ViewHolder Boilerplate", 15),
                ),
                user = carol,
                createdAt = minutesAgo(18),
                selectedOptionIds = setOf("opt_2"),
            ),
            unreadCount = 4,
        ),
        ChatDialog(
            id = "dialog_bob",
            dialogName = bob.name,
            users = listOf(me, bob),
            lastMessage = ChatMessage(
                id = "d_bob_last",
                text = "Migrated from XML ChatKit with zero domain model changes.",
                user = bob,
                createdAt = daysAgo(1, hour = 17),
            ),
            unreadCount = 0,
        ),
    )

    fun initialMessagesFor(dialog: ChatDialog<IMessage>): List<IMessage> {
        return when (dialog.id) {
            "dialog_team" -> teamThreadMessages()
            "dialog_bob" -> bobThreadMessages()
            else -> aliceShowcaseMessages()
        }
    }

    private fun aliceShowcaseMessages(): List<IMessage> {
        val intro = ChatMessage(
            id = "a_1",
            text = "Welcome to ChatKit-Compose! Everything here uses Material 3 and pure Compose state.",
            user = alice,
            createdAt = daysAgo(1, hour = 11, minute = 15),
        )
        val replyMsg = ChatMessage(
            id = "a_2",
            text = "And our legacy IUser / IMessage / IDialog interfaces work right out of the box.",
            user = me,
            createdAt = daysAgo(1, hour = 11, minute = 17),
            status = MessageStatus.READ,
            replyTo = intro,
            reactions = mapOf("🚀" to 3, "❤️" to 1),
        )
        val securityNotice = SystemEventMessage(
            id = "a_3",
            eventText = "Encryption keys refreshed & verified",
            user = alice,
            createdAt = minutesAgo(35),
            isSecurityEvent = true,
        )
        val docMsg = DocumentMessage(
            id = "a_4",
            fileName = "ChatKit_Compose_Architecture.pdf",
            filePathOrUri = "demo://architecture.pdf",
            user = alice,
            createdAt = minutesAgo(26),
            fileSizeBytes = 2_570_000L,
            additionalFilesCount = 1,
            text = "Full migration & Material 3 spec attached.",
        )
        val contactMsg = ContactMessage(
            id = "a_5",
            contactName = "Bob Tanaka",
            organization = "Security Engineering",
            phoneNumber = "+49 30 5550 1942",
            verificationHash = "9F82A1C0E44B7D12",
            user = alice,
            createdAt = minutesAgo(20),
        )
        val locationMsg = LocationMessage(
            id = "a_6",
            latitude = 52.5163,
            longitude = 13.3777,
            placeName = "Brandenburg Gate",
            address = "Pariser Platz, 10117 Berlin",
            user = me,
            createdAt = minutesAgo(16),
            status = MessageStatus.DELIVERED,
        )
        val pollMsg = PollMessage(
            id = "a_7",
            question = "Which built-in type should we demo next?",
            options = listOf(
                PollOption("p_1", "Interactive Voice Waveform", 8),
                PollOption("p_2", "Video & GIF Previews", 11),
                PollOption("p_3", "Customizable Profile Screen", 6),
            ),
            selectedOptionIds = setOf("p_2"),
            user = alice,
            createdAt = minutesAgo(12),
        )
        val voiceMsg = VoiceMessage(
            id = "a_8",
            audioUrl = "demo://voice_intro.opus",
            durationSeconds = 18,
            user = alice,
            createdAt = minutesAgo(9),
            text = "Voice note about animation toggles",
        )
        val videoMsg = VideoMessage(
            id = "a_9",
            videoUrl = "demo://compose_walkthrough.mp4",
            thumbnailUrl = "demo://video_thumb.jpg",
            durationSeconds = 42,
            fileSizeText = "14.2 MB",
            additionalFilesCount = 3,
            user = me,
            createdAt = minutesAgo(6),
            text = "Screen capture of smooth LazyColumn transitions",
            status = MessageStatus.READ,
        )
        val gifMsg = GifMessage(
            id = "a_10",
            gifUrl = "demo://celebrate.gif",
            user = alice,
            createdAt = minutesAgo(4),
            text = "GIFs obey ChatKitAnimationConfig automatically!",
        )
        val imageMsg = ChatMessage(
            id = "a_11",
            text = "Light & Dark Material 3 tokens adapt automatically.",
            user = alice,
            createdAt = minutesAgo(3),
            imageUrl = "demo://material3_palette.png",
            additionalFilesCount = 2,
        )
        val maskedMsg = ChatMessage(
            id = "a_12",
            text = "Spoiler: Zero ViewHolder XML required anymore!",
            user = alice,
            createdAt = minutesAgo(2),
            isMasked = true,
            status = MessageStatus.UNCONFIRMED,
            statusLabel = "NEW",
        )
        return listOf(
            intro,
            replyMsg,
            securityNotice,
            docMsg,
            contactMsg,
            locationMsg,
            pollMsg,
            voiceMsg,
            videoMsg,
            gifMsg,
            imageMsg,
            maskedMsg,
        )
    }

    private fun teamThreadMessages(): List<IMessage> = listOf(
        ChatMessage(
            id = "t_1",
            text = "Offline group messages show sender prefixes like MAYBE when unverified.",
            user = bob,
            createdAt = minutesAgo(30),
            status = MessageStatus.WARNING,
        ),
        ChatMessage(
            id = "t_2",
            text = "Tap any member's avatar on the left to inspect their ChatProfileSection!",
            user = alice,
            createdAt = minutesAgo(22),
        ),
        PollMessage(
            id = "t_3",
            question = "Favorite ChatKit-Compose upgrade?",
            options = listOf(
                PollOption("opt_1", "Material 3 Light & Dark", 12),
                PollOption("opt_2", "Built-in Rich Message Types", 19),
                PollOption("opt_3", "Zero ViewHolder Boilerplate", 15),
            ),
            user = carol,
            createdAt = minutesAgo(18),
            selectedOptionIds = setOf("opt_2"),
        ),
    )

    private fun bobThreadMessages(): List<IMessage> = listOf(
        ChatMessage(
            id = "b_1",
            text = "Notice how missing files stay readable with clear visual feedback.",
            user = bob,
            createdAt = daysAgo(1, hour = 16, minute = 45),
        ),
        DocumentMessage(
            id = "b_2",
            fileName = "expired_archive.zip",
            filePathOrUri = "demo://expired.zip",
            user = bob,
            createdAt = daysAgo(1, hour = 16, minute = 50),
            isAvailable = false,
        ),
        ChatMessage(
            id = "b_3",
            text = "Migrated from XML ChatKit with zero domain model changes.",
            user = bob,
            createdAt = daysAgo(1, hour = 17),
        ),
    )

    fun buildProfileItems(
        dialog: ChatDialog<IMessage>,
        focusUser: ChatUser?,
        onOpenChat: () -> Unit,
        onRefreshKeys: () -> Unit,
        onResetKeys: () -> Unit,
        onShowToast: (String) -> Unit,
        onSelectMember: (ChatUser) -> Unit,
    ): List<ChatProfileItem> {
        val targetName = focusUser?.name ?: dialog.dialogName
        val isGroup = focusUser == null && dialog.users.size > 2
        val targetId = focusUser?.id ?: dialog.id

        return buildList {
            add(ChatProfileItem.SectionHeader("Identity & Security"))
            add(ChatProfileItem.StaticInfo("Display Name", targetName))
            add(
                ChatProfileItem.StaticInfo(
                    label = "Verified Key Fingerprint",
                    value = "SHA256:${targetId.uppercase()}-7F3A-910C-E821",
                    isMonospace = true,
                ),
            )
            add(
                ChatProfileItem.StaticInfo(
                    label = "Encryption Status",
                    value = if (isGroup) "Group Relay • 3 verified members" else "End-to-end verified",
                ),
            )

            add(ChatProfileItem.SectionHeader("Actions"))
            add(
                ChatProfileItem.Action(
                    text = "Open Conversation",
                    description = "Return to message timeline",
                    icon = ChatKitIcons.Send,
                    onClick = onOpenChat,
                ),
            )
            add(
                ChatProfileItem.Action(
                    text = "Refresh Encryption Keys",
                    description = "Rotate ratchet tokens cleanly",
                    icon = ChatKitIcons.Refresh,
                    onClick = onRefreshKeys,
                ),
            )
            add(
                ChatProfileItem.Action(
                    text = "Reset Session Keys",
                    description = "Clear local ratchets and notify contact",
                    icon = ChatKitIcons.Key,
                    isDestructive = true,
                    onClick = onResetKeys,
                ),
            )

            if (isGroup) {
                add(ChatProfileItem.SectionHeader("Group Members (${dialog.users.size})"))
                dialog.users.forEach { member ->
                    val chatUser = member as? ChatUser
                    add(
                        ChatProfileItem.MemberRow(
                            user = member,
                            roleLabel = when (member.id) {
                                me.id -> "You"
                                bob.id -> "Unverified"
                                else -> "Verified"
                            },
                            subtitle = if (member.isOnline) "Online now" else "Last seen recently",
                            isVerified = member.id != bob.id,
                            onClick = if (chatUser != null && chatUser.id != me.id) {
                                { onSelectMember(chatUser) }
                            } else {
                                null
                            },
                        ),
                    )
                }
            } else {
                add(ChatProfileItem.SectionHeader("Security Audit"))
                add(
                    ChatProfileItem.StaticInfo(
                        label = null,
                        value = "No security warnings detected for $targetName.",
                    ),
                )
            }
        }
    }

    fun buildQuickActions(
        onMessage: () -> Unit,
        onRefreshKeys: () -> Unit,
        onInfoToast: () -> Unit,
    ): List<ChatProfileQuickAction> = listOf(
        ChatProfileQuickAction("msg", "Message", ChatKitIcons.Send, onMessage),
        ChatProfileQuickAction("keys", "Rotate Keys", ChatKitIcons.Key, onRefreshKeys),
        ChatProfileQuickAction("info", "Fingerprint", ChatKitIcons.Info, onInfoToast),
    )

    fun minutesAgo(minutes: Int): Date =
        Calendar.getInstance().apply { add(Calendar.MINUTE, -minutes) }.time

    fun daysAgo(days: Int, hour: Int = 12, minute: Int = 0): Date =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }.time
}

