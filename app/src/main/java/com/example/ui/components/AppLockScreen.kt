package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalVaultPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fullscreen aesthetic app lock overlay with numeric keypad, animated dots,
 * vibration feedback, and shake animation on error.
 */
@Composable
fun AppLockScreen(
    onUnlock: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalVaultPalette.current
    val accentColor = LocalAccentColor.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Shake offset animation for invalid PIN attempts
    val shakeOffset = remember { Animatable(0f) }

    fun triggerShake() {
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            isError = true
            errorMessage = "Incorrect Passcode"
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -24f at 50
                    24f at 100
                    -18f at 150
                    18f at 200
                    -10f at 250
                    10f at 300
                    0f at 400
                }
            )
            delay(1500L)
            enteredPin = ""
            isError = false
            errorMessage = null
        }
    }

    fun handleDigitPress(digit: String) {
        if (enteredPin.length < 4 && !isError) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val updated = enteredPin + digit
            enteredPin = updated
            if (updated.length == 4) {
                val success = onUnlock(updated)
                if (!success) {
                    triggerShake()
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty() && !isError) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("app_lock_screen"),
        color = palette.bg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = shakeOffset.value.dp)
            ) {
                // Lock Icon with glowing background circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.25f),
                                    accentColor.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = palette.cardBg.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                        modifier = Modifier.size(62.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.Lock,
                                contentDescription = "App Locked",
                                tint = if (isError) MaterialTheme.colorScheme.error else accentColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Application Locked",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage ?: "Enter your 4-digit security code to unlock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isError) MaterialTheme.colorScheme.error else palette.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Passcode Indicator Dots (4 dots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        val dotColor by animateColorAsState(
                            targetValue = when {
                                isError -> MaterialTheme.colorScheme.error
                                isFilled -> accentColor
                                else -> palette.cardBg.copy(alpha = 0.6f)
                            },
                            animationSpec = tween(180),
                            label = "DotColor"
                        )
                        val dotBorderColor by animateColorAsState(
                            targetValue = when {
                                isError -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                isFilled -> accentColor
                                else -> palette.border.copy(alpha = 0.4f)
                            },
                            animationSpec = tween(180),
                            label = "DotBorder"
                        )
                        val dotScale by animateFloatAsState(
                            targetValue = if (isFilled) 1.25f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "DotScale"
                        )

                        Box(
                            modifier = Modifier
                                .size((18 * dotScale).dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(
                                    BorderStroke(1.5.dp, dotBorderColor),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Numeric Keypad Grid (1-9, empty, 0, Backspace)
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in keypadRows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (item in row) {
                                when (item) {
                                    "" -> {
                                        Spacer(modifier = Modifier.size(72.dp))
                                    }
                                    "DEL" -> {
                                        Surface(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = ripple(bounded = true)
                                                ) {
                                                    handleBackspace()
                                                }
                                                .testTag("keypad_del"),
                                            shape = CircleShape,
                                            color = palette.cardBg.copy(alpha = 0.35f),
                                            border = BorderStroke(1.dp, palette.border.copy(alpha = 0.25f))
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Backspace,
                                                    contentDescription = "Backspace",
                                                    tint = palette.textPrimary,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        Surface(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = ripple(bounded = true)
                                                ) {
                                                    handleDigitPress(item)
                                                }
                                                .testTag("keypad_$item"),
                                            shape = CircleShape,
                                            color = palette.cardBg.copy(alpha = 0.65f),
                                            border = BorderStroke(1.dp, palette.border.copy(alpha = 0.3f))
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = item,
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontFamily = FontFamily.Default,
                                                    color = palette.textPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
