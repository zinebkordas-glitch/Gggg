package com.example.ui.components.glass

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.ui.components.glass.backdrop.GlassSurface
import com.example.ui.screens.LocalHazeState
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalVaultPalette

/**
 * Floating Liquid Glass Navigation Bar with Gesture Interactions.
 *
 * Implements:
 * 1. Deep translucent floating glass capsule with bidirectional tapering edge light.
 * 2. Diffused ambient floating shadow.
 * 3. Liquid Glass Selection Indicator with physical spring glide & long-press gesture pulse.
 * 4. Micro-haptic tactile feedback on active tab gesture.
 * 5. Adaptive landscape optimization and strict boundary clipping.
 */
@Composable
fun LiquidGlassNavigationBar(
    items: List<LiquidGlassNavItem> = LiquidGlassNavItem.defaultNavItems,
    selectedId: String,
    onItemSelected: (LiquidGlassNavItem) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: dev.chrisbanes.haze.HazeState? = LocalHazeState.current,
    onItemLongPressed: ((LiquidGlassNavItem) -> Unit)? = null,
    barHeightDp: Int = 64,
    transparency: Float = 0.65f,
    blurRadiusDp: Int = 24
) {
    val palette = LocalVaultPalette.current
    val accent = LocalAccentColor.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val haptic = LocalHapticFeedback.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var pulseTrigger by remember { mutableIntStateOf(0) }

    // Precise luminance calculation using Compose's native luminance()
    val isLight = remember(palette.bg) {
        palette.bg.luminance() > 0.45f
    }
    val isDark = !isLight

    val selectedIndex by remember(selectedId, items) {
        derivedStateOf {
            items.indexOfFirst { it.id.equals(selectedId, ignoreCase = true) }
        }
    }
    val hasValidSelection = selectedIndex >= 0

    val safeHeight = barHeightDp.coerceIn(48, 80)
    val barHeight = if (isLandscape) (safeHeight * 0.82f).dp else safeHeight.dp
    val barCornerRadius = barHeight / 2
    val barShape = RoundedCornerShape(barCornerRadius)

    val unselectedItemColor = if (isDark) {
        Color.White.copy(alpha = 0.70f)
    } else {
        Color(0xFF3C404E).copy(alpha = 0.80f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(
                horizontal = if (isLandscape) 32.dp else 16.dp,
                vertical = if (isLandscape) 6.dp else 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .height(barHeight)
                .shadow(
                    elevation = if (isDark) 14.dp else 18.dp,
                    shape = barShape,
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.40f) else Color(0x241E2235),
                    spotColor = if (isDark) accent.copy(alpha = 0.22f) else Color(0x1E000000)
                )
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val itemCount = items.size.coerceAtLeast(1)
            val itemWidthPx = totalWidthPx / itemCount
            val itemWidthDp = with(density) { itemWidthPx.toDp() }

            val effectiveIndex = if (hasValidSelection) selectedIndex else 0
            val targetCenterX = (effectiveIndex + 0.5f) * itemWidthPx
            val indicatorHeight = barHeight - if (isLandscape) 8.dp else 12.dp

            // LAYER 1: Deep Translucent Glass Surface with Tapering Edge Light
            GlassSurface(
                shape = barShape,
                isDark = isDark,
                accentColor = accent,
                hazeState = hazeState,
                blurRadius = blurRadiusDp.coerceIn(0, 40).dp,
                transparency = transparency.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxSize()
            )

            // LAYER 2: Moving Glass Selection Indicator (with Liquid Gesture Pulse)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(barShape)
                    .padding(vertical = if (isLandscape) 4.dp else 6.dp)
            ) {
                LiquidGlassSelectionIndicator(
                    targetCenterX = targetCenterX,
                    itemWidth = itemWidthDp,
                    height = indicatorHeight,
                    accentColor = accent,
                    isDarkTheme = isDark,
                    isVisible = hasValidSelection,
                    pulseTrigger = pulseTrigger,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            // LAYER 3: 100% Crisp Interactive Navigation Items with Gesture Recognition
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isItemSelected = index == selectedIndex
                    LiquidGlassNavigationItem(
                        item = item,
                        isSelected = isItemSelected,
                        itemWidth = itemWidthDp,
                        accentColor = accent,
                        unselectedColor = unselectedItemColor,
                        onClick = { onItemSelected(item) },
                        onLongClick = {
                            if (isItemSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pulseTrigger++
                                onItemLongPressed?.invoke(item)
                            }
                        }
                    )
                }
            }
        }
    }
}
