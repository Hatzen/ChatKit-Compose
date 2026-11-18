package de.hartz.software.chatkitcompose.messages

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.hartz.software.chatkitcompose.models.IUser
import de.hartz.software.chatkitcompose.models.MessageContentType
import de.hartz.software.chatkitcompose.theme.ChatAvatar
import de.hartz.software.chatkitcompose.theme.ChatKitIcons
import de.hartz.software.chatkitcompose.theme.LocalChatKitAnimationConfig
import de.hartz.software.chatkitcompose.theme.LocalChatKitImageLoader
import de.hartz.software.chatkitcompose.theme.MessagesListStyle
import de.hartz.software.chatkitcompose.utils.ChatFileTypeDetector
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.roundToInt

@Composable
internal fun ImageAttachmentContent(
    image: MessageContentType.Image,
    description: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val ratio = (image.aspectRatio ?: (4f / 3f)).coerceIn(0.6f, 2.5f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 230.dp)
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(12.dp))
            .alpha(if (image.isAvailable) 1f else 0.55f)
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
    ) {
        LocalChatKitImageLoader.current.LoadImage(
            url = image.imageUrl,
            contentDescription = description,
            modifier = Modifier.fillMaxSize(),
        )

        if (!image.isAvailable) {
            UnavailableMediaOverlay()
        } else if (image.additionalFilesCount > 0) {
            OverlayBadge(
                text = "+${image.additionalFilesCount} files",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
            )
        }
    }
}

