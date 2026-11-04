package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.MemoryEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun MemoryScreen(viewModel: LifeOsViewModel) {
    val memories by viewModel.memories.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Work Routines", "Personal Preferences", "Important People", "Ongoing Projects")

    val filteredMemories = remember(memories, selectedCategory) {
        if (selectedCategory == "All") memories
        else memories.filter { it.category == selectedCategory }
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

            Text(
                text = "AI Memory System",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = LifeOsTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Transparent long-term context with user sovereignty & consent",
                style = MaterialTheme.typography.bodyMedium,
                color = LifeOsTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) LifeOsElevated else LifeOsCardBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) LifeOsAccentGold else LifeOsBorder
                        ),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredMemories, key = { it.id }) { memory ->
                    MemoryCard(
                        memory = memory,
                        onToggleApproved = { viewModel.toggleMemoryApproval(memory) },
                        onDelete = { viewModel.deleteMemory(memory.id) }
                    )
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
                .testTag("fab_add_memory")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Memory")
        }
    }

    if (showAddDialog) {
        AddMemoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, cat ->
                viewModel.addMemory(title, content, cat)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryEntity,
    onToggleApproved: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (memory.isApproved) LifeOsBorderGold else LifeOsBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = LifeOsAccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = memory.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = LifeOsTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleApproved, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (memory.isApproved) Icons.Default.Verified else Icons.Default.Lock,
                            contentDescription = "Consent",
                            tint = if (memory.isApproved) LifeOsSuccess else LifeOsWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = LifeOsTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = memory.content,
                style = MaterialTheme.typography.bodyLarge,
                color = LifeOsTextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = LifeOsElevated
                ) {
                    Text(
                        text = memory.category,
                        fontSize = 11.sp,
                        color = LifeOsAccentGoldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = if (memory.isApproved) "Active Context • Confidence ${(memory.confidence * 100).toInt()}%" else "Revoked from AI",
                    fontSize = 11.sp,
                    color = if (memory.isApproved) LifeOsSuccess else LifeOsWarning
                )
            }
        }
    }
}

@Composable
private fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal Preferences") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = { Text("Store Personal Memory", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Memory Key / Subject") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Context & Directives") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, content, category) },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary)
            ) {
                Text("Approve & Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = LifeOsTextSecondary) }
        }
    )
}
