package de.hartz.software.chatkitcompose.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.models.IUser
import de.hartz.software.chatkitcompose.theme.ChatAvatar
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import de.hartz.software.chatkitcompose.theme.LocalChatKitAnimationConfig

/**
 * Quick action button displayed directly beneath the hero header in [ChatProfileSection].
 */
@Immutable
data class ChatProfileQuickAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

/**
 * Declarative row types supported inside [ChatProfileSection], inspired by ParAnnoying's
 * `UserProfileItem` and expanded for Compose Material 3 profile screens.
 */
sealed interface ChatProfileItem {
    data class SectionHeader(val title: String) : ChatProfileItem

    data class StaticInfo(
        val label: String?,
        val value: String,
        val isMonospace: Boolean = false,
        val isWarning: Boolean = false,
    ) : ChatProfileItem

    data class Action(
        val text: String,
        val description: String? = null,
        val icon: ImageVector? = null,
        val isDestructive: Boolean = false,
        val onClick: () -> Unit,
    ) : ChatProfileItem

    data class MemberRow(
        val user: IUser,
        val roleLabel: String? = null,
        val subtitle: String? = null,
        val isVerified: Boolean = true,
        val onClick: (() -> Unit)? = null,
    ) : ChatProfileItem
}

/**
 * Customizable Material 3 user or group profile screen/section.
 *
 * Displays a hero avatar, title, subtitle, optional verification badge, quick actions,
 * and structured [items] (info key/value cards, clickable actions, security notices, and
 * group members).
 */
@Composable
fun ChatProfileSection(
    title: String,
    avatarUrl: String?,
    items: List<ChatProfileItem>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isOnline: Boolean = false,
    verificationBadge: String? = null,
    quickActions: List<ChatProfileQuickAction> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    headerExtraContent: (@Composable () -> Unit)? = null,
) {
    val animConfig = LocalChatKitAnimationConfig.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item(key = "profile_hero_header", contentType = "profile_hero") {
            ProfileHeroHeader(
                title = title,
                avatarUrl = avatarUrl,
                subtitle = subtitle,
                isOnline = isOnline,
                verificationBadge = verificationBadge,
                quickActions = quickActions,
                headerExtraContent = headerExtraContent,
            )
        }

        items(
            items = items,
            contentType = { it::class.simpleName ?: "profile_item" },
        ) { item ->
            val animModifier = if (animConfig.shouldAnimateItemPlacement) {
                Modifier.animateItem()
            } else {
                Modifier
            }
            when (item) {
                is ChatProfileItem.SectionHeader -> ProfileSectionHeaderRow(
                    title = item.title,
                    modifier = animModifier,
                )
                is ChatProfileItem.StaticInfo -> ProfileStaticInfoRow(
                    item = item,
                    modifier = animModifier,
                )
                is ChatProfileItem.Action -> ProfileActionRow(
                    item = item,
                    modifier = animModifier,
                )
                is ChatProfileItem.MemberRow -> ProfileMemberRow(
                    item = item,
                    modifier = animModifier,
                )
            }
        }
    }
}

@Composable
private fun ProfileHeroHeader(
    title: String,
    avatarUrl: String?,
    subtitle: String?,
    isOnline: Boolean,
    verificationBadge: String?,
    quickActions: List<ChatProfileQuickAction>,
    headerExtraContent: (@Composable () -> Unit)?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ChatAvatar(
            name = title,
            imageUrl = avatarUrl,
            size = 88.dp,
            isOnline = isOnline,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (!verificationBadge.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Icon(
                        imageVector = ChatKitIcons.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = verificationBadge,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }

        if (quickActions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                quickActions.forEach { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        FilledTonalIconButton(
                            onClick = action.onClick,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                            )
                        }
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        headerExtraContent?.let {
            Spacer(modifier = Modifier.height(12.dp))
            it()
        }
    }
}

@Composable
private fun ProfileSectionHeaderRow(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 4.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ProfileStaticInfoRow(
    item: ChatProfileItem.StaticInfo,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = if (item.isWarning) {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            if (!item.label.isNullOrBlank()) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (item.isWarning) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(
                text = item.value,
                style = if (item.isMonospace) {
                    MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                color = if (item.isWarning) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Composable
private fun ProfileActionRow(
    item: ChatProfileItem.Action,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (item.isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = item.onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (item.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
            )
            if (!item.description.isNullOrBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProfileMemberRow(
    item: ChatProfileItem.MemberRow,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .let { base -> if (item.onClick != null) base.clickable(onClick = item.onClick) else base }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ChatAvatar(
            name = item.user.name,
            imageUrl = item.user.avatar,
            size = 40.dp,
            isOnline = item.user.isOnline,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.user.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.subtitle.isNullOrBlank()) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (!item.roleLabel.isNullOrBlank()) {
            Surface(
                color = if (item.isVerified) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                },
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(
                        text = item.roleLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.isVerified) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        },
                    )
                }
            }
        }
    }
}

