package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.auth.FirebaseUser

data class DrawerNavCategory(
    val title: String,
    val items: List<DrawerNavItemData>
)

data class DrawerNavItemData(
    val screen: LifeOsScreen,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
fun LifeOsDrawerContent(
    currentScreen: LifeOsScreen,
    currentUser: FirebaseUser?,
    onSelectScreen: (LifeOsScreen) -> Unit,
    onQuickCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navCategories = listOf(
        DrawerNavCategory(
            title = "CORE OPERATIONS",
            items = listOf(
                DrawerNavItemData(LifeOsScreen.DASHBOARD, Icons.Default.Dashboard),
                DrawerNavItemData(LifeOsScreen.ASSISTANT, Icons.Default.AutoAwesome, "AI"),
                DrawerNavItemData(LifeOsScreen.MY_DAY, Icons.Default.CalendarToday),
                DrawerNavItemData(LifeOsScreen.TASKS, Icons.Default.Checklist)
            )
        ),
        DrawerNavCategory(
            title = "INTELLIGENCE & GROWTH",
            items = listOf(
                DrawerNavItemData(LifeOsScreen.REMINDERS, Icons.Default.Notifications),
                DrawerNavItemData(LifeOsScreen.MEMORY, Icons.Default.Memory, "Context"),
                DrawerNavItemData(LifeOsScreen.GOALS_HABITS, Icons.Default.EmojiEvents),
                DrawerNavItemData(LifeOsScreen.KNOWLEDGE, Icons.Default.MenuBook)
            )
        ),
        DrawerNavCategory(
            title = "OPERATIONS & SYSTEM",
            items = listOf(
                DrawerNavItemData(LifeOsScreen.FINANCE, Icons.Default.AccountBalanceWallet),
                DrawerNavItemData(LifeOsScreen.AUTOMATIONS, Icons.Default.Bolt),
                DrawerNavItemData(LifeOsScreen.SETTINGS, Icons.Default.Settings)
            )
        )
    )

    ModalDrawerSheet(
        modifier = modifier
            .widthIn(max = 320.dp)
            .fillMaxHeight(),
        drawerContainerColor = LifeOsBgSecondary,
        drawerContentColor = LifeOsTextPrimary,
        drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
        windowInsets = WindowInsets.statusBars
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Section: LIFEOS AI Brand & Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LifeOsElevated)
                        .border(1.dp, LifeOsAccentGold, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "LIFEOS",
                        tint = LifeOsAccentGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LIFEOS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsAccentGold
                        )
                    }
                    Text(
                        text = "Your Life. Organized by Intelligence.",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ExecutiveStatusPill()

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Create Action Button
            Button(
                onClick = onQuickCreateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LifeOsElevated,
                    contentColor = LifeOsAccentGold
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorderGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_quick_create_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Executive Action",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = LifeOsBorder, thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Navigation Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                navCategories.forEach { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = category.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsTextSecondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 2.dp)
                        )

                        category.items.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.screen.label,
                                        tint = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.screen.label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) LifeOsTextPrimary else LifeOsTextSecondary
                                    )
                                },
                                badge = item.badge?.let {
                                    {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) LifeOsAccentGold else LifeOsElevated
                                        ) {
                                            Text(
                                                text = it,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) LifeOsBgPrimary else LifeOsAccentGoldLight,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                },
                                selected = isSelected,
                                onClick = { onSelectScreen(item.screen) },
                                shape = RoundedCornerShape(10.dp),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = LifeOsCardBg,
                                    unselectedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp)
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) LifeOsBorderGold else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .testTag("drawer_nav_${item.screen.name}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = LifeOsBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Footer Section: User Profile & Cloud Sync
            Surface(
                color = LifeOsCardBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectScreen(LifeOsScreen.SETTINGS) }
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("drawer_profile_footer")
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LifeOsElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsAccentGold),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (currentUser != null) "G" else "CMD",
                                fontWeight = FontWeight.Bold,
                                color = LifeOsAccentGold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.displayName ?: "Commander",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = LifeOsTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = if (currentUser != null) "Cloud Firestore Sync" else "Local OS Core",
                            fontSize = 11.sp,
                            color = if (currentUser != null) LifeOsSuccess else LifeOsTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Profile",
                        tint = LifeOsTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
