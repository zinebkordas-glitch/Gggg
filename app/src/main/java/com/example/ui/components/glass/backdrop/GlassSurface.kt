package com.example.ui.components.glass.backdrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.screens.LocalHazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

/**
 * Authentic Frosted Glass Surface with Bilateral Tapering Edge Light.
 *
 * Implements:
 * 1. 24dp real-time frosted backdrop blur.
 * 2. Unbroken, natural corner flow without abrupt clipping or straight cuts.
 * 3. Top Edge Light: begins as a soft hairline at the bottom-left curve, swells to maximum radiance across the top shelf, and tapers/thins out to 0 at the top-right curve.
 * 4. Bottom Edge Light: begins as a soft hairline at the top-right curve, swells to maximum radiance across the bottom shelf, and tapers/thins out to 0 at the bottom-left curve.
 * 5. All 4 terminations fade and thin out gracefully into the glass body.
 */
@Composable
fun GlassSurface(
    shape: RoundedCornerShape,
    isDark: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 24.dp,
    refractionStrength: Float = 0f,
    chromaticAberration: Float = 0f
) {
    val hazeState = LocalHazeState.current

    val drawEdgeLight: DrawScope.() -> Unit = {
        val cornerPx = size.height * 0.5f
        val strokePx = 1.2.dp.toPx()
        val thinStrokePx = 0.6.dp.toPx()

        // 1. Subtle unbroken ambient glass perimeter contour
        drawRoundRect(
            color = Color.White.copy(alpha = if (isDark) 0.12f else 0.22f),
            cornerRadius = CornerRadius(cornerPx, cornerPx),
            style = Stroke(width = strokePx)
        )

        // 2. Top Edge Light Path: sweeps left arc, top horizontal line, and wraps into top-right corner
        val topPath = Path().apply {
            arcTo(
                rect = Rect(0f, 0f, cornerPx * 2f, size.height),
                startAngleDegrees = 160f,
                sweepAngleDegrees = 110f,
                forceMoveTo = true
            )
            lineTo(size.width - cornerPx, 0f)
            arcTo(
                rect = Rect(size.width - cornerPx * 2f, 0f, size.width, size.height),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 60f,
                forceMoveTo = false
            )
        }

        // Top Core beam (1.2dp) - enters and exits smoothly within the hairline envelope
        val topCoreBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,                                       // Starts faded from left
                Color.White.copy(alpha = if (isDark) 0.60f else 0.80f), // Swelling into core
                Color.White.copy(alpha = if (isDark) 0.85f else 0.98f), // Peak specular catch
                accentColor.copy(alpha = if (isDark) 0.40f else 0.55f),
                Color.White.copy(alpha = if (isDark) 0.55f else 0.75f),
                Color.Transparent                                       // Fading before top-right corner
            ),
            startX = cornerPx * 0.3f,
            endX = size.width - (cornerPx * 0.3f)
        )
        drawPath(
            path = topPath,
            brush = topCoreBrush,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // Top Hairline (0.6dp) - spans full path from 0 to 0, ensuring both ends taper to an ultra-fine point
        val topHairlineBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,                                       // 100% transparent start at left
                Color.White.copy(alpha = if (isDark) 0.35f else 0.50f), // Tapered entry
                Color.White.copy(alpha = if (isDark) 0.70f else 0.90f),
                Color.White.copy(alpha = if (isDark) 0.40f else 0.60f),
                Color.White.copy(alpha = if (isDark) 0.10f else 0.20f), // Tapered exit
                Color.Transparent                                       // 100% transparent exit at right
            ),
            startX = 0f,
            endX = size.width
        )
        drawPath(
            path = topPath,
            brush = topHairlineBrush,
            style = Stroke(width = thinStrokePx, cap = StrokeCap.Round)
        )

        // 3. Bottom Edge Light Path: sweeps right arc, bottom horizontal line, and wraps into bottom-left corner
        val bottomPath = Path().apply {
            arcTo(
                rect = Rect(size.width - cornerPx * 2f, 0f, size.width, size.height),
                startAngleDegrees = 340f,
                sweepAngleDegrees = 110f,
                forceMoveTo = true
            )
            lineTo(cornerPx, size.height)
            arcTo(
                rect = Rect(0f, 0f, cornerPx * 2f, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 60f,
                forceMoveTo = false
            )
        }

        // Bottom Core beam (1.2dp) - enters and exits smoothly within the hairline envelope
        val bottomCoreBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,                                       // Fading before bottom-left corner
                Color.White.copy(alpha = if (isDark) 0.55f else 0.75f),
                accentColor.copy(alpha = if (isDark) 0.40f else 0.55f),
                Color.White.copy(alpha = if (isDark) 0.85f else 0.98f), // Peak specular catch
                Color.White.copy(alpha = if (isDark) 0.60f else 0.80f), // Swelling from right
                Color.Transparent                                       // Starts faded from right
            ),
            startX = cornerPx * 0.3f,
            endX = size.width - (cornerPx * 0.3f)
        )
        drawPath(
            path = bottomPath,
            brush = bottomCoreBrush,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // Bottom Hairline (0.6dp) - spans full path from 0 to 0, ensuring both ends taper to an ultra-fine point
        val bottomHairlineBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,                                       // 100% transparent exit at left
                Color.White.copy(alpha = if (isDark) 0.10f else 0.20f), // Tapered exit
                Color.White.copy(alpha = if (isDark) 0.40f else 0.60f),
                Color.White.copy(alpha = if (isDark) 0.70f else 0.90f),
                Color.White.copy(alpha = if (isDark) 0.35f else 0.50f), // Tapered entry
                Color.Transparent                                       // 100% transparent start at right
            ),
            startX = 0f,
            endX = size.width
        )
        drawPath(
            path = bottomPath,
            brush = bottomHairlineBrush,
            style = Stroke(width = thinStrokePx, cap = StrokeCap.Round)
        )
    }

    if (hazeState != null) {
        val hazeStyle = HazeStyle(
            backgroundColor = if (isDark) Color(0xFF141620) else Color(0xFFF6F8FC),
            tint = HazeTint(
                color = if (isDark) {
                    Color(0xFF141620).copy(alpha = 0.70f)
                } else {
                    Color.White.copy(alpha = 0.76f)
                }
            ),
            blurRadius = blurRadius,
            noiseFactor = 0.03f
        )

        // Genuine Real-Time Backdrop Frosted Blur with Bilateral Tapering Edge Light
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(shape)
                .hazeChild(
                    state = hazeState,
                    shape = shape,
                    style = hazeStyle
                )
                .drawBehind(drawEdgeLight)
        )
    } else {
        // High-fidelity fallback
        val squareFrostGradient = if (isDark) {
            listOf(
                Color(0xFF1E212D).copy(alpha = 0.76f),
                Color(0xFF141620).copy(alpha = 0.82f),
                accentColor.copy(alpha = 0.06f),
                Color(0xFF0F1018).copy(alpha = 0.86f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.88f),
                Color(0xFFF3F5FA).copy(alpha = 0.82f),
                accentColor.copy(alpha = 0.05f),
                Color(0xFFE8ECF4).copy(alpha = 0.86f)
            )
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(shape)
                .blur(radius = blurRadius)
                .background(Brush.verticalGradient(squareFrostGradient))
                .drawBehind(drawEdgeLight)
        )
    }
}
