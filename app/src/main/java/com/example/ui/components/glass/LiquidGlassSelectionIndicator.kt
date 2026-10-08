package com.example.ui.components.glass

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Pure Liquid Glass Selection Indicator with Gesture Motion Support.
 *
 * Implements:
 * - Perfectly circular, harmonious pill shape (RoundedCornerShape(percent = 50)).
 * - Pure translucent glass gradient without artificial top glares or flares.
 * - Reactive liquid elastic pulse animation on gesture / long-press.
 * - Zero re-layout overhead (uses graphicsLayer translationX & scaleX).
 */
@Composable
fun LiquidGlassSelectionIndicator(
    targetCenterX: Float,
    itemWidth: Dp,
    height: Dp,
    accentColor: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    pulseTrigger: Int = 0
) {
    val positionAnim = remember { Animatable(targetCenterX) }
    val stretchAnim = remember { Animatable(1.0f) }
    val alphaAnim = remember { Animatable(if (isVisible) 1f else 0f) }
    val pulseScale = remember { Animatable(1.0f) }

    LaunchedEffect(targetCenterX, isVisible) {
        if (!isVisible) {
            alphaAnim.animateTo(0f, animationSpec = tween(150))
            return@LaunchedEffect
        }
        if (alphaAnim.value < 1f) {
            launch { alphaAnim.animateTo(1f, animationSpec = tween(150)) }
        }

        val distance = abs(targetCenterX - positionAnim.value)
        if (distance > 2f) {
            val targetStretch = 1.0f + (distance / 200f).coerceIn(0.08f, 0.22f)

            // GPU lateral stretch impulse
            launch {
                stretchAnim.animateTo(
                    targetValue = targetStretch,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                stretchAnim.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            // Damped physical spring navigation
            positionAnim.animateTo(
                targetValue = targetCenterX,
                animationSpec = spring(
                    dampingRatio = 0.82f,
                    stiffness = 400f
                )
            )
        } else {
            positionAnim.snapTo(targetCenterX)
        }
    }

    // Trigger elastic liquid pulse when gesture is recognized
    LaunchedEffect(pulseTrigger) {
        if (pulseTrigger > 0) {
            launch {
                pulseScale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = 600f
                    )
                )
                pulseScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = 350f
                    )
                )
            }
        }
    }

    // Circular harmonious pill geometry
    val baseWidth = (itemWidth - 10.dp).coerceIn(44.dp, 72.dp)
    val pillShape = RoundedCornerShape(percent = 50)

    // Pure translucent liquid glass gradient (clean, calm, no flare, no dark bottom shadow)
    val glassIndicatorGradient = if (isDarkTheme) {
        listOf(
            Color.White.copy(alpha = 0.16f),
            Color.White.copy(alpha = 0.09f),
            Color.White.copy(alpha = 0.06f)
        )
    } else {
        listOf(
            Color.White.copy(alpha = 0.80f),
            Color.White.copy(alpha = 0.58f),
            Color.White.copy(alpha = 0.40f)
        )
    }

    // Subtle, clean specular glass perimeter border
    val glassBorderBrush = Brush.verticalGradient(
        colors = if (isDarkTheme) {
            listOf(
                Color.White.copy(alpha = 0.40f),
                Color.White.copy(alpha = 0.14f),
                Color.White.copy(alpha = 0.06f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.40f),
                Color.White.copy(alpha = 0.18f)
            )
        }
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = positionAnim.value - (baseWidth.toPx() / 2f)
                scaleX = stretchAnim.value * pulseScale.value
                scaleY = pulseScale.value
                alpha = alphaAnim.value
            }
            .width(baseWidth)
            .height(height)
            .clip(pillShape)
            .background(Brush.verticalGradient(glassIndicatorGradient))
            .border(
                width = 1.dp,
                brush = glassBorderBrush,
                shape = pillShape
            )
    )
}
