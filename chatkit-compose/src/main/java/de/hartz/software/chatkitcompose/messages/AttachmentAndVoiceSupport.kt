package de.hartz.software.chatkitcompose.messages

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import de.hartz.software.chatkitcompose.utils.ChatFileTypeDetector
import kotlin.math.sin

/**
 * Attachment option categories supported by [MessageInput]'s expandable attachment picker.
 */
enum class AttachmentKind {
    IMAGE,
    GIF,
    VIDEO,
    FILE,
    POLL,
    LOCATION,
    CONTACT,
}

/**
 * Metadata extracted for a file or media item chosen via Android's document/photo pickers.
 */
@Immutable
data class ChatAttachmentFile(
    val uri: String,
    val displayName: String,
    val sizeBytes: Long,
    val mimeType: String?,
    val kind: ChatFileTypeDetector.Kind,
)

/**
 * Controller exposing system photo/video and multi-document picker launchers for Compose screens.
 */
@Stable
class ChatAttachmentLaunchers internal constructor(
    private val launchMedia: (ActivityResultContracts.PickVisualMedia.VisualMediaType) -> Unit,
    private val launchDocuments: (Array<String>) -> Unit,
) {
    /** Launches Android's Photo Picker for images (and GIFs). */
    fun pickImages() = launchMedia(ActivityResultContracts.PickVisualMedia.ImageOnly)

    /** Launches Android's Photo Picker for videos. */
    fun pickVideos() = launchMedia(ActivityResultContracts.PickVisualMedia.VideoOnly)

    /** Launches Android's Photo Picker for both images and videos. */
    fun pickImagesAndVideos() = launchMedia(ActivityResultContracts.PickVisualMedia.ImageAndVideo)

    /** Launches Android's Storage Access Framework document picker for arbitrary files. */
    fun pickDocuments(mimeTypes: Array<String> = arrayOf("*/*")) = launchDocuments(mimeTypes)
}

/**
 * Remembers Android [ActivityResultContracts] launchers that resolve file names, sizes,
 * MIME types, and [ChatFileTypeDetector.Kind] automatically before delivering [onFilesSelected].
 */
@Composable
fun rememberChatAttachmentLaunchers(
    maxVisualItems: Int = 10,
    onFilesSelected: (List<ChatAttachmentFile>) -> Unit,
): ChatAttachmentLaunchers {
    val context = LocalContext.current

    val visualLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxVisualItems.coerceAtLeast(2)),
    ) { uris ->
        if (uris.isNotEmpty()) {
            onFilesSelected(uris.map { resolveAttachmentMetadata(context, it) })
        }
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        if (uris.isNotEmpty()) {
            onFilesSelected(uris.map { resolveAttachmentMetadata(context, it) })
        }
    }

    return remember(visualLauncher, documentLauncher) {
        ChatAttachmentLaunchers(
            launchMedia = { mediaType ->
                visualLauncher.launch(PickVisualMediaRequest(mediaType))
            },
            launchDocuments = { mimeTypes ->
                documentLauncher.launch(mimeTypes)
            },
        )
    }
}

private fun resolveAttachmentMetadata(context: Context, uri: Uri): ChatAttachmentFile {
    val mimeType = context.contentResolver.getType(uri)
    var displayName = uri.lastPathSegment?.substringAfterLast('/') ?: "attachment"
    var sizeBytes = 0L

    context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (nameIdx >= 0) {
                cursor.getString(nameIdx)?.takeIf { it.isNotBlank() }?.let { displayName = it }
            }
            if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                sizeBytes = cursor.getLong(sizeIdx).coerceAtLeast(0L)
            }
        }
    }

    val kind = ChatFileTypeDetector.detectKind(displayName, mimeType)
    return ChatAttachmentFile(
        uri = uri.toString(),
        displayName = displayName,
        sizeBytes = sizeBytes,
        mimeType = mimeType,
        kind = kind,
    )
}

/**
 * State holder for the interactive voice recording UI inside [MessageInput].
 *
 * Manages recording lifecycle (`isRecording`, `elapsedSeconds`, live waveform bars) so apps can
 * plug in either `MediaRecorder` / `AudioRecord` or lightweight simulated/demo recording cleanly.
 */
@Stable
class VoiceRecorderState {
    var isRecording: Boolean by mutableStateOf(false)
        private set

    var elapsedSeconds: Int by mutableIntStateOf(0)
        private set

    private val _liveWaveform = mutableStateListOf<Float>()
    val liveWaveform: List<Float>
        get() = _liveWaveform

    /** Begins recording a new voice note. */
    fun start() {
        elapsedSeconds = 0
        _liveWaveform.clear()
        _liveWaveform.addAll(listOf(0.30f, 0.55f, 0.75f, 0.45f, 0.65f))
        isRecording = true
    }

    /** Appends a tick sample while recording (invoked internally or by an external amplitude monitor). */
    fun recordTick(amplitude: Float? = null) {
        if (!isRecording) return
        val nextAmp = amplitude ?: (0.35f + 0.55f * kotlin.math.abs(sin((_liveWaveform.size + 1) * 0.7f)))
        _liveWaveform.add(nextAmp.coerceIn(0.15f, 1f))
        if (_liveWaveform.size > 32) {
            _liveWaveform.removeAt(0)
        }
    }

    fun incrementSecond() {
        if (isRecording) {
            elapsedSeconds += 1
        }
    }

    /** Cancels and discards the current recording. */
    fun cancel() {
        isRecording = false
        elapsedSeconds = 0
        _liveWaveform.clear()
    }

    /** Stops recording and returns `(durationSeconds, normalizedWaveform)`. */
    fun stop(): Pair<Int, List<Float>> {
        val duration = elapsedSeconds.coerceAtLeast(1)
        val waveform = if (_liveWaveform.isEmpty()) {
            listOf(0.3f, 0.7f, 0.5f, 0.9f, 0.6f, 0.4f, 0.8f, 0.5f)
        } else {
            _liveWaveform.toList()
        }
        isRecording = false
        elapsedSeconds = 0
        _liveWaveform.clear()
        return duration to waveform
    }
}

/**
 * Creates and remembers a [VoiceRecorderState] for [MessageInput].
 */
@Composable
fun rememberVoiceRecorderState(): VoiceRecorderState = remember { VoiceRecorderState() }

