package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.model.ChatMessageEntity
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel
import kotlinx.coroutines.launch

@Composable
fun AssistantScreen(viewModel: LifeOsViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val selectedDept by viewModel.selectedDepartment.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val departments = listOf(
        "Chief of Staff",
        "Productivity Manager",
        "Research Assistant",
        "Finance Assistant",
        "AI Goal Coach",
        "Memory Manager",
        "Communication Assistant"
    )

    val suggestedPrompts = listOf(
        "Plan my day",
        "What are my pending tasks?",
        "Remind me tomorrow at 10 AM to call client",
        "Remember that I prefer deep work at night",
        "Log $45 expense for lunch",
        "Summarize my week"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
            .padding(bottom = 80.dp)
    ) {
        // Department Filter Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LifeOsBgSecondary)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            departments.forEach { dept ->
                DepartmentBadge(
                    department = dept,
                    isSelected = selectedDept == dept,
                    onClick = { viewModel.setSelectedDepartment(dept) }
                )
            }
        }

        // Active Department Indicator Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LifeOsCardBg)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LifeOsAccentGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Active Agent: $selectedDept",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeOsTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isSpeaking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.voiceManager.stopSpeaking() }
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speaking",
                        tint = LifeOsAccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mute Voice", fontSize = 11.sp, color = LifeOsAccentGold)
                }
            }
        }

        // Chat Conversation Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            items(messages) { msg ->
                ChatMessageItem(
                    message = msg,
                    onSpeak = { viewModel.voiceManager.speak(msg.text) }
                )
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = LifeOsAccentGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$selectedDept is synthesizing context...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LifeOsTextSecondary
                        )
                    }
                }
            }
        }

        // Suggested Prompts Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestedPrompts.forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = LifeOsElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
                    modifier = Modifier.clickable {
                        viewModel.sendUserMessage(prompt)
                    }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsAccentGoldLight,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Chat Input Bar with Voice Button
        Surface(
            color = LifeOsBgSecondary,
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice Input Button
                IconButton(
                    onClick = { viewModel.triggerVoiceInput() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isListening) LifeOsAlert else LifeOsElevated)
                        .border(1.dp, if (isListening) LifeOsAlert else LifeOsBorder, CircleShape)
                        .testTag("chat_voice_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isListening) Color.White else LifeOsAccentGold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = {
                        Text(
                            text = if (isListening) "Listening to voice..." else "Instruct $selectedDept...",
                            fontSize = 14.sp,
                            color = LifeOsTextSecondary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_textfield"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LifeOsCardBg,
                        unfocusedContainerColor = LifeOsCardBg,
                        focusedBorderColor = LifeOsAccentGold,
                        unfocusedBorderColor = LifeOsBorder,
                        focusedTextColor = LifeOsTextPrimary,
                        unfocusedTextColor = LifeOsTextPrimary
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (inputPrompt.isNotBlank()) {
                            viewModel.sendUserMessage(inputPrompt)
                            inputPrompt = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LifeOsAccentGold)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = LifeOsBgPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessageEntity,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Text(
                    text = message.department,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsAccentGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read aloud",
                        tint = LifeOsTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) LifeOsElevated else LifeOsCardBg,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) LifeOsBorderGold else LifeOsBorder
            ),
            modifier = Modifier.widthIn(max = 330.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LifeOsTextPrimary,
                    lineHeight = 22.sp
                )

                // Tool Execution Receipt Pill if tool executed
                if (!message.toolActionType.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LifeOsElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsAccentGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LifeOsSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Action Confirmed: ${message.toolActionType} (${message.toolActionData ?: "Success"})",
                                fontSize = 11.sp,
                                color = LifeOsAccentGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
