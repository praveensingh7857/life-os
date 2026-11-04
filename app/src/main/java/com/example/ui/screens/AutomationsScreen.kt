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
import com.example.data.model.AutomationEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun AutomationsScreen(viewModel: LifeOsViewModel) {
    val automations by viewModel.automations.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Automation Center",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Autonomous executive routines executing in the background",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary
                )
            }
        }

        items(automations, key = { it.id }) { auto ->
            Surface(
                color = LifeOsCardBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (auto.isEnabled) LifeOsBorderGold else LifeOsBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (auto.isEnabled) LifeOsAccentGold else LifeOsTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = auto.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LifeOsTextPrimary
                            )
                        }

                        Switch(
                            checked = auto.isEnabled,
                            onCheckedChange = { viewModel.toggleAutomation(auto) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LifeOsAccentGold,
                                checkedTrackColor = LifeOsElevated,
                                uncheckedThumbColor = LifeOsTextSecondary,
                                uncheckedTrackColor = LifeOsCardBg
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = auto.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LifeOsTextSecondary
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
                                text = "Trigger: ${auto.trigger}",
                                fontSize = 11.sp,
                                color = LifeOsTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Last executed: ${auto.lastRun}",
                            fontSize = 11.sp,
                            color = if (auto.isEnabled) LifeOsSuccess else LifeOsTextSecondary
                        )
                    }
                }
            }
        }
    }
}
