package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CallGreen

@Composable
fun KeypadScreen(
    number: String,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onCallClick: (Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Displayed Number Display Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = number.ifEmpty { "Enter Number" },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = if (number.length > 12) 24.sp else 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (number.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("keypad_display_text")
                )

                if (number.isNotEmpty()) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("keypad_backspace_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = stringResource(R.string.delete_digit),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3x4 Keypad Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row 1: 1, 2, 3
            KeypadRow(
                keys = listOf(
                    KeypadKey("1", ""),
                    KeypadKey("2", "ABC"),
                    KeypadKey("3", "DEF")
                ),
                onKeyClick = onDigitClick
            )

            // Row 2: 4, 5, 6
            KeypadRow(
                keys = listOf(
                    KeypadKey("4", "GHI"),
                    KeypadKey("5", "JKL"),
                    KeypadKey("6", "MNO")
                ),
                onKeyClick = onDigitClick
            )

            // Row 3: 7, 8, 9
            KeypadRow(
                keys = listOf(
                    KeypadKey("7", "PQRS"),
                    KeypadKey("8", "TUV"),
                    KeypadKey("9", "WXYZ")
                ),
                onKeyClick = onDigitClick
            )

            // Row 4: *, 0 (+), #
            KeypadRow(
                keys = listOf(
                    KeypadKey("*", ""),
                    KeypadKey("0", "+"),
                    KeypadKey("#", "")
                ),
                onKeyClick = onDigitClick,
                onLongClick = { if (it == "0") onDigitClick("+") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prominent Green CALL Button
        Button(
            onClick = { onCallClick(context) },
            enabled = number.isNotEmpty(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CallGreen,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(72.dp)
                .testTag("keypad_call_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.call_button).uppercase(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

data class KeypadKey(val digit: String, val subText: String)

@Composable
private fun KeypadRow(
    keys: List<KeypadKey>,
    onKeyClick: (String) -> Unit,
    onLongClick: ((String) -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        keys.forEach { key ->
            KeypadButton(
                key = key,
                onClick = { onKeyClick(key.digit) },
                onLongClick = { onLongClick?.invoke(key.digit) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeypadButton(
    key: KeypadKey,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("keypad_key_${key.digit}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = key.digit,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (key.subText.isNotEmpty()) {
                Text(
                    text = key.subText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
