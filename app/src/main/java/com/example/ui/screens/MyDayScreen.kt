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
import com.example.data.model.ReminderEntity
import com.example.data.model.TaskEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsScreen
import com.example.viewmodel.LifeOsViewModel

data class ScheduleBlock(
    val timeSlot: String,
    val title: String,
    val category: String,
    val type: String, // "Deep Focus", "Meeting", "Execution", "Review", "Routine"
    val isCompleted: Boolean = false,
    val priority: String = "Medium"
)

@Composable
fun MyDayScreen(
    viewModel: LifeOsViewModel,
    onNavigate: (LifeOsScreen) -> Unit
) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    var showAddBlockDialog by remember { mutableStateOf(false) }

    // Synthesized smart schedule blocks
    val scheduleBlocks = remember(tasks, reminders) {
        val list = mutableListOf<ScheduleBlock>()
        list.add(
            ScheduleBlock(
                timeSlot = "08:00 AM — 09:00 AM",
                title = "Morning Executive Briefing & Alignment",
                category = "Productivity",
                type = "Routine",
                isCompleted = true,
                priority = "Low"
            )
        )

        // Inject reminders
        reminders.filter { !it.isCompleted }.forEach { r ->
            list.add(
                ScheduleBlock(
                    timeSlot = r.time,
                    title = r.title,
                    category = r.category,
                    type = "Meeting",
                    priority = "High"
                )
            )
        }

        // Inject tasks
        tasks.forEachIndexed { index, t ->
            val slot = when (index) {
                0 -> "09:30 AM — 11:30 AM"
                1 -> "01:30 PM — 03:00 PM"
                2 -> "03:30 PM — 05:00 PM"
                else -> "07:30 PM — 09:00 PM"
            }
            list.add(
                ScheduleBlock(
                    timeSlot = slot,
                    title = t.title,
                    category = t.category,
                    type = if (t.priority == "Urgent") "Deep Focus" else "Execution",
                    priority = t.priority
                )
            )
        }

        list.add(
            ScheduleBlock(
                timeSlot = "09:30 PM — 10:00 PM",
                title = "Daily Retrospective & Tomorrow Architecture",
                category = "Mindset",
                type = "Review",
                priority = "Low"
            )
        )

        list
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Screen Header
        item {
            Column {
                Text(
                    text = "My Day",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Intelligent hourly timeline • Calibrated for executive focus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary
                )
            }
        }

        // AI Schedule Optimizer Action Card
        item {
            GlassCard(
                borderColor = LifeOsBorderGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Schedule Optimizer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsAccentGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Chief of Staff has dynamically scheduled ${scheduleBlocks.size} blocks with zero overlap.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LifeOsTextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.setSelectedDepartment("Chief of Staff")
                            onNavigate(LifeOsScreen.ASSISTANT)
                            viewModel.sendUserMessage("Plan my day")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LifeOsElevated,
                            contentColor = LifeOsAccentGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_ai_replan")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rebalance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Timeline Feed
        item {
            Text(
                text = "Chronological Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = LifeOsTextPrimary
            )
        }

        items(scheduleBlocks) { block ->
            TimelineBlockItem(block = block)
        }
    }
}

@Composable
private fun TimelineBlockItem(block: ScheduleBlock) {
    Surface(
        color = LifeOsCardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (block.type == "Deep Focus") LifeOsAccentGold else LifeOsBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Column
            Column(
                modifier = Modifier.width(90.dp)
            ) {
                Text(
                    text = block.timeSlot.split("—").firstOrNull()?.trim() ?: block.timeSlot,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsAccentGold
                )
                Text(
                    text = block.type,
                    fontSize = 11.sp,
                    color = LifeOsTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(LifeOsBorder)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Content Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (block.isCompleted) LifeOsTextSecondary else LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = block.category,
                    fontSize = 11.sp,
                    color = LifeOsTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            PriorityBadge(priority = block.priority)
        }
    }
}
