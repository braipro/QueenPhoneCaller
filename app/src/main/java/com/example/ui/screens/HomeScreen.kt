package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ContactItem
import com.example.ui.components.ContactTile
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurplePrimary

@Composable
fun HomeScreen(
    contacts: List<ContactItem>,
    isLoading: Boolean,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onContactClick: (ContactItem) -> Unit,
    gridColumns: Int = 3,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    simplifiedMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!hasPermission) {
        PermissionScreen(onRequestPermission = onRequestPermission, modifier = modifier)
        return
    }

    if (isLoading && contacts.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = RoyalPurplePrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    // Filter contacts based on search query
    val filteredContacts = if (searchQuery.isBlank()) {
        contacts
    } else {
        contacts.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumbers.any { p -> p.number.contains(searchQuery) }
        }
    }

    val favorites = filteredContacts.filter { it.isFavorite || it.isEmergency }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Optional Search bar (hidden in simplified mode)
        if (!simplifiedMode) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_search_field"),
                    placeholder = {
                        Text(
                            text = "Search by name or number…",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }

        if (filteredContacts.isEmpty()) {
            // Empty state
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
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.no_contacts_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.no_contacts_desc),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // 3-column (or 2-column) grid of contacts
            val columns = if (gridColumns in 2..3) gridColumns else 3
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("contacts_grid"),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Favorites banner if present and no active search
                if (favorites.isNotEmpty() && searchQuery.isBlank() && !simplifiedMode) {
                    item(span = { GridItemSpan(columns) }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp, top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Favorites & Family",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    items(favorites, key = { "fav_${it.id}" }) { contact ->
                        ContactTile(
                            contact = contact,
                            onClick = { onContactClick(contact) },
                            simplifiedMode = simplifiedMode
                        )
                    }

                    item(span = { GridItemSpan(columns) }) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All Contacts",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                }

                // Main Contacts list
                items(filteredContacts, key = { it.id }) { contact ->
                    ContactTile(
                        contact = contact,
                        onClick = { onContactClick(contact) },
                        simplifiedMode = simplifiedMode
                    )
                }
            }
        }
    }
}
