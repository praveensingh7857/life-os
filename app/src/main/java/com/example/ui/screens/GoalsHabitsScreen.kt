package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.GoalEntity
import com.example.data.model.HabitEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun GoalsHabitsScreen(viewModel: LifeOsViewModel) {
    val goals by viewModel.goals.collectAsState()
    val habits by viewModel.habits.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Goals & Habits",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Long-range vision translated into daily compounding execution",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary
                )
            }
        }

        // Section: Active Goals
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Strategic Milestones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = LifeOsTextPrimary
                )
                IconButton(onClick = { showAddGoalDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = LifeOsAccentGold)
                }
            }
        }

        items(goals, key = { it.id }) { goal ->
            GoalCard(
                goal = goal,
                onProgressChange = { p -> viewModel.updateGoalProgress(goal.id, p) },
                onDelete = { viewModel.deleteGoal(goal.id) }
            )
        }

        // Section: Daily Habit Streaks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Habit Rhythms",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = LifeOsTextPrimary
                )
                IconButton(onClick = { showAddHabitDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = LifeOsAccentGold)
                }
            }
        }

        items(habits, key = { it.id }) { habit ->
            HabitCard(
                habit = habit,
                onToggle = { viewModel.toggleHabitToday(habit) },
                onDelete = { viewModel.deleteHabit(habit.id) }
            )
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, desc, target, cat ->
                viewModel.addGoal(title, desc, target, cat, "")
                showAddGoalDialog = false
            }
        )
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { title, freq, cat ->
                viewModel.addHabit(title, freq, cat)
                showAddHabitDialog = false
            }
        )
    }
}

@Composable
private fun GoalCard(
    goal: GoalEntity,
    onProgressChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LifeOsTextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            if (goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = goal.description, style = MaterialTheme.typography.bodyMedium, color = LifeOsTextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Target: ${goal.targetDate}", fontSize = 11.sp, color = LifeOsTextSecondary)
                Text(text = "${goal.progressPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LifeOsAccentGold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { goal.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = LifeOsAccentGold,
                trackColor = LifeOsElevated
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { onProgressChange(goal.progressPercent + 10) },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = LifeOsElevated, contentColor = LifeOsAccentGold),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("+10% Progress", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = LifeOsWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${habit.currentStreak} day streak (Best: ${habit.bestStreak}) • ${habit.frequency}",
                        fontSize = 12.sp,
                        color = LifeOsTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (habit.isCompletedToday) LifeOsSuccess else LifeOsElevated,
                        contentColor = if (habit.isCompletedToday) Color.White else LifeOsAccentGold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (habit.isCompletedToday) "Completed" else "Check In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LifeOsTextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, target: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("Q4 2026") }
    var cat by remember { mutableStateOf("Career") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = { Text("New Strategic Goal", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description & Outcome") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, desc, target, cat) },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary)
            ) {
                Text("Create Goal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = LifeOsTextSecondary) } }
    )
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, freq: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("Daily") }
    var cat by remember { mutableStateOf("Focus") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = { Text("New Daily Habit", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Habit Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = LifeOsTextPrimary,
                    unfocusedTextColor = LifeOsTextPrimary,
                    focusedBorderColor = LifeOsAccentGold,
                    unfocusedBorderColor = LifeOsBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, freq, cat) },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary)
            ) {
                Text("Add Habit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = LifeOsTextSecondary) } }
    )
}
