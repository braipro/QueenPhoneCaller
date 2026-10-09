package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CallRecord
import com.example.data.model.CallType
import com.example.data.model.ContactItem
import com.example.ui.components.AvatarFallback
import com.example.ui.theme.CallGreen
import com.example.ui.theme.HangupRed
import com.example.ui.theme.RoyalPurplePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentsScreen(
    recentCalls: List<CallRecord>,
    contacts: List<ContactItem>,
    onContactClick: (ContactItem) -> Unit,
    onDirectCall: (Context, String) -> Unit,
    onClearHistory: () -> Unit,
    simplifiedMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App history informational banner
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Calls made via Queen Phone",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                if (recentCalls.isNotEmpty() && !simplifiedMode) {
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear call history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (recentCalls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = RoyalPurplePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.no_recents_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.no_recents_desc),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("recents_list"),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(recentCalls, key = { it.id }) { record ->
                    val matchedContact = contacts.find { it.id == record.contactId }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                if (matchedContact != null) {
                                    onContactClick(matchedContact)
                                } else {
                                    onDirectCall(context, record.phoneNumber)
                                }
                            }
                            .testTag("recent_item_${record.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Large Contact Photo Frame (64x64 dp)
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.5.dp,
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                val photo = matchedContact?.effectivePhotoUri ?: record.photoUri
                                if (!photo.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = photo,
                                        contentDescription = "Photo of ${record.contactName}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    AvatarFallback(name = record.contactName)
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Name, Number and Call Info
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = record.contactName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = if (simplifiedMode) 18.sp else 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Call Type Icon
                                    val (typeIcon, typeColor) = when (record.type) {
                                        CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade to CallGreen
                                        CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived to RoyalPurplePrimary
                                        CallType.MISSED -> Icons.Default.CallMissed to HangupRed
                                    }
                                    Icon(
                                        imageVector = typeIcon,
                                        contentDescription = null,
                                        tint = typeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = record.phoneNumber,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1
                                    )
                                }

                                if (!simplifiedMode) {
                                    Text(
                                        text = dateFormat.format(Date(record.timestamp)),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Prominent One-Tap Green Call Button
                            IconButton(
                                onClick = { onDirectCall(context, record.phoneNumber) },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(CallGreen)
                                    .testTag("recent_call_button_${record.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call ${record.contactName}",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
