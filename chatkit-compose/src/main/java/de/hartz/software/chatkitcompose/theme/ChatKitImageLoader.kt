package de.hartz.software.chatkitcompose.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

/**
 * Pluggable image loading contract replacing legacy `com.stfalcon.chatkit.commons.ImageLoader`.
 *
 * Implement this interface with Coil (`AsyncImage`), Glide, or any custom image pipeline
 * and supply it via [LocalChatKitImageLoader].
 */
fun interface ChatKitImageLoader {
    @Composable
    fun LoadImage(
        url: String?,
        contentDescription: String?,
        modifier: Modifier,
    )
}

/**
 * Default placeholder loader used when no external image library is configured.
 */
object DefaultChatKitImageLoader : ChatKitImageLoader {
    @Composable
    override fun LoadImage(
        url: String?,
        contentDescription: String?,
        modifier: Modifier,
    ) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp),
            ) {
                Icon(
                    imageVector = ChatKitIcons.ImagePlaceholder,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp),
                )
                if (!url.isNullOrBlank()) {
                    Text(
                        text = url.substringAfterLast('/').ifBlank { url },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * CompositionLocal providing the active [ChatKitImageLoader] to `MessagesList` and `DialogsList`.
 */
val LocalChatKitImageLoader = staticCompositionLocalOf<ChatKitImageLoader> { DefaultChatKitImageLoader }

/**
 * Convenience wrapper providing [imageLoader] to nested ChatKit composables.
 */
@Composable
fun ProvideChatKitImageLoader(
    imageLoader: ChatKitImageLoader,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalChatKitImageLoader provides imageLoader, content = content)
}

/**
 * Shared avatar composable rendering either an image via [LocalChatKitImageLoader] or a
 * deterministic Material initials badge when [imageUrl] is null or the default loader is active.
 */
@Composable
fun ChatAvatar(
    name: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    shape: Shape = CircleShape,
    isOnline: Boolean = false,
    onlineIndicatorColor: Color = Color(0xFF2E7D32),
) {
    val imageLoader = LocalChatKitImageLoader.current
    val showImage = !imageUrl.isNullOrBlank() && imageLoader !is DefaultChatKitImageLoader

    Box(modifier = modifier.size(size)) {
        if (showImage) {
            imageLoader.LoadImage(
                url = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
            )
        } else {
            val palette = avatarPalette()
            val containerColor = palette[name.hashCode().absoluteValue % palette.size]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(containerColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = extractInitials(name),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    maxLines = 1,
                )
            }
        }

        if (isOnline) {
            val indicatorSize = (size * 0.28f).coerceIn(10.dp, 14.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(onlineIndicatorColor)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            )
        }
    }
}

private fun extractInitials(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}

private fun avatarPalette(): List<Color> = listOf(
    Color(0xFF1E88E5),
    Color(0xFF00897B),
    Color(0xFF5E35B1),
    Color(0xFFD81B60),
    Color(0xFFF4511E),
    Color(0xFF3949AB),
    Color(0xFF039BE5),
    Color(0xFF43A047),
)

