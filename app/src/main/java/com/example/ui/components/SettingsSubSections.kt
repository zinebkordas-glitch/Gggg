package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.SettingsEntity
import com.example.ui.SettingsSection
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalVaultPalette
import com.example.ui.theme.VaultPalette

@Composable
fun SettingsMainMenu(
    modifier: Modifier = Modifier,
    currentSettings: SettingsEntity,
    onNavigateTo: (SettingsSection) -> Unit
) {
    val palette = LocalVaultPalette.current
    val accent = LocalAccentColor.current

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        GroupedCard {
            // 1. Display
            SettingsRow(
                title = "Display",
                subtitle = "Theme, accent & display options",
                icon = painterResource(id = R.drawable.ic_settings_display),
                trailing = { SettingsNavigationChevron() },
                onClick = { onNavigateTo(SettingsSection.DISPLAY) }
            )
            SettingsDivider()
            // 2. Privacy
            SettingsRow(
                title = "Privacy",
                subtitle = "Content privacy blur",
                icon = painterResource(id = R.drawable.ic_settings_privacy),
                trailing = { SettingsNavigationChevron() },
                onClick = { onNavigateTo(SettingsSection.PRIVACY) }
            )
            SettingsDivider()
            // 3. Integrations
            SettingsRow(
                title = "Integrations",
                subtitle = "Real-Debrid, Torbox & StashDB",
                icon = painterResource(id = R.drawable.ic_settings_integrations),
                trailing = { SettingsNavigationChevron() },
                onClick = { onNavigateTo(SettingsSection.INTEGRATIONS) }
            )
            SettingsDivider()
            // 4. Filter
            SettingsRow(
                title = "Filter",
                subtitle = if (currentSettings.blockedStudioNames.isNotEmpty()) {
                    "${currentSettings.blockedStudioNames.size} studio(s) blocked"
                } else {
                    "Blocked studios & content filters"
                },
                icon = painterResource(id = R.drawable.ic_settings_filter),
                trailing = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (currentSettings.blockedStudioNames.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                color = accent.copy(alpha = 0.15f),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${currentSettings.blockedStudioNames.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accent
                                    )
                                }
                            }
                        }
                        SettingsNavigationChevron()
                    }
                },
                onClick = { onNavigateTo(SettingsSection.FILTER) }
            )
            SettingsDivider()
            // 5. Data & Backup
            SettingsRow(
                title = "Data & Backup",
                subtitle = "Export & restore your media",
                icon = painterResource(id = R.drawable.ic_settings_backup),
                trailing = { SettingsNavigationChevron() },
                onClick = { onNavigateTo(SettingsSection.DATA_BACKUP) }
            )
            SettingsDivider()
            // 6. Sample Dataset
            SettingsRow(
                title = "Sample Dataset",
                subtitle = "Demo data for testing",
                icon = painterResource(id = R.drawable.ic_settings_sample_data),
                trailing = { SettingsNavigationChevron() },
                onClick = { onNavigateTo(SettingsSection.SAMPLE_DATA) }
            )
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
fun SettingsDisplaySection(
    modifier: Modifier = Modifier,
    themeName: String,
    onThemeChange: (String) -> Unit,
    accentHex: String,
    onAccentChange: (String) -> Unit,
    showManagementCards: Boolean,
    onShowManagementCardsChange: (Boolean) -> Unit,
    enableVideoPlayerGestures: Boolean,
    onEnableVideoPlayerGesturesChange: (Boolean) -> Unit,
    navBarTransparency: Float = 0.65f,
    onNavBarTransparencyChange: (Float) -> Unit = {},
    navBarBlurDp: Int = 24,
    onNavBarBlurDpChange: (Int) -> Unit = {},
    navBarActiveTabBlurDp: Int = 16,
    onNavBarActiveTabBlurDpChange: (Int) -> Unit = {}
) {
    val palette = LocalVaultPalette.current
    val accent = LocalAccentColor.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Theme selection in Grouped Card
        GroupedCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Theme",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = palette.textPrimary
                )

                NativeThemeSelector(
                    selectedTheme = themeName,
                    onSelectTheme = onThemeChange
                )
            }
        }

        // Color Palette in Grouped Card
        GroupedCard {
            Box(modifier = Modifier.padding(20.dp)) {
                ColorPalettePicker(
                    selectedId = accentHex,
                    onSelectPalette = onAccentChange
                )
            }
        }

        // Navigation Bar Controls: Transparency (%), Background Blur (dp), and Active Tab Blur (dp)
        GroupedCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        "Navigation Bar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = palette.textPrimary
                    )
                    Text(
                        "Floating glass transparency & blur controls",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                }

                HorizontalDivider(color = palette.border.copy(alpha = 0.4f))

                // 1. Navigation Bar Transparency percentage
                NavBarSliderCard(
                    title = "Transparency",
                    rangeHint = "Min 0% • Max 100%",
                    valueDisplay = "${(navBarTransparency * 100).toInt()}%",
                    icon = Icons.Outlined.Opacity,
                    value = navBarTransparency,
                    valueRange = 0f..1f,
                    onValueChange = { onNavBarTransparencyChange(it.coerceIn(0f, 1f)) },
                    testTag = "navbar_transparency_slider",
                    accent = accent,
                    palette = palette
                )

                // 2. Navigation Bar Blur Radius in dp
                NavBarSliderCard(
                    title = "Background Blur",
                    rangeHint = "Min 0 dp • Max 40 dp",
                    valueDisplay = "$navBarBlurDp dp",
                    icon = Icons.Outlined.BlurOn,
                    value = navBarBlurDp.toFloat(),
                    valueRange = 0f..40f,
                    onValueChange = { onNavBarBlurDpChange(it.toInt().coerceIn(0, 40)) },
                    testTag = "navbar_blur_slider",
                    accent = accent,
                    palette = palette
                )

                // 3. Active Tab Blur in dp
                NavBarSliderCard(
                    title = "Active Tab Blur",
                    rangeHint = "Min 0 dp • Max 40 dp",
                    valueDisplay = "$navBarActiveTabBlurDp dp",
                    icon = Icons.Outlined.AutoAwesome,
                    value = navBarActiveTabBlurDp.toFloat(),
                    valueRange = 0f..40f,
                    onValueChange = { onNavBarActiveTabBlurDpChange(it.toInt().coerceIn(0, 40)) },
                    testTag = "navbar_active_tab_blur_slider",
                    accent = accent,
                    palette = palette
                )
            }
        }

        // Cards layout toggle in Grouped Card
        GroupedCard {
            SettingsRow(
                title = "Cards Layout",
                subtitle = "Display cards for Actors and Studios management",
                trailing = {
                    Switch(
                        checked = showManagementCards,
                        onCheckedChange = onShowManagementCardsChange,
                        colors = museSwitchColors(),
                        modifier = Modifier.testTag("cards_management_switch")
                    )
                },
                onClick = { onShowManagementCardsChange(!showManagementCards) }
            )
        }

        // Player Gestures toggle in Grouped Card
        GroupedCard {
            SettingsRow(
                title = "Player Gestures",
                subtitle = "Control volume and brightness by vertical swipes in the video overlay player. When turned off, vertical scrolling over the video passes through smoothly.",
                trailing = {
                    Switch(
                        checked = enableVideoPlayerGestures,
                        onCheckedChange = onEnableVideoPlayerGesturesChange,
                        colors = museSwitchColors(),
                        modifier = Modifier.testTag("player_gestures_switch")
                    )
                },
                onClick = { onEnableVideoPlayerGesturesChange(!enableVideoPlayerGestures) }
            )
        }
    }
}

