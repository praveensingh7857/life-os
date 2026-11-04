package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.TaskEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: LifeOsViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // All, Todo, In Progress, Completed, Urgent
    var isKanbanMode by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredTasks = remember(tasks, searchQuery, selectedFilter) {
        tasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() ||
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Todo" -> task.status == "Todo"
                "In Progress" -> task.status == "In Progress"
                "Completed" -> task.status == "Completed"
                "Urgent" -> task.priority == "Urgent"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Row with View Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Task Operations",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsTextPrimary
                    )
                    Text(
                        text = "${filteredTasks.size} tasks displayed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
                    )
                }

                IconButton(
                    onClick = { isKanbanMode = !isKanbanMode },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LifeOsElevated)
                        .border(1.dp, LifeOsBorder, RoundedCornerShape(10.dp))
                        .testTag("btn_toggle_kanban")
                ) {
                    Icon(
                        imageVector = if (isKanbanMode) Icons.Default.ViewList else Icons.Default.ViewKanban,
                        contentDescription = "Toggle View",
                        tint = LifeOsAccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search tasks, projects, categories...", color = LifeOsTextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LifeOsAccentGold) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = LifeOsTextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_search_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LifeOsCardBg,
                    unfocusedContainerColor = LifeOsCardBg,
                    focusedBorderColor = LifeOsAccentGold,
                    unfocusedBorderColor = LifeOsBorder,
                    focusedTextColor = LifeOsTextPrimary,
                    unfocusedTextColor = LifeOsTextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Pills Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Todo", "In Progress", "Completed", "Urgent").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) LifeOsElevated else LifeOsCardBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) LifeOsAccentGold else LifeOsBorder
                        ),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content List or Kanban View
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks match your criteria.\nTap '+' to create an executive task.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = LifeOsTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskDetailedRow(
                            task = task,
                            onToggle = { viewModel.toggleTaskStatus(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Task
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            containerColor = LifeOsAccentGold,
            contentColor = LifeOsBgPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .testTag("fab_add_task")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }

    if (showAddTaskDialog) {
        CreateTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, priority, category, dueDate ->
                viewModel.addTask(title, desc, priority, category, dueDate)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
private fun TaskDetailedRow(
    task: TaskEntity,
    onToggle: () -> Unit,
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (task.status == "Completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle status",
                            tint = if (task.status == "Completed") LifeOsSuccess else LifeOsTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.status == "Completed") LifeOsTextSecondary else LifeOsTextPrimary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = LifeOsTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary,
                    modifier = Modifier.padding(start = 36.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriorityBadge(priority = task.priority)

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = LifeOsElevated
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 11.sp,
                            color = LifeOsTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (task.status) {
                            "Completed" -> Color(0x2A10B981)
                            "In Progress" -> Color(0x2AF59E0B)
                            else -> LifeOsElevated
                        }
                    ) {
                        Text(
                            text = task.status,
                            fontSize = 11.sp,
                            color = when (task.status) {
                                "Completed" -> LifeOsSuccess
                                "In Progress" -> LifeOsWarning
                                else -> LifeOsTextSecondary
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Due: ${task.dueDate}",
                    fontSize = 11.sp,
                    color = LifeOsTextSecondary
                )
            }
        }
    }
}

@Composable
private fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, priority: String, category: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("High") }
    var category by remember { mutableStateOf("Executive") }
    var dueDate by remember { mutableStateOf("Today, 5:00 PM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = {
            Text("Create Executive Task", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_task_title")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description / Directives") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Urgent", "High", "Medium", "Low").forEach { p ->
                        val isSelected = priority == p
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) LifeOsElevated else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) LifeOsAccentGold else LifeOsBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { priority = p }
                        ) {
                            Text(
                                text = p,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, desc, priority, category, dueDate) },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary),
                modifier = Modifier.testTag("dialog_task_confirm")
            ) {
                Text("Create Task", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LifeOsTextSecondary)
            }
        }
    )
}
