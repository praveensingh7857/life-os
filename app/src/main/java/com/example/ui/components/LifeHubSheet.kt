package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeHubSheet(
    onDismiss: () -> Unit,
    onSelectScreen: (LifeOsScreen) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIFEOS Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsTextPrimary
                    )
                    Text(
                        text = "Executive modules & intelligence suites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = LifeOsTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HubItemCard(
                        title = "Smart Reminders",
                        subtitle = "Alerts & deadlines",
                        icon = Icons.Default.Notifications,
                        accentColor = LifeOsWarning,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectScreen(LifeOsScreen.REMINDERS); onDismiss() }
                    )
                    HubItemCard(
                        title = "AI Memory",
                        subtitle = "Verified context",
                        icon = Icons.Default.Memory,
                        accentColor = LifeOsPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectScreen(LifeOsScreen.MEMORY); onDismiss() }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HubItemCard(
                        title = "Goals & Habits",
                        subtitle = "Milestones & streaks",
                        icon = Icons.Default.EmojiEvents,
                        accentColor = Color(0xFFF97316),
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectScreen(LifeOsScreen.GOALS_HABITS); onDismiss() }
                    )
                    HubItemCard(
                        title = "Personal Finance",
                        subtitle = "Ledger & budget",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = LifeOsSuccess,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectScreen(LifeOsScreen.FINANCE); onDismiss() }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HubItemCard(
                        title = "Knowledge Vault",
                        subtitle = "Strategic notes",
                        icon = Icons.Default.MenuBook,
                        accentColor = LifeOsInfo,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectScreen(LifeOsScreen.KNOWLEDGE); onDismiss() }
                    )
                    HubItemCard(
                        title = "Automations",
                        subtitle = "Background routines",
                        icon = Icons.Default.Bolt,
                        accentColor = LifeOsAccentGold,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectScreen(LifeOsScreen.AUTOMATIONS); onDismiss() }
                    )
                }

                HubItemCard(
                    title = "System Settings",
                    subtitle = "Profile, voice synthesis, data governance",
                    icon = Icons.Default.Settings,
                    accentColor = LifeOsTextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSelectScreen(LifeOsScreen.SETTINGS); onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HubItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = LifeOsElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("hub_item_${title.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = LifeOsTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = LifeOsTextSecondary
                )
            }
        }
    }
}