@Composable
internal fun GifAttachmentContent(
    gif: MessageContentType.Gif,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val animEnabled = LocalChatKitAnimationConfig.current.shouldAnimateTransitions
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 220.dp)
            .aspectRatio(gif.aspectRatio.coerceIn(0.75f, 2.2f))
            .clip(RoundedCornerShape(12.dp))
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
    ) {
        LocalChatKitImageLoader.current.LoadImage(
            url = gif.gifUrl,
            contentDescription = "GIF attachment",
            modifier = Modifier.fillMaxSize(),
        )
        Surface(
            color = Color.Black.copy(alpha = 0.68f),
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Icon(
                    imageVector = ChatKitIcons.Gif,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = if (animEnabled) "AUTO" else "STATIC",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
internal fun VideoAttachmentContent(
    video: MessageContentType.Video,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 130.dp, max = 220.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .alpha(if (video.isAvailable) 1f else 0.55f)
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
    ) {
        LocalChatKitImageLoader.current.LoadImage(
            url = video.thumbnailUrl ?: video.videoUrl,
            contentDescription = "Video attachment",
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.65f),
                contentColor = Color.White,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = ChatKitIcons.PlayArrow,
                        contentDescription = "Play video",
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }

        if (video.durationSeconds > 0 || !video.fileSizeText.isNullOrBlank()) {
            val parts = listOfNotNull(
                video.durationSeconds.takeIf { it > 0 }?.let { formatDuration(it) },
                video.fileSizeText,
            )
            OverlayBadge(
                text = parts.joinToString(" • "),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
            )
        }

        if (!video.isAvailable) {
            UnavailableMediaOverlay()
        } else if (video.additionalFilesCount > 0) {
            OverlayBadge(
                text = "+${video.additionalFilesCount} files",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
            )
        }
    }
}

@Composable
internal fun VoiceMessageContent(
    voice: MessageContentType.Voice,
    textColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val animateWaveform = LocalChatKitAnimationConfig.current.shouldAnimateVoiceWaveform
    var isPlaying by remember(voice.id) { mutableStateOf(false) }
    var progress by remember(voice.id) { mutableFloatStateOf(0f) }
    var speedMultiplier by remember(voice.id) { mutableFloatStateOf(1f) }

    LaunchedEffect(isPlaying, speedMultiplier, voice.durationSeconds) {
        if (!isPlaying) return@LaunchedEffect
        val totalMs = (voice.durationSeconds.coerceAtLeast(1) * 1000L / speedMultiplier).toLong().coerceAtLeast(300L)
        val tickMs = 60L
        val step = tickMs.toFloat() / totalMs.toFloat()
        while (isPlaying) {
            delay(tickMs)
            val next = progress + step
            if (next >= 1f) {
                progress = 0f
                isPlaying = false
            } else {
                progress = next
            }
        }
    }

    val bars = voice.waveform.ifEmpty { MessageContentType.DEFAULT_WAVEFORM }
    val elapsedSeconds = (voice.durationSeconds * progress).roundToInt()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilledTonalIconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = if (isPlaying) ChatKitIcons.Pause else ChatKitIcons.PlayArrow,
                contentDescription = if (isPlaying) "Pause voice note" else "Play voice note",
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                bars.forEachIndexed { index, amplitude ->
                    val threshold = (index + 1).toFloat() / bars.size.toFloat()
                    val isPlayed = progress >= threshold
                    val targetHeight = (amplitude.coerceIn(0.15f, 1f) * 24f)
                    val animatedHeight by animateFloatAsState(
                        targetValue = if (isPlaying && animateWaveform) {
                            (targetHeight * (0.85f + 0.3f * ((index % 3) / 2f))).coerceAtMost(26f)
                        } else {
                            targetHeight
                        },
                        animationSpec = tween(durationMillis = 180),
                        label = "voiceBarHeight",
                    )
                    val finalHeight = if (animateWaveform) animatedHeight else targetHeight

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(finalHeight.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isPlayed) accentColor else textColor.copy(alpha = 0.28f),
                            )
                            .clickable { progress = threshold },
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isPlaying || progress > 0f) {
                        "${formatDuration(elapsedSeconds)} / ${formatDuration(voice.durationSeconds)}"
                    } else {
                        formatDuration(voice.durationSeconds)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.78f),
                )

                Surface(
                    color = accentColor.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            speedMultiplier = when (speedMultiplier) {
                                1f -> 1.5f
                                1.5f -> 2f
                                else -> 1f
                            }
                        },
                ) {
                    Text(
                        text = if (speedMultiplier == 1.5f) "1.5x" else "${speedMultiplier.toInt()}x",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun PollMessageContent(
    poll: MessageContentType.Poll,
    textColor: Color,
    accentColor: Color,
    onVoteOption: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val totalVotes = poll.options.sumOf { it.votes }.coerceAtLeast(0)
    val animEnabled = LocalChatKitAnimationConfig.current.shouldAnimateTransitions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = ChatKitIcons.Poll,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = poll.question,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
            )
        }

        poll.options.forEach { option ->
            val isSelected = option.id in poll.selectedOptionIds
            val fraction = if (totalVotes > 0) option.votes.toFloat() / totalVotes.toFloat() else 0f
            val animatedFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(durationMillis = if (animEnabled) 320 else 0),
                label = "pollFraction",
            )
            val fillFraction = if (animEnabled) animatedFraction else fraction
            val percent = (fraction * 100f).roundToInt()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) accentColor else textColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.45f))
                    .let { base ->
                        if (onVoteOption != null) base.clickable { onVoteOption(option.id) } else base
                    },
            ) {
                Box(modifier = Modifier.matchParentSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fillFraction)
                            .background(
                                if (isSelected) accentColor.copy(alpha = 0.22f) else accentColor.copy(alpha = 0.10f),
                            ),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isSelected) "✓ ${option.label}" else option.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        ),
                        color = textColor,
                        modifier = Modifier.weight(1f),
                    )
                    if (totalVotes > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) accentColor else textColor.copy(alpha = 0.72f),
                        )
                    }
                }
            }
        }

        Text(
            text = if (totalVotes == 1) "1 vote" else "$totalVotes votes",
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.72f),
        )
    }
}

