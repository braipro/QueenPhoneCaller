package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PermPhoneMsg
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.repository.AppSettings
import com.example.service.CallService
import com.example.ui.theme.CallGreen
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurplePrimary

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSimplifiedModeChange: (Boolean) -> Unit,
    onSpeakNamesChange: (Boolean) -> Unit,
    onGridColumnsChange: (Int) -> Unit,
    onDarkThemeModeChange: (Int) -> Unit,
    onResetDefaults: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(18.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.caregiver_settings),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card 1: Accessibility & No-Reading Mode
        SettingsCard(title = "Accessibility Options") {
            // Simplified Mode Toggle
            SettingSwitchRow(
                icon = Icons.Default.TouchApp,
                title = "No-Reading Mode",
                subtitle = "Enlarges photos and buttons, hides search bar and secondary text",
                checked = settings.simplifiedMode,
                onCheckedChange = onSimplifiedModeChange,
                testTag = "toggle_simplified_mode"
            )

            // Spoken Contact Names (TTS)
            SettingSwitchRow(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "Speak Name Aloud",
                subtitle = "Reads contact's name aloud when their photo is touched",
                checked = settings.speakNamesAloud,
                onCheckedChange = onSpeakNamesChange,
                testTag = "toggle_speak_names"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: Layout & Appearance
        SettingsCard(title = "Layout & Theme") {
            // Grid Columns: 3 columns vs 2 columns (Extra Large)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = null,
                        tint = RoyalPurplePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Contact Grid Size",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (settings.gridColumns == 2) "2 Large tiles per row" else "3 Tiles per row (standard)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onGridColumnsChange(3) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.gridColumns == 3) RoyalPurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (settings.gridColumns == 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("3")
                    }
                    Button(
                        onClick = { onGridColumnsChange(2) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.gridColumns == 2) RoyalPurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (settings.gridColumns == 2) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("2 (Big)")
                    }
                }
            }

            // Theme toggle: Warm Cream vs Dark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Brightness4,
                        contentDescription = null,
                        tint = RoyalPurplePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "App Theme",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (settings.darkThemeMode == 2) "Dark Royal Mode" else "Light Warm Cream",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = settings.darkThemeMode == 2,
                    onCheckedChange = { isDark ->
                        onDarkThemeModeChange(if (isDark) 2 else 1)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 3: Diagnostic & Calling test
        SettingsCard(title = "Diagnostics & Calling Test") {
            val hasContactPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val hasCallPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

            DiagnosticRow(
                title = "Contacts Access",
                status = if (hasContactPerm) "Granted" else "Not Granted",
                isOk = hasContactPerm
            )

            DiagnosticRow(
                title = "Direct Call Permission",
                status = if (hasCallPerm) "Granted (Direct Call)" else "Fallback (Safe Dialer)",
                isOk = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    CallService.initiateCall(context, "123")
                    Toast.makeText(context, "Tested dialer integration", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CallGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.PermPhoneMsg, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test System Dialer Launcher")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reset Defaults Button
        OutlinedButton(
            onClick = {
                onResetDefaults()
                Toast.makeText(context, "Settings restored to defaults", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("reset_defaults_button")
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.reset_defaults))
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RoyalPurplePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = RoyalGold,
                checkedTrackColor = RoyalPurplePrimary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun DiagnosticRow(
    title: String,
    status: String,
    isOk: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isOk) CallGreen else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOk) CallGreen else MaterialTheme.colorScheme.error
            )
        }
    }
}
