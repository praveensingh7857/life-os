package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsScreen
import com.example.viewmodel.LifeOsViewModel
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: LifeOsViewModel,
    onNavigate: (LifeOsScreen) -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val finances by viewModel.finances.collectAsState()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val completedTasksCount = tasks.count { it.status == "Completed" }
    val completionPercentage = if (tasks.isNotEmpty()) (completedTasksCount * 100) / tasks.size else 0
    val totalExpenses = finances.filter { it.type == "Expense" }.sumOf { it.amount }
    val totalIncome = finances.filter { it.type == "Income" }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Executive Header Section
        item {
            Column {
                Text(
                    text = "$greeting, Commander",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your personal command center • AI Executive Core active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary
                )
            }
        }

        // AI Daily Briefing Banner
        item {
            GlassCard(
                backgroundColor = LifeOsCardBg,
                borderColor = LifeOsBorderGold,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_briefing_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0x2AC6A15B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = LifeOsAccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EXECUTIVE BRIEFING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsAccentGold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Chief of Staff",
                        fontSize = 11.sp,
                        color = LifeOsTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val topUrgent = pendingTasks.firstOrNull { it.priority == "Urgent" } ?: pendingTasks.firstOrNull()
                val topReminder = reminders.firstOrNull { !it.isCompleted }

                Text(
                    text = "You have ${pendingTasks.size} tasks pending today. Highest priority focus is \"${topUrgent?.title ?: "Strategic Milestones"}\". Next calendar checkpoint: ${topReminder?.title ?: "Review Plan"} at ${topReminder?.time ?: "10:00 AM"}.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LifeOsTextPrimary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            viewModel.setSelectedDepartment("Chief of Staff")
                            onNavigate(LifeOsScreen.ASSISTANT)
                            viewModel.sendUserMessage("Plan my day")
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = LifeOsElevated,
                            contentColor = LifeOsAccentGold
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_plan_my_day")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Optimize My Schedule", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Metrics Grid (4 Key Executive Indicators)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task Velocity Metric
                MetricWidget(
                    title = "Task Velocity",
                    value = "$completionPercentage%",
                    subtitle = "${completedTasksCount}/${tasks.size} done",
                    icon = Icons.Default.CheckCircle,
                    accentColor = LifeOsSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(LifeOsScreen.TASKS) }
                )

                // Habit Consistency
                val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
                MetricWidget(
                    title = "Peak Streak",
                    value = "${maxStreak}d",
                    subtitle = "${habits.count { it.isCompletedToday }}/${habits.size} checked",
                    icon = Icons.Default.LocalFireDepartment,
                    accentColor = LifeOsWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(LifeOsScreen.GOALS_HABITS) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Net Cashflow / Spend
                MetricWidget(
                    title = "Expenses",
                    value = "$${totalExpenses.toInt()}",
                    subtitle = "Income: $${totalIncome.toInt()}",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = LifeOsAccentGold,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(LifeOsScreen.FINANCE) }
                )

                // Memory Context
                MetricWidget(
                    title = "AI Memories",
                    value = "${memories.size}",
                    subtitle = "Verified & active",
                    icon = Icons.Default.Memory,
                    accentColor = LifeOsPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(LifeOsScreen.MEMORY) }
                )
            }
        }

        // Today's Priority Tasks Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Priorities",
                    style = MaterialTheme.typography.titleLarge,
                    color = LifeOsTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "View all (${tasks.size})",
                    fontSize = 12.sp,
                    color = LifeOsAccentGold,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onNavigate(LifeOsScreen.TASKS) }
                        .padding(4.dp)
                )
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "All priority items completed. Take a breath or schedule deep work.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
                    )
                }
            }
        } else {
            items(pendingTasks.take(4)) { task ->
                TaskQuickRow(
                    task = task,
                    onToggle = { viewModel.toggleTaskStatus(task) },
                    onClick = { onNavigate(LifeOsScreen.TASKS) }
                )
            }
        }

        // Habit Daily Tracker Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Habit Rhythms",
                    style = MaterialTheme.typography.titleLarge,
                    color = LifeOsTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Goals & Streaks",
                    fontSize = 12.sp,
                    color = LifeOsAccentGold,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onNavigate(LifeOsScreen.GOALS_HABITS) }
                        .padding(4.dp)
                )
            }
        }

        items(habits) { habit ->
            HabitQuickRow(
                habit = habit,
                onToggle = { viewModel.toggleHabitToday(habit) }
            )
        }
    }
}

@Composable
private fun MetricWidget(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeOsTextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = LifeOsTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = LifeOsTextSecondary
            )
        }
    }
}

@Composable
private fun TaskQuickRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("task_quick_row_${task.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (task.status == "Completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle status",
                    tint = if (task.status == "Completed") LifeOsSuccess else LifeOsTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Due ${task.dueDate} • ${task.category}",
                        fontSize = 11.sp,
                        color = LifeOsTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            PriorityBadge(priority = task.priority)
        }
    }
}

@Composable
private fun HabitQuickRow(
    habit: HabitEntity,
    onToggle: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LifeOsTextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = LifeOsWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${habit.currentStreak} day streak (Best: ${habit.bestStreak})",
                        fontSize = 11.sp,
                        color = LifeOsTextSecondary
                    )
                }
            }

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (habit.isCompletedToday) LifeOsSuccess else LifeOsElevated,
                    contentColor = if (habit.isCompletedToday) Color.White else LifeOsAccentGold
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("habit_check_${habit.id}")
            ) {
                if (habit.isCompletedToday) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Done", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Check-In", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