@Composable
internal fun LocationMessageContent(
    location: MessageContentType.Location,
    textColor: Color,
    accentColor: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val mapBg = MaterialTheme.colorScheme.surfaceVariant
    val roadColor = MaterialTheme.colorScheme.surface
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(mapBg),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = size.width / 6f
                for (i in 1..5) {
                    val x = step * i
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5f)
                }
                val hStep = size.height / 4f
                for (i in 1..3) {
                    val y = hStep * i
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
                }
                val road = Path().apply {
                    moveTo(0f, size.height * 0.75f)
                    cubicTo(
                        size.width * 0.3f,
                        size.height * 0.25f,
                        size.width * 0.65f,
                        size.height * 0.85f,
                        size.width,
                        size.height * 0.30f,
                    )
                }
                drawPath(road, color = roadColor, style = Stroke(width = 14f, cap = StrokeCap.Round))
            }

            Surface(
                shape = CircleShape,
                color = accentColor,
                contentColor = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(38.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = ChatKitIcons.LocationOn,
                        contentDescription = "Location pin",
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            OverlayBadge(
                text = String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp),
            )
        }

        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = location.placeName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!location.address.isNullOrBlank()) {
                Text(
                    text = location.address!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.78f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
internal fun ContactMessageContent(
    contact: MessageContentType.Contact,
    textColor: Color,
    accentColor: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ChatAvatar(
                name = contact.contactName,
                imageUrl = contact.contactAvatarUrl,
                size = 42.dp,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.contactName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = listOfNotNull(contact.organization, contact.phoneNumber, contact.email)
                    .firstOrNull()
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.78f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (!contact.verificationHash.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        Icon(
                            imageVector = ChatKitIcons.Lock,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = contact.verificationHash!!.take(12),
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = accentColor,
                        )
                    }
                }
            }
            Icon(
                imageVector = ChatKitIcons.ContactPage,
                contentDescription = "Open contact",
                tint = accentColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
internal fun DocumentMessageContent(
    document: MessageContentType.Document,
    textColor: Color,
    accentColor: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val badge = ChatFileTypeDetector.extractExtensionBadge(document.fileName)
    val sizeInfo = if (!document.isAvailable) {
        "Deleted / Not found"
    } else if (document.fileSizeBytes > 0L) {
        ChatFileTypeDetector.formatFileSize(document.fileSizeBytes)
    } else {
        "Tap to open"
    }

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (document.isAvailable) 1f else 0.6f)
            .clip(RoundedCornerShape(12.dp))
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.16f),
                contentColor = accentColor,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(44.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = ChatKitIcons.Description,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.fileName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val extraSuffix = if (document.additionalFilesCount > 0) {
                    " • +${document.additionalFilesCount} files"
                } else {
                    ""
                }
                Text(
                    text = "$sizeInfo$extraSuffix",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.74f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
internal fun SystemEventChip(
    event: MessageContentType.SystemEvent,
    style: MessagesListStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = style.dateHeaderContainerColor.copy(alpha = 0.85f),
            shape = RoundedCornerShape(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                if (event.isSecurityEvent) {
                    Icon(
                        imageVector = ChatKitIcons.Lock,
                        contentDescription = null,
                        tint = style.dateHeaderTextColor,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Text(
                    text = event.eventText,
                    style = MaterialTheme.typography.labelMedium,
                    color = style.dateHeaderTextColor,
                )
            }
        }
    }
}

@Composable
internal fun TypingIndicatorBubble(
    typingUsers: List<IUser>,
    style: MessagesListStyle,
    modifier: Modifier = Modifier,
) {
    if (typingUsers.isEmpty()) return
    val animateTyping = LocalChatKitAnimationConfig.current.shouldAnimateTypingIndicator
    val firstUser = typingUsers.first()
    val label = if (typingUsers.size == 1) {
        "${firstUser.name} is typing"
    } else {
        "${typingUsers.size} people are typing"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .semantics { contentDescription = label },
        verticalAlignment = Alignment.Bottom,
    ) {
        if (style.showIncomingAvatar) {
            ChatAvatar(
                name = firstUser.name,
                imageUrl = firstUser.avatar,
                size = style.avatarSize,
                isOnline = firstUser.isOnline,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = style.incomingBubbleColor,
            shape = RoundedCornerShape(style.bubbleCornerRadius),
            tonalElevation = 1.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                if (animateTyping) {
                    val transition = rememberInfiniteTransition(label = "typingDots")
                    for (idx in 0..2) {
                        val alpha by transition.animateFloat(
                            initialValue = 0.25f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 550, delayMillis = idx * 140),
                                repeatMode = RepeatMode.Reverse,
                            ),
                            label = "dot$idx",
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(style.incomingTextColor.copy(alpha = alpha)),
                        )
                    }
                } else {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(style.incomingTextColor.copy(alpha = 0.65f)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Color.Black.copy(alpha = 0.68f),
        contentColor = Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun UnavailableMediaOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        OverlayBadge(text = "Media unavailable")
    }
}

internal fun formatDuration(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return String.format(Locale.US, "%d:%02d", mins, secs)
}

