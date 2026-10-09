package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ContactItem
import com.example.ui.components.AvatarFallback
import com.example.ui.theme.CallGreen
import com.example.ui.theme.QueenRose
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurplePrimary

@Composable
fun ContactConfirmScreen(
    contact: ContactItem,
    onCallClick: (ContactItem, String) -> Unit,
    onBackClick: () -> Unit,
    onToggleFavorite: (ContactItem) -> Unit,
    onToggleEmergency: (ContactItem) -> Unit,
    onCustomPhotoSelected: (contactId: String, uri: Uri) -> Unit,
    onResetPhoto: (contactId: String) -> Unit,
    simplifiedMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    // Photo picker launcher (no storage permission required!)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onCustomPhotoSelected(contact.id, uri)
        }
    }

    // Selected phone number state (defaults to primary or first available)
    var selectedNumber by remember(contact.id, contact.phoneNumbers) {
        mutableStateOf(contact.primaryNumber ?: "")
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Row: Back button, Star Favorite, Caregiver Photo Change
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .testTag("confirm_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Quick actions: Favorite, Emergency, Photo Edit
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emergency Toggle
                IconButton(
                    onClick = { onToggleEmergency(contact) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (contact.isEmergency) QueenRose.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .testTag("confirm_emergency_button")
                ) {
                    Icon(
                        imageVector = if (contact.isEmergency) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle emergency contact",
                        tint = if (contact.isEmergency) QueenRose else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Favorite Toggle
                IconButton(
                    onClick = { onToggleFavorite(contact) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (contact.isFavorite) RoyalGold.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .testTag("confirm_favorite_button")
                ) {
                    Icon(
                        imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (contact.isFavorite) RoyalGold else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Change Photo Button (Caregiver feature)
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RoyalPurplePrimary.copy(alpha = 0.12f))
                        .testTag("confirm_photo_edit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.change_photo),
                        tint = RoyalPurplePrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Reset Photo (if custom photo is active)
                if (contact.customPhotoPath != null) {
                    IconButton(
                        onClick = { onResetPhoto(contact.id) },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag("confirm_photo_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset_photo),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // VERY LARGE PHOTOGRAPH
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .border(
                    width = if (contact.isEmergency) 5.dp else if (contact.isFavorite) 4.dp else 2.5.dp,
                    color = when {
                        contact.isEmergency -> QueenRose
                        contact.isFavorite -> RoyalGold
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(32.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val photoUri = contact.effectivePhotoUri
            if (!photoUri.isNullOrEmpty()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Large photo of ${contact.displayName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AvatarFallback(name = contact.displayName)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Large Person Name
        Text(
            text = contact.displayName.ifBlank { "Family Member" },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (simplifiedMode) 30.sp else 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Numbers Section
        if (contact.phoneNumbers.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneDisabled,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.no_phone_number),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        } else if (contact.phoneNumbers.size == 1) {
            // Single Phone Number
            val numberItem = contact.phoneNumbers.first()
            selectedNumber = numberItem.number
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${numberItem.label}: ${numberItem.number}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        } else {
            // Multiple Phone Numbers: Large choice cards
            Text(
                text = "Select Number:",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            contact.phoneNumbers.forEach { item ->
                val isSelected = selectedNumber == item.number
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedNumber = item.number }
                        .testTag("number_option_${item.number}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) {
                        CardDefaults.outlinedCardBorder().copy(
                            width = 2.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    } else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = item.number,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // PROMINENT GREEN CALL BUTTON
        val canCall = contact.phoneNumbers.isNotEmpty() && selectedNumber.isNotBlank()
        Button(
            onClick = {
                if (canCall) {
                    onCallClick(contact, selectedNumber)
                }
            },
            enabled = canCall,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CallGreen,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .testTag("confirm_call_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Telephone",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.call_button).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
