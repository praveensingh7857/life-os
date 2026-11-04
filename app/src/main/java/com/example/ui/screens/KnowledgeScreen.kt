package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.DocumentEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun KnowledgeScreen(viewModel: LifeOsViewModel) {
    val documents by viewModel.documents.collectAsState()
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
                        text = "Knowledge Vault",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Indexed strategic documents, mental models, and executive notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
                    )
                }
            }

            items(documents, key = { it.id }) { doc ->
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
                                text = doc.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LifeOsTextPrimary
                            )
                            IconButton(onClick = { viewModel.deleteDocument(doc.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LifeOsTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = doc.content,
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
                                    text = doc.category,
                                    fontSize = 11.sp,
                                    color = LifeOsAccentGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(text = "Added ${doc.dateAdded}", fontSize = 11.sp, color = LifeOsTextSecondary)
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
                .testTag("fab_add_doc")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Document Note")
        }
    }

    if (showAddDialog) {
        AddDocumentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, cat, content, tags ->
                viewModel.addDocument(title, cat, content, tags)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddDocumentDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, cat: String, content: String, tags: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Strategy") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = { Text("Add to Knowledge Vault", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Document Title") },
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
                    label = { Text("Note Content / Text") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, category, content, "notes") },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary)
            ) {
                Text("Save Document", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = LifeOsTextSecondary) } }
    )
}
