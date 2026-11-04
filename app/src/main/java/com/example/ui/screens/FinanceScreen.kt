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
import com.example.data.model.FinanceEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel
import java.util.Locale

@Composable
fun FinanceScreen(viewModel: LifeOsViewModel) {
    val finances by viewModel.finances.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalIncome = finances.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpenses = finances.filter { it.type == "Expense" }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpenses
    val monthlyBudget = 2500.0
    val budgetPercent = ((totalExpenses / monthlyBudget) * 100).toInt().coerceIn(0, 100)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Personal Finance",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Private financial ledger • Spending analytics & cashflow",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
                    )
                }
            }

            // Summary Card (Balance & Cashflow)
            item {
                GlassCard(
                    borderColor = LifeOsBorderGold,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Net Cash Balance",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = LifeOsTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format(Locale.US, "%,.2f", netBalance)}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (netBalance >= 0) LifeOsAccentGold else LifeOsAlert
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Total Income", fontSize = 11.sp, color = LifeOsTextSecondary)
                            Text(
                                text = "+$${String.format(Locale.US, "%,.2f", totalIncome)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LifeOsSuccess
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Total Expenses", fontSize = 11.sp, color = LifeOsTextSecondary)
                            Text(
                                text = "-$${String.format(Locale.US, "%,.2f", totalExpenses)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LifeOsAlert
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Monthly Budget Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Monthly Budget ($${monthlyBudget.toInt()})", fontSize = 11.sp, color = LifeOsTextSecondary)
                        Text(text = "$budgetPercent% used", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LifeOsAccentGold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { budgetPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (budgetPercent > 85) LifeOsAlert else LifeOsAccentGold,
                        trackColor = LifeOsElevated
                    )
                }
            }

            // Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = LifeOsTextPrimary
                    )
                }
            }

            items(finances, key = { it.id }) { item ->
                TransactionRow(
                    transaction = item,
                    onDelete = { viewModel.deleteFinance(item.id) }
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = LifeOsAccentGold,
            contentColor = LifeOsBgPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .testTag("fab_add_finance")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Log Transaction")
        }
    }

    if (showAddDialog) {
        AddFinanceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, type, category ->
                viewModel.addFinance(title, amount, type, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TransactionRow(
    transaction: FinanceEntity,
    onDelete: () -> Unit
) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(12.dp),
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (transaction.type == "Income") Color(0x2A10B981) else Color(0x2AEF4444),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (transaction.type == "Income") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (transaction.type == "Income") LifeOsSuccess else LifeOsAlert,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = LifeOsTextPrimary
                    )
                    Text(
                        text = "${transaction.category} • ${transaction.date}",
                        fontSize = 11.sp,
                        color = LifeOsTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (transaction.type == "Income") "+" else "-"}$${String.format(Locale.US, "%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == "Income") LifeOsSuccess else LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LifeOsTextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AddFinanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, amount: Double, type: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var category by remember { mutableStateOf("Business") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = { Text("Log Transaction", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Expense", "Income").forEach { t ->
                        val isSelected = type == t
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) LifeOsElevated else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LifeOsAccentGold else LifeOsBorder),
                            modifier = Modifier.weight(1f).clickable { type = t }
                        ) {
                            Text(
                                text = t,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) LifeOsAccentGold else LifeOsTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Description (e.g. Lunch with Client)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($)") },
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
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    onConfirm(title, amount, type, category)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LifeOsAccentGold, contentColor = LifeOsBgPrimary)
            ) {
                Text("Log Entry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = LifeOsTextSecondary) } }
    )
}
