package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsScreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeOsTopBar(
    currentScreen: LifeOsScreen,
    isListening: Boolean,
    onMenuClick: () -> Unit = {},
    onVoiceClick: () -> Unit,
    onQuickActionClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val dateString = remember {
        val sdf = SimpleDateFormat("EEE, MMM d", Locale.US)
        sdf.format(Date())
    }

    Surface(
        color = LifeOsBgPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand & System Identity with Menu Trigger
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu Button to open Drawer
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LifeOsElevated)
                        .border(1.dp, LifeOsBorder, RoundedCornerShape(8.dp))
                        .testTag("topbar_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Navigation Menu",
                        tint = LifeOsAccentGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(LifeOsElevated)
                        .border(1.dp, LifeOsAccentGold, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "LIFEOS",
                        tint = LifeOsAccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LIFEOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsAccentGold
                        )
                    }
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsTextSecondary
                    )
                }
            }

            // Status & Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExecutiveStatusPill()

                // Voice Activation Button
                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isListening) LifeOsAlert else LifeOsElevated)
                        .border(1.dp, if (isListening) LifeOsAlert else LifeOsBorder, CircleShape)
                        .testTag("topbar_voice_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = if (isListening) Color.White else LifeOsAccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Quick Create / Actions
                IconButton(
                    onClick = onQuickActionClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LifeOsElevated)
                        .border(1.dp, LifeOsBorder, CircleShape)
                        .testTag("topbar_quick_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Action",
                        tint = LifeOsTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LifeOsBottomBar(
    currentScreen: LifeOsScreen,
    onNavigate: (LifeOsScreen) -> Unit,
    onOpenHub: () -> Unit
) {
    Surface(
        color = LifeOsBgSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard",
                isSelected = currentScreen == LifeOsScreen.DASHBOARD,
                testTag = "nav_dashboard",
                onClick = { onNavigate(LifeOsScreen.DASHBOARD) }
            )
            NavItem(
                icon = Icons.Default.AutoAwesome,
                label = "Assistant",
                isSelected = currentScreen == LifeOsScreen.ASSISTANT,
                testTag = "nav_assistant",
                onClick = { onNavigate(LifeOsScreen.ASSISTANT) }
            )
            NavItem(
                icon = Icons.Default.CalendarToday,
                label = "My Day",
                isSelected = currentScreen == LifeOsScreen.MY_DAY,
                testTag = "nav_my_day",
                onClick = { onNavigate(LifeOsScreen.MY_DAY) }
            )
            NavItem(
                icon = Icons.Default.Checklist,
                label = "Tasks",
                isSelected = currentScreen == LifeOsScreen.TASKS,
                testTag = "nav_tasks",
                onClick = { onNavigate(LifeOsScreen.TASKS) }
            )
            NavItem(
                icon = Icons.Default.Apps,
                label = "Life Hub",
                isSelected = currentScreen in listOf(
                    LifeOsScreen.REMINDERS,
                    LifeOsScreen.MEMORY,
                    LifeOsScreen.GOALS_HABITS,
                    LifeOsScreen.FINANCE,
                    LifeOsScreen.KNOWLEDGE,
                    LifeOsScreen.AUTOMATIONS,
                    LifeOsScreen.SETTINGS
                ),
                testTag = "nav_hub",
                onClick = onOpenHub
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) LifeOsElevated else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary
        )
    }
}
