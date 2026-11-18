package de.hartz.software.chatkitcompose.models

import androidx.compose.runtime.Immutable

/**
 * Contract representing a chat participant or message author.
 *
 * Designed to mirror `com.stfalcon.chatkit.commons.models.IUser` from legacy ChatKit
 * so existing user models can migrate with minimal changes.
 */
interface IUser {
    /** Unique identifier for the user. */
    val id: String

    /** Display name shown in dialogs, headers, and group chat bubbles. */
    val name: String

    /** Optional URL or URI pointing to the user's avatar image. */
    val avatar: String?

    /** Optional presence indicator shown on avatars in modern chat UIs. */
    val isOnline: Boolean
        get() = false
}

/**
 * Ready-to-use immutable implementation of [IUser].
 */
@Immutable
data class ChatUser(
    override val id: String,
    override val name: String,
    override val avatar: String? = null,
    override val isOnline: Boolean = false,
) : IUser

