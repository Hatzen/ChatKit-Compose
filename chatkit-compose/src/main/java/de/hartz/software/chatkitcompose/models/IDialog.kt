package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Immutable

/**
 * Contract representing a conversation thread (1-on-1 or group chat).
 *
 * Mirrors `com.stfalcon.chatkit.commons.models.IDialog` from legacy ChatKit.
 */
interface IDialog<out M : IMessage> {
    /** Unique identifier for the dialog. */
    val id: String

    /** Optional URL or URI for the conversation photo. */
    val dialogPhoto: String?

    /** Display title of the conversation or group. */
    val dialogName: String

    /** Participants belonging to this dialog. */
    val users: List<IUser>

    /** Most recent message in the conversation, if any. */
    val lastMessage: M?

    /** Number of unread messages for the current user. */
    val unreadCount: Int
}

/**
 * Ready-to-use immutable implementation of [IDialog].
 */
@Immutable
data class ChatDialog<out M : IMessage>(
    override val id: String,
    override val dialogPhoto: String? = null,
    override val dialogName: String,
    override val users: List<IUser> = emptyList(),
    override val lastMessage: M? = null,
    override val unreadCount: Int = 0,
) : IDialog<M>