@Composable
fun SettingsPrivacySection(
    modifier: Modifier = Modifier,
    betaTestPrivacy: Boolean,
    onBetaTestPrivacyChange: (Boolean) -> Unit,
    isPasscodeEnabled: Boolean,
    passcodeHash: String,
    onEnablePasscode: (String) -> Unit,
    onDisablePasscode: () -> Unit,
    onChangePasscode: (String) -> Unit
) {
    val palette = LocalVaultPalette.current
    val accentColor = LocalAccentColor.current
    var activeDialogMode by remember { mutableStateOf<PasscodeDialogMode?>(null) }

    if (activeDialogMode != null) {
        PasscodeSetupDialog(
            mode = activeDialogMode!!,
            storedHash = passcodeHash,
            onSuccess = { newPin ->
                when (activeDialogMode) {
                    PasscodeDialogMode.SETUP_NEW -> {
                        if (newPin != null) onEnablePasscode(newPin)
                    }
                    PasscodeDialogMode.VERIFY_TO_DISABLE -> {
                        onDisablePasscode()
                    }
                    PasscodeDialogMode.CHANGE_OLD, PasscodeDialogMode.CHANGE_NEW -> {
                        if (newPin != null) onChangePasscode(newPin)
                    }
                    null -> {}
                }
                activeDialogMode = null
            },
            onDismiss = { activeDialogMode = null }
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Security & Lock Section
        GroupedCard {
            Column {
                val currentMode = activeDialogMode
                SettingsRow(
                    title = "App Passcode Lock",
                    subtitle = if (isPasscodeEnabled) {
                        "Application is protected by a 4-digit numeric code required upon launch"
                    } else {
                        "Protect access to the app with a 4-digit numeric passcode"
                    },
                    imageVector = if (isPasscodeEnabled) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                    trailing = {
                        Switch(
                            checked = isPasscodeEnabled,
                            onCheckedChange = { enable ->
                                if (enable) {
                                    activeDialogMode = PasscodeDialogMode.SETUP_NEW
                                } else {
                                    activeDialogMode = PasscodeDialogMode.VERIFY_TO_DISABLE
                                }
                            },
                            colors = museSwitchColors(),
                            modifier = Modifier.testTag("app_passcode_switch")
                        )
                    },
                    onClick = {
                        if (isPasscodeEnabled) {
                            activeDialogMode = PasscodeDialogMode.VERIFY_TO_DISABLE
                        } else {
                            activeDialogMode = PasscodeDialogMode.SETUP_NEW
                        }
                    }
                )

                if (isPasscodeEnabled) {
                    HorizontalDivider(
                        color = palette.border.copy(alpha = 0.4f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    SettingsRow(
                        title = "Change Passcode",
                        subtitle = "Update your existing 4-digit security PIN",
                        imageVector = Icons.Outlined.Key,
                        trailing = { SettingsNavigationChevron() },
                        onClick = {
                            activeDialogMode = PasscodeDialogMode.CHANGE_OLD
                        }
                    )
                }
            }
        }

        // Media privacy blur section
        GroupedCard {
            SettingsRow(
                title = "Beta Test Privacy",
                subtitle = "Loads all media seamlessly in the app while applying a smart privacy blur to obscure image content across all screens.",
                trailing = {
                    Switch(
                        checked = betaTestPrivacy,
                        onCheckedChange = onBetaTestPrivacyChange,
                        colors = museSwitchColors(),
                        modifier = Modifier.testTag("beta_test_privacy_switch")
                    )
                },
                onClick = { onBetaTestPrivacyChange(!betaTestPrivacy) }
            )
        }
    }
}

@Composable
fun SettingsSampleDataSection(
    modifier: Modifier = Modifier,
    sampleDataStatus: String,
    accentColor: Color,
    onLoadSample: () -> Unit,
    onClearSample: () -> Unit
) {
    val palette = LocalVaultPalette.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GroupedCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Sample dataset management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                Text(
                    "Load realistic sample data (studios, actors, scenes with magnets) or clean them completely from the database",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onLoadSample,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_app_add), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load Sample")
                    }

                    OutlinedButton(
                        onClick = onClearSample,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }

                if (sampleDataStatus.isNotEmpty()) {
                    Text(
                        sampleDataStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarSliderCard(
    title: String,
    rangeHint: String,
    valueDisplay: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTag: String,
    accent: Color,
    palette: VaultPalette
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.cardBg.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, palette.border.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Icon + Titles & Value Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = accent.copy(alpha = 0.14f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            ),
                            color = palette.textPrimary
                        )
                        Text(
                            text = rangeHint,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = palette.textMuted
                        )
                    }
                }

                // Value Pill Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = valueDisplay,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Slider Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                SmoothFluidSlider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    activeColor = accent,
                    inactiveTrackColor = palette.border.copy(alpha = 0.4f),
                    trackHeight = 10.dp,
                    thumbDiameter = 22.dp,
                    expandedThumbDiameter = 26.dp,
                    testTag = testTag,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

