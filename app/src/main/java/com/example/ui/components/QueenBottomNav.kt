package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.QueenNavTab
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurpleDark
import com.example.ui.theme.RoyalPurplePrimary

@Composable
fun QueenBottomNav(
    currentTab: QueenNavTab,
    onTabSelected: (QueenNavTab) -> Unit,
    simplifiedMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // HOME / FAMILY
        NavigationBarItem(
            selected = currentTab == QueenNavTab.HOME,
            onClick = { onTabSelected(QueenNavTab.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = stringResource(R.string.nav_home),
                    modifier = Modifier.size(30.dp)
                )
            },
            label = if (!simplifiedMode) {
                {
                    Text(
                        text = stringResource(R.string.nav_home),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else null,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = RoyalPurplePrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        // FAVORITES
        NavigationBarItem(
            selected = currentTab == QueenNavTab.FAVORITES,
            onClick = { onTabSelected(QueenNavTab.FAVORITES) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.nav_favorites),
                    modifier = Modifier.size(30.dp)
                )
            },
            label = if (!simplifiedMode) {
                {
                    Text(
                        text = stringResource(R.string.nav_favorites),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else null,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalPurpleDark,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = RoyalGold,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_favorites")
        )

        // RECENTS
        NavigationBarItem(
            selected = currentTab == QueenNavTab.RECENTS,
            onClick = { onTabSelected(QueenNavTab.RECENTS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.nav_recents),
                    modifier = Modifier.size(30.dp)
                )
            },
            label = if (!simplifiedMode) {
                {
                    Text(
                        text = stringResource(R.string.nav_recents),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else null,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = RoyalPurplePrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_recents")
        )

        // KEYPAD
        NavigationBarItem(
            selected = currentTab == QueenNavTab.KEYPAD,
            onClick = { onTabSelected(QueenNavTab.KEYPAD) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dialpad,
                    contentDescription = stringResource(R.string.nav_keypad),
                    modifier = Modifier.size(30.dp)
                )
            },
            label = if (!simplifiedMode) {
                {
                    Text(
                        text = stringResource(R.string.nav_keypad),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else null,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = RoyalPurplePrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_keypad")
        )
    }
}
