package de.hartz.software.chatkitcompose.utils

import java.util.Locale

/**
 * Extension and MIME-type classifier for incoming/outgoing file attachments.
 *
 * Centralizes the extension mapping logic originally maintained in ParAnnoying's
 * `FileMessage` so apps can classify files cleanly without custom ViewHolder boilerplate.
 */
object ChatFileTypeDetector {

    enum class Kind {
        IMAGE,
        GIF,
        VIDEO,
        VOICE,
        CONTACT,
        LOCATION,
        POLL,
        DOCUMENT,
    }

    private val imageExtensions = setOf("JPG", "JPEG", "PNG", "WEBP", "BMP", "SVG", "HEIC")
    private val gifExtensions = setOf("GIF")
    private val videoExtensions = setOf("MP4", "AVI", "MKV", "WEBM", "MOV", "3GP")
    private val voiceExtensions = setOf("MP3", "WAV", "OGG", "M4A", "AAC", "FLAC", "OPUS")
    private val contactExtensions = setOf("VCF", "VCARD", "CONTACT")
    private val locationExtensions = setOf("GPX", "KML", "GEOJSON", "MAP", "LOC")
    private val pollExtensions = setOf("SURVEY", "POLL")

    /**
     * Detects the [Kind] from a filename/path and optional [mimeType].
     */
    fun detectKind(
        fileNameOrPath: String,
        mimeType: String? = null,
    ): Kind {
        val normalizedMime = mimeType?.lowercase(Locale.ROOT).orEmpty()
        when {
            normalizedMime == "image/gif" -> return Kind.GIF
            normalizedMime.startsWith("image/") -> return Kind.IMAGE
            normalizedMime.startsWith("video/") -> return Kind.VIDEO
            normalizedMime.startsWith("audio/") -> return Kind.VOICE
            normalizedMime == "text/vcard" || normalizedMime == "text/x-vcard" -> return Kind.CONTACT
            normalizedMime == "application/geo+json" || normalizedMime == "application/gpx+xml" -> return Kind.LOCATION
        }

        val cleanName = fileNameOrPath.substringAfterLast('/').substringAfterLast('\\')
        val effectiveExt = extractEffectiveExtension(cleanName)

        return when (effectiveExt) {
            in gifExtensions -> Kind.GIF
            in imageExtensions -> Kind.IMAGE
            in videoExtensions -> Kind.VIDEO
            in voiceExtensions -> Kind.VOICE
            in contactExtensions -> Kind.CONTACT
            in locationExtensions -> Kind.LOCATION
            in pollExtensions -> Kind.POLL
            else -> Kind.DOCUMENT
        }
    }

    /**
     * Extracts a human-friendly uppercase file extension badge (e.g., `"PDF"`, `"ZIP"`).
     */
    fun extractExtensionBadge(fileName: String): String {
        val ext = extractEffectiveExtension(fileName)
        return ext.ifBlank { "FILE" }.take(5)
    }

    /**
     * Formats byte sizes consistently for document and media bubbles.
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        return when {
            bytes < 1024L -> "$bytes B"
            bytes < 1024L * 1024L -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
            bytes < 1024L * 1024L * 1024L -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
            else -> String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    private fun extractEffectiveExtension(fileName: String): String {
        val candidate = if (fileName.endsWith(".txt", ignoreCase = true) &&
            fileName.dropLast(4).contains('.')
        ) {
            fileName.dropLast(4).substringAfterLast('.', "")
        } else {
            fileName.substringAfterLast('.', "")
        }
        return candidate.uppercase(Locale.ROOT).trim()
    }
}

