package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContactItem
import com.example.ui.theme.QueenRose
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurplePrimary

@Composable
fun ContactTile(
    contact: ContactItem,
    onClick: () -> Unit,
    simplifiedMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("contact_tile_${contact.id}")
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Photo Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = if (contact.isEmergency) 3.dp else if (contact.isFavorite) 2.5.dp else 1.5.dp,
                        color = when {
                            contact.isEmergency -> QueenRose
                            contact.isFavorite -> RoyalGold
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val photoUri = contact.effectivePhotoUri
                if (!photoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Photo of ${contact.displayName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Distinct, attractive avatar fallback for contacts without photos
                    AvatarFallback(name = contact.displayName)
                }

                // Favorite Star Badge
                if (contact.isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(26.dp)
                            .background(RoyalGold, CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite contact",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Emergency Badge
                if (contact.isEmergency) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .size(26.dp)
                            .background(QueenRose, CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Emergency contact",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Contact Name Label (Minimal and clear)
            Text(
                text = contact.displayName.ifBlank { "Family" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (simplifiedMode) 16.sp else 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
fun AvatarFallback(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: ""
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (initial.isNotBlank()) {
            Text(
                text = initial,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = RoyalPurplePrimary.copy(alpha = 0.7f)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = RoyalPurplePrimary.copy(alpha = 0.6f),
                modifier = Modifier.size(44.dp)
            )
        }
    }
}
