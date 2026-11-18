package de.hartz.software.chatkitcompose.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Dependency-free Material 24dp vector icons used across ChatKit-Compose.
 * Keeping these lightweight avoids forcing downstream consumers to bundle `material-icons-extended`.
 */
object ChatKitIcons {

    val Send: ImageVector by lazy {
        materialIcon(name = "ChatKitSend", autoMirror = true) {
            moveTo(2.01f, 21.0f)
            lineTo(23.0f, 12.0f)
            lineTo(2.01f, 3.0f)
            lineTo(2.0f, 10.0f)
            lineToRelative(15.0f, 2.0f)
            lineToRelative(-15.0f, 2.0f)
            close()
        }
    }

    val AttachFile: ImageVector by lazy {
        materialIcon(name = "ChatKitAttachFile") {
            moveTo(16.5f, 6.0f)
            verticalLineToRelative(11.5f)
            curveToRelative(0.0f, 2.21f, -1.79f, 4.0f, -4.0f, 4.0f)
            reflectiveCurveToRelative(-4.0f, -1.79f, -4.0f, -4.0f)
            verticalLineTo(5.0f)
            curveToRelative(0.0f, -1.38f, 1.12f, -2.5f, 2.5f, -2.5f)
            reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
            verticalLineToRelative(10.5f)
            curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
            reflectiveCurveToRelative(-1.0f, -0.45f, -1.0f, -1.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(10.0f)
            verticalLineToRelative(9.5f)
            curveToRelative(0.0f, 1.38f, 1.12f, 2.5f, 2.5f, 2.5f)
            reflectiveCurveToRelative(2.5f, -1.12f, 2.5f, -2.5f)
            verticalLineTo(5.0f)
            curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
            reflectiveCurveTo(7.0f, 2.79f, 7.0f, 5.0f)
            verticalLineToRelative(12.5f)
            curveToRelative(0.0f, 3.04f, 2.46f, 5.5f, 5.5f, 5.5f)
            reflectiveCurveToRelative(5.5f, -2.46f, 5.5f, -5.5f)
            verticalLineTo(6.0f)
            horizontalLineToRelative(-1.5f)
            close()
        }
    }

    val Close: ImageVector by lazy {
        materialIcon(name = "ChatKitClose") {
            moveTo(19.0f, 6.41f)
            lineTo(17.59f, 5.0f)
            lineTo(12.0f, 10.59f)
            lineTo(6.41f, 5.0f)
            lineTo(5.0f, 6.41f)
            lineTo(10.59f, 12.0f)
            lineTo(5.0f, 17.59f)
            lineTo(6.41f, 19.0f)
            lineTo(12.0f, 13.41f)
            lineTo(17.59f, 19.0f)
            lineTo(19.0f, 17.59f)
            lineTo(13.41f, 12.0f)
            close()
        }
    }

    val Reply: ImageVector by lazy {
        materialIcon(name = "ChatKitReply", autoMirror = true) {
            moveTo(10.0f, 9.0f)
            verticalLineTo(5.0f)
            lineToRelative(-7.0f, 7.0f)
            lineToRelative(7.0f, 7.0f)
            verticalLineToRelative(-4.1f)
            curveToRelative(5.0f, 0.0f, 8.5f, 1.6f, 11.0f, 5.1f)
            curveToRelative(-1.0f, -5.0f, -4.0f, -10.0f, -11.0f, -11.0f)
            close()
        }
    }

    val Check: ImageVector by lazy {
        materialIcon(name = "ChatKitCheck") {
            moveTo(9.0f, 16.17f)
            lineTo(4.83f, 12.0f)
            lineToRelative(-1.42f, 1.41f)
            lineTo(9.0f, 19.0f)
            lineTo(21.0f, 7.0f)
            lineToRelative(-1.41f, -1.41f)
            close()
        }
    }

    val DoneAll: ImageVector by lazy {
        materialIcon(name = "ChatKitDoneAll") {
            moveTo(18.0f, 7.0f)
            lineToRelative(-1.41f, -1.41f)
            lineToRelative(-6.34f, 6.34f)
            lineToRelative(1.41f, 1.41f)
            lineTo(18.0f, 7.0f)
            close()
            moveTo(22.24f, 5.59f)
            lineTo(11.66f, 16.17f)
            lineTo(7.48f, 12.0f)
            lineToRelative(-1.41f, 1.41f)
            lineTo(11.66f, 19.0f)
            lineToRelative(12.0f, -12.0f)
            lineToRelative(-1.42f, -1.41f)
            close()
            moveTo(0.41f, 13.41f)
            lineTo(6.0f, 19.0f)
            lineToRelative(1.41f, -1.41f)
            lineTo(1.83f, 12.0f)
            lineTo(0.41f, 13.41f)
            close()
        }
    }

    val Schedule: ImageVector by lazy {
        materialIcon(name = "ChatKitSchedule") {
            moveTo(11.99f, 2.0f)
            curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
            reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
            curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
            reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
            close()
            moveTo(12.0f, 20.0f)
            curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
            reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
            reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
            reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
            close()
            moveTo(12.5f, 7.0f)
            horizontalLineTo(11.0f)
            verticalLineToRelative(6.0f)
            lineToRelative(5.25f, 3.15f)
            lineToRelative(0.75f, -1.23f)
            lineToRelative(-4.5f, -2.67f)
            close()
        }
    }

    val ErrorOutline: ImageVector by lazy {
        materialIcon(name = "ChatKitErrorOutline") {
            moveTo(11.0f, 15.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-2.0f)
            close()
            moveTo(11.0f, 7.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(6.0f)
            horizontalLineToRelative(-2.0f)
            close()
            moveTo(11.99f, 2.0f)
            curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
            reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
            curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
            reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
            close()
            moveTo(12.0f, 20.0f)
            curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
            reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
            reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
            reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
            close()
        }
    }

    val ImagePlaceholder: ImageVector by lazy {
        materialIcon(name = "ChatKitImagePlaceholder") {
            moveTo(21.0f, 19.0f)
            verticalLineTo(5.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            horizontalLineTo(5.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(14.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            close()
            moveTo(8.5f, 13.5f)
            lineToRelative(2.5f, 3.01f)
            lineTo(14.5f, 12.0f)
            lineToRelative(4.5f, 6.0f)
            horizontalLineTo(5.0f)
            lineToRelative(3.5f, -4.5f)
            close()
        }
    }

    val PlayArrow: ImageVector by lazy {
        materialIcon(name = "ChatKitPlayArrow") {
            moveTo(8.0f, 5.0f)
            verticalLineToRelative(14.0f)
            lineToRelative(11.0f, -7.0f)
            close()
        }
    }

    val Pause: ImageVector by lazy {
        materialIcon(name = "ChatKitPause") {
            moveTo(6.0f, 19.0f)
            horizontalLineToRelative(4.0f)
            verticalLineTo(5.0f)
            horizontalLineTo(6.0f)
            verticalLineToRelative(14.0f)
            close()
            moveTo(14.0f, 5.0f)
            verticalLineToRelative(14.0f)
            horizontalLineToRelative(4.0f)
            verticalLineTo(5.0f)
            horizontalLineToRelative(-4.0f)
            close()
        }
    }

    val Stop: ImageVector by lazy {
        materialIcon(name = "ChatKitStop") {
            moveTo(6.0f, 6.0f)
            horizontalLineToRelative(12.0f)
            verticalLineToRelative(12.0f)
            horizontalLineTo(6.0f)
            close()
        }
    }

    val Mic: ImageVector by lazy {
        materialIcon(name = "ChatKitMic") {
            moveTo(12.0f, 14.0f)
            curveToRelative(1.66f, 0.0f, 2.99f, -1.34f, 2.99f, -3.0f)
            lineTo(15.0f, 5.0f)
            curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
            reflectiveCurveTo(9.0f, 3.34f, 9.0f, 5.0f)
            verticalLineToRelative(6.0f)
            curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
            close()
            moveTo(17.3f, 11.0f)
            curveToRelative(0.0f, 3.0f, -2.54f, 5.1f, -5.3f, 5.1f)
            reflectiveCurveTo(6.7f, 14.0f, 6.7f, 11.0f)
            horizontalLineTo(5.0f)
            curveToRelative(0.0f, 3.41f, 2.72f, 6.23f, 6.0f, 6.72f)
            verticalLineTo(21.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-3.28f)
            curveToRelative(3.28f, -0.48f, 6.0f, -3.3f, 6.0f, -6.72f)
            horizontalLineToRelative(-1.7f)
            close()
        }
    }

    val VideoCall: ImageVector by lazy {
        materialIcon(name = "ChatKitVideoCall") {
            moveTo(17.0f, 10.5f)
            verticalLineTo(7.0f)
            curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(12.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            verticalLineToRelative(-3.5f)
            lineToRelative(4.0f, 4.0f)
            verticalLineToRelative(-11.0f)
            lineToRelative(-4.0f, 4.0f)
            close()
        }
    }

    val Gif: ImageVector by lazy {
        materialIcon(name = "ChatKitGif") {
            moveTo(11.5f, 9.0f)
            horizontalLineTo(13.0f)
            verticalLineToRelative(6.0f)
            horizontalLineToRelative(-1.5f)
            verticalLineTo(9.0f)
            close()
            moveTo(9.0f, 9.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-0.6f, 0.0f, -1.0f, 0.5f, -1.0f, 1.0f)
            verticalLineToRelative(4.0f)
            curveToRelative(0.0f, 0.5f, 0.4f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(3.0f)
            curveToRelative(0.6f, 0.0f, 1.0f, -0.5f, 1.0f, -1.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(8.5f)
            verticalLineToRelative(1.5f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-3.0f)
            horizontalLineTo(10.0f)
            verticalLineTo(10.0f)
            curveToRelative(0.0f, -0.5f, -0.4f, -1.0f, -1.0f, -1.0f)
            close()
            moveTo(19.0f, 10.5f)
            verticalLineTo(9.0f)
            horizontalLineToRelative(-4.5f)
            verticalLineToRelative(6.0f)
            horizontalLineTo(16.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-1.5f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-1.0f)
            horizontalLineToRelative(3.0f)
            close()
        }
    }

    val Poll: ImageVector by lazy {
        materialIcon(name = "ChatKitPoll") {
            moveTo(19.0f, 3.0f)
            horizontalLineTo(5.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(14.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(5.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(9.0f, 17.0f)
            horizontalLineTo(7.0f)
            verticalLineToRelative(-7.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(7.0f)
            close()
            moveTo(13.0f, 17.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(10.0f)
            close()
            moveTo(17.0f, 17.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(4.0f)
            close()
        }
    }

    val LocationOn: ImageVector by lazy {
        materialIcon(name = "ChatKitLocationOn") {
            moveTo(12.0f, 2.0f)
            curveTo(8.13f, 2.0f, 5.0f, 5.13f, 5.0f, 9.0f)
            curveToRelative(0.0f, 5.25f, 7.0f, 13.0f, 7.0f, 13.0f)
            reflectiveCurveToRelative(7.0f, -7.75f, 7.0f, -13.0f)
            curveToRelative(0.0f, -3.87f, -3.13f, -7.0f, -7.0f, -7.0f)
            close()
            moveTo(12.0f, 11.5f)
            curveToRelative(-1.38f, 0.0f, -2.5f, -1.12f, -2.5f, -2.5f)
            reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
            reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
            reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
            close()
        }
    }

    val ContactPage: ImageVector by lazy {
        materialIcon(name = "ChatKitContactPage") {
            moveTo(14.0f, 2.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(16.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(12.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(8.0f)
            lineToRelative(-6.0f, -6.0f)
            close()
            moveTo(12.0f, 10.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
            reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
            reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
            reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
            close()
            moveTo(16.0f, 18.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(-0.57f)
            curveToRelative(0.0f, -0.81f, 0.48f, -1.53f, 1.22f, -1.85f)
            curveToRelative(0.85f, -0.37f, 1.79f, -0.58f, 2.78f, -0.58f)
            curveToRelative(0.99f, 0.0f, 1.93f, 0.21f, 2.78f, 0.58f)
            curveToRelative(0.74f, 0.32f, 1.22f, 1.04f, 1.22f, 1.85f)
            verticalLineTo(18.0f)
            close()
        }
    }

    val Description: ImageVector by lazy {
        materialIcon(name = "ChatKitDescription") {
            moveTo(14.0f, 2.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
            lineTo(4.0f, 20.0f)
            curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
            horizontalLineTo(18.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(8.0f)
            lineToRelative(-6.0f, -6.0f)
            close()
            moveTo(16.0f, 18.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(2.0f)
            close()
            moveTo(16.0f, 14.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(2.0f)
            close()
            moveTo(13.0f, 9.0f)
            verticalLineTo(3.5f)
            lineTo(18.5f, 9.0f)
            horizontalLineTo(13.0f)
            close()
        }
    }

    val Warning: ImageVector by lazy {
        materialIcon(name = "ChatKitWarning") {
            moveTo(1.0f, 21.0f)
            horizontalLineToRelative(22.0f)
            lineTo(12.0f, 2.0f)
            lineTo(1.0f, 21.0f)
            close()
            moveTo(13.0f, 18.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(2.0f)
            close()
            moveTo(13.0f, 14.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(4.0f)
            close()
        }
    }

    val HelpOutline: ImageVector by lazy {
        materialIcon(name = "ChatKitHelpOutline") {
            moveTo(11.0f, 18.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(2.0f)
            close()
            moveTo(12.0f, 2.0f)
            curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
            reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
            reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
            reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
            close()
            moveTo(12.0f, 20.0f)
            curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
            reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
            reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
            reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
            close()
            moveTo(12.0f, 6.0f)
            curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
            horizontalLineToRelative(2.0f)
            curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
            reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
            curveToRelative(0.0f, 2.0f, -3.0f, 1.75f, -3.0f, 5.0f)
            horizontalLineToRelative(2.0f)
            curveToRelative(0.0f, -2.25f, 3.0f, -2.5f, 3.0f, -5.0f)
            curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
            close()
        }
    }

    val Lock: ImageVector by lazy {
        materialIcon(name = "ChatKitLock") {
            moveTo(18.0f, 8.0f)
            horizontalLineToRelative(-1.0f)
            verticalLineTo(6.0f)
            curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
            reflectiveCurveTo(7.0f, 3.24f, 7.0f, 6.0f)
            verticalLineToRelative(2.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(12.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(10.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(12.0f, 17.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
            reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
            reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
            reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
            close()
            moveTo(15.1f, 8.0f)
            horizontalLineTo(8.9f)
            verticalLineTo(6.0f)
            curveToRelative(0.0f, -1.71f, 1.39f, -3.1f, 3.1f, -3.1f)
            curveToRelative(1.71f, 0.0f, 3.1f, 1.39f, 3.1f, 3.1f)
            verticalLineToRelative(2.0f)
            close()
        }
    }

    val VisibilityOff: ImageVector by lazy {
        materialIcon(name = "ChatKitVisibilityOff") {
            moveTo(12.0f, 7.0f)
            curveToRelative(2.76f, 0.0f, 5.0f, 2.24f, 5.0f, 5.0f)
            curveToRelative(0.0f, 0.65f, -0.13f, 1.26f, -0.36f, 1.83f)
            lineToRelative(2.92f, 2.92f)
            curveToRelative(1.51f, -1.26f, 2.7f, -2.89f, 3.43f, -4.75f)
            curveToRelative(-1.73f, -4.39f, -6.0f, -7.5f, -11.0f, -7.5f)
            curveToRelative(-1.4f, 0.0f, -2.74f, 0.25f, -3.98f, 0.7f)
            lineToRelative(2.16f, 2.16f)
            curveTo(10.74f, 7.13f, 11.35f, 7.0f, 12.0f, 7.0f)
            close()
            moveTo(2.0f, 4.27f)
            lineToRelative(2.28f, 2.28f)
            lineToRelative(0.46f, 0.46f)
            curveTo(3.08f, 8.3f, 1.78f, 10.02f, 1.0f, 12.0f)
            curveToRelative(1.73f, 4.39f, 6.0f, 7.5f, 11.0f, 7.5f)
            curveToRelative(1.55f, 0.0f, 3.03f, -0.3f, 4.38f, -0.84f)
            lineToRelative(0.42f, 0.42f)
            lineTo(19.73f, 22.0f)
            lineTo(21.0f, 20.73f)
            lineTo(3.27f, 3.0f)
            lineTo(2.0f, 4.27f)
            close()
            moveTo(7.53f, 9.8f)
            lineToRelative(1.55f, 1.55f)
            curveToRelative(-0.05f, 0.21f, -0.08f, 0.43f, -0.08f, 0.65f)
            curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
            curveToRelative(0.22f, 0.0f, 0.44f, -0.03f, 0.65f, -0.08f)
            lineToRelative(1.55f, 1.55f)
            curveToRelative(-0.67f, 0.33f, -1.41f, 0.53f, -2.2f, 0.53f)
            curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
            curveToRelative(0.0f, -0.79f, 0.2f, -1.53f, 0.53f, -2.2f)
            close()
        }
    }

    val Refresh: ImageVector by lazy {
        materialIcon(name = "ChatKitRefresh") {
            moveTo(17.65f, 6.35f)
            curveTo(16.2f, 4.9f, 14.21f, 4.0f, 12.0f, 4.0f)
            curveToRelative(-4.42f, 0.0f, -7.99f, 3.58f, -7.99f, 8.0f)
            reflectiveCurveToRelative(3.57f, 8.0f, 7.99f, 8.0f)
            curveToRelative(3.73f, 0.0f, 6.84f, -2.55f, 7.73f, -6.0f)
            horizontalLineToRelative(-2.08f)
            curveToRelative(-0.82f, 2.33f, -3.04f, 4.0f, -5.65f, 4.0f)
            curveToRelative(-3.31f, 0.0f, -6.0f, -2.69f, -6.0f, -6.0f)
            reflectiveCurveToRelative(2.69f, -6.0f, 6.0f, -6.0f)
            curveToRelative(1.66f, 0.0f, 3.14f, 0.69f, 4.22f, 1.78f)
            lineTo(13.0f, 11.0f)
            horizontalLineToRelative(7.0f)
            verticalLineTo(4.0f)
            lineToRelative(-2.35f, 2.35f)
            close()
        }
    }

    val Delete: ImageVector by lazy {
        materialIcon(name = "ChatKitDelete") {
            moveTo(6.0f, 19.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(8.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(6.0f)
            verticalLineToRelative(12.0f)
            close()
            moveTo(19.0f, 4.0f)
            horizontalLineToRelative(-3.5f)
            lineToRelative(-1.0f, -1.0f)
            horizontalLineToRelative(-5.0f)
            lineToRelative(-1.0f, 1.0f)
            horizontalLineTo(5.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(14.0f)
            verticalLineTo(4.0f)
            close()
        }
    }

    val Info: ImageVector by lazy {
        materialIcon(name = "ChatKitInfo") {
            moveTo(12.0f, 2.0f)
            curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
            reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
            reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
            reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
            close()
            moveTo(13.0f, 17.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-6.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(6.0f)
            close()
            moveTo(13.0f, 9.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(2.0f)
            close()
        }
    }

    val Key: ImageVector by lazy {
        materialIcon(name = "ChatKitKey") {
            moveTo(12.65f, 10.0f)
            curveTo(11.83f, 7.67f, 9.61f, 6.0f, 7.0f, 6.0f)
            curveToRelative(-3.31f, 0.0f, -6.0f, 2.69f, -6.0f, 6.0f)
            reflectiveCurveToRelative(2.69f, 6.0f, 6.0f, 6.0f)
            curveToRelative(2.61f, 0.0f, 4.83f, -1.67f, 5.65f, -4.0f)
            horizontalLineTo(17.0f)
            verticalLineToRelative(4.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineTo(12.65f)
            close()
            moveTo(7.0f, 14.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
            reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
            reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
            reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
            close()
        }
    }

    val PersonAdd: ImageVector by lazy {
        materialIcon(name = "ChatKitPersonAdd") {
            moveTo(15.0f, 12.0f)
            curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
            reflectiveCurveToRelative(-1.79f, -4.0f, -4.0f, -4.0f)
            reflectiveCurveToRelative(-4.0f, 1.79f, -4.0f, 4.0f)
            reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
            close()
            moveTo(6.0f, 10.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(4.0f)
            verticalLineToRelative(3.0f)
            horizontalLineTo(1.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-3.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(6.0f)
            close()
            moveTo(15.0f, 14.0f)
            curveToRelative(-2.67f, 0.0f, -8.0f, 1.34f, -8.0f, 4.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(16.0f)
            verticalLineToRelative(-2.0f)
            curveToRelative(0.0f, -2.66f, -5.33f, -4.0f, -8.0f, -4.0f)
            close()
        }
    }

    val ArrowBack: ImageVector by lazy {
        materialIcon(name = "ChatKitArrowBack", autoMirror = true) {
            moveTo(20.0f, 11.0f)
            horizontalLineTo(7.83f)
            lineToRelative(5.59f, -5.59f)
            lineTo(12.0f, 4.0f)
            lineToRelative(-8.0f, 8.0f)
            lineToRelative(8.0f, 8.0f)
            lineToRelative(1.41f, -1.41f)
            lineTo(7.83f, 13.0f)
            horizontalLineTo(20.0f)
            verticalLineToRelative(-2.0f)
            close()
        }
    }

    val DarkMode: ImageVector by lazy {
        materialIcon(name = "ChatKitDarkMode") {
            moveTo(12.0f, 3.0f)
            curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
            reflectiveCurveToRelative(4.03f, 9.0f, 9.0f, 9.0f)
            reflectiveCurveToRelative(9.0f, -4.03f, 9.0f, -9.0f)
            curveToRelative(0.0f, -0.46f, -0.04f, -0.92f, -0.1f, -1.36f)
            curveToRelative(-0.98f, 1.37f, -2.58f, 2.26f, -4.4f, 2.26f)
            curveToRelative(-2.98f, 0.0f, -5.4f, -2.42f, -5.4f, -5.4f)
            curveToRelative(0.0f, -1.81f, 0.89f, -3.42f, 2.26f, -4.4f)
            curveTo(12.92f, 3.04f, 12.46f, 3.0f, 12.0f, 3.0f)
            close()
        }
    }

    val LightMode: ImageVector by lazy {
        materialIcon(name = "ChatKitLightMode") {
            moveTo(12.0f, 7.0f)
            curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
            reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
            reflectiveCurveToRelative(5.0f, -2.24f, 5.0f, -5.0f)
            reflectiveCurveToRelative(-2.24f, -5.0f, -5.0f, -5.0f)
            close()
            moveTo(2.0f, 13.0f)
            horizontalLineToRelative(2.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
            horizontalLineTo(2.0f)
            curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
            reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
            close()
            moveTo(20.0f, 13.0f)
            horizontalLineToRelative(2.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
            horizontalLineToRelative(-2.0f)
            curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
            reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
            close()
            moveTo(11.0f, 2.0f)
            verticalLineToRelative(2.0f)
            curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
            reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
            verticalLineTo(2.0f)
            curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
            reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
            close()
            moveTo(11.0f, 20.0f)
            verticalLineToRelative(2.0f)
            curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
            reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
            verticalLineToRelative(-2.0f)
            curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
            reflectiveCurveToRelative(-1.0f, 0.45f, -1.0f, 1.0f)
            close()
        }
    }

    val Tune: ImageVector by lazy {
        materialIcon(name = "ChatKitTune") {
            moveTo(3.0f, 17.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(6.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(3.0f)
            close()
            moveTo(3.0f, 5.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(10.0f)
            verticalLineTo(5.0f)
            horizontalLineTo(3.0f)
            close()
            moveTo(13.0f, 21.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(-8.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(6.0f)
            horizontalLineToRelative(2.0f)
            close()
            moveTo(7.0f, 9.0f)
            verticalLineToRelative(2.0f)
            horizontalLineTo(3.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(7.0f)
            close()
            moveTo(21.0f, 13.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(11.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(10.0f)
            close()
            moveTo(15.0f, 9.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(4.0f)
            verticalLineTo(5.0f)
            horizontalLineToRelative(-4.0f)
            verticalLineTo(3.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(6.0f)
            close()
        }
    }

    val ContentCopy: ImageVector by lazy {
        materialIcon(name = "ChatKitContentCopy") {
            moveTo(16.0f, 1.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(3.0f)
            horizontalLineToRelative(12.0f)
            verticalLineTo(1.0f)
            close()
            moveTo(19.0f, 5.0f)
            horizontalLineTo(8.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(11.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(7.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(19.0f, 21.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(11.0f)
            verticalLineToRelative(14.0f)
            close()
        }
    }

    private fun materialIcon(
        name: String,
        autoMirror: Boolean = false,
        block: PathBuilder.() -> Unit,
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror,
    ).apply {
        path(fill = SolidColor(Color.Black), pathBuilder = block)
    }.build()
}

