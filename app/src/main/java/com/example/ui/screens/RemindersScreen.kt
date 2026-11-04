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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReminderEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun RemindersScreen(viewModel: LifeOsViewModel) {
    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Smart Reminders",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Time-sensitive checkpoints, recurring reminders & notifications",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
                    )
                }
            }

            items(reminders, key = { it.id }) { reminder ->
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(onClick = { viewModel.toggleReminder(reminder) }) {
                                Icon(
                                    imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Toggle",
                                    tint = if (reminder.isCompleted) LifeOsSuccess else LifeOsTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = reminder.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (reminder.isCompleted) LifeOsTextSecondary else LifeOsTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = LifeOsAccentGold, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${reminder.date} at ${reminder.time} • ${reminder.category}",
                                        fontSize = 11.sp,
                                        color = LifeOsTextSecondary
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.deleteReminder(reminder.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LifeOsTextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = LifeOsAccentGold,
            contentColor = LifeOsBgPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .testTag("fab_add_reminder")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Reminder")
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, time, date, cat ->
                viewModel.addReminder(title, time, date, cat, false, "None")
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, time: String, date: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("10:00 AM") }
    var date by remember { mutableStateOf("Tomorrow") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = { Text("New Reminder", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What should I remind you about?") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LifeOsTextPrimary,
                            unfocusedTextColor = LifeOsTextPrimary,
                            focusedBorderColor = LifeOsAccentGold,
                            unfocusedBorderColor = LifeOsBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LifeOsTextPrimary,
                            unfocusedTextColor = LifeOsTextPrimary,
                            focusedBorderColor = LifeOsAccentGold,
                            unfocusedBorderColor = LifeOsBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, time, date, "Executive") },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary)
            ) {
                Text("Schedule", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = LifeOsTextSecondary) } }
    )
}
