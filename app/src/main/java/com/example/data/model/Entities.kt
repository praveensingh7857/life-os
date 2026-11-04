package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: String = "Medium", // Urgent, High, Medium, Low
    val status: String = "Todo", // Todo, In Progress, Completed
    val category: String = "Work", // Executive, Work, Personal, Health, Finance, Strategy
    val dueDate: String = "Today",
    val subtasksRaw: String = "", // newline separated
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val time: String = "09:00 AM",
    val date: String = "Today",
    val category: String = "General",
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceRule: String = "None",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String = "Personal Preferences", // Personal Preferences, Work Routines, Important People, Ongoing Projects, Life Goals
    val isApproved: Boolean = true,
    val confidence: Float = 0.95f,
    val source: String = "User Confirmed",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val targetDate: String = "Q4 2026",
    val progressPercent: Int = 0,
    val category: String = "Career",
    val milestonesRaw: String = "", // newline separated
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val frequency: String = "Daily",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isCompletedToday: Boolean = false,
    val category: String = "Focus",
    val lastCompletedDate: String = ""
)

@Entity(tableName = "finances")
data class FinanceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: String = "Expense", // Expense, Income
    val category: String = "Business", // Food, Travel, Shopping, Education, Business, Bills, Entertainment, Other
    val date: String = "Today",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String = "Strategy",
    val content: String,
    val summary: String = "",
    val tags: String = "executive, notes",
    val dateAdded: String = "Nov 3"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String = "main_session",
    val sender: String, // "user" or "ai"
    val department: String = "Chief of Staff",
    val text: String,
    val toolActionType: String? = null,
    val toolActionData: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val trigger: String,
    val action: String,
    val isEnabled: Boolean = true,
    val lastRun: String = "Today, 8:00 AM"
)
