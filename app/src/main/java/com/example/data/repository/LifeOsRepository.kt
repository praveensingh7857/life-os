package com.example.data.repository

import com.example.data.LifeOsDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LifeOsRepository(private val database: LifeOsDatabase) {

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = database.taskDao().getPendingTasks()

    suspend fun insertTask(task: TaskEntity) = database.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskEntity) = database.taskDao().updateTask(task)
    suspend fun updateTaskStatus(id: String, status: String) = database.taskDao().updateTaskStatus(id, status)
    suspend fun deleteTask(id: String) = database.taskDao().deleteTaskById(id)

    // Reminders
    val allReminders: Flow<List<ReminderEntity>> = database.reminderDao().getAllReminders()
    suspend fun insertReminder(reminder: ReminderEntity) = database.reminderDao().insertReminder(reminder)
    suspend fun toggleReminder(id: String, completed: Boolean) = database.reminderDao().toggleReminder(id, completed)
    suspend fun deleteReminder(id: String) = database.reminderDao().deleteReminderById(id)

    // Memories
    val allMemories: Flow<List<MemoryEntity>> = database.memoryDao().getAllMemories()
    val approvedMemories: Flow<List<MemoryEntity>> = database.memoryDao().getApprovedMemories()
    suspend fun insertMemory(memory: MemoryEntity) = database.memoryDao().insertMemory(memory)
    suspend fun setMemoryApproved(id: String, approved: Boolean) = database.memoryDao().setMemoryApproved(id, approved)
    suspend fun deleteMemory(id: String) = database.memoryDao().deleteMemoryById(id)

    // Goals
    val allGoals: Flow<List<GoalEntity>> = database.goalDao().getAllGoals()
    suspend fun insertGoal(goal: GoalEntity) = database.goalDao().insertGoal(goal)
    suspend fun updateGoalProgress(id: String, progress: Int) = database.goalDao().updateGoalProgress(id, progress)
    suspend fun deleteGoal(id: String) = database.goalDao().deleteGoalById(id)

    // Habits
    val allHabits: Flow<List<HabitEntity>> = database.habitDao().getAllHabits()
    suspend fun insertHabit(habit: HabitEntity) = database.habitDao().insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = database.habitDao().updateHabit(habit)
    suspend fun deleteHabit(id: String) = database.habitDao().deleteHabitById(id)

    // Finances
    val allFinances: Flow<List<FinanceEntity>> = database.financeDao().getAllFinances()
    suspend fun insertFinance(finance: FinanceEntity) = database.financeDao().insertFinance(finance)
    suspend fun deleteFinance(id: String) = database.financeDao().deleteFinanceById(id)

    // Documents
    val allDocuments: Flow<List<DocumentEntity>> = database.documentDao().getAllDocuments()
    suspend fun insertDocument(doc: DocumentEntity) = database.documentDao().insertDocument(doc)
    suspend fun deleteDocument(id: String) = database.documentDao().deleteDocumentById(id)

    // Chat
    val chatMessages: Flow<List<ChatMessageEntity>> = database.chatMessageDao().getAllMessages()
    suspend fun insertChatMessage(msg: ChatMessageEntity) = database.chatMessageDao().insertMessage(msg)
    suspend fun clearChat() = database.chatMessageDao().clearAllMessages()

    // Automations
    val allAutomations: Flow<List<AutomationEntity>> = database.automationDao().getAllAutomations()
    suspend fun toggleAutomation(id: String, enabled: Boolean) = database.automationDao().toggleAutomation(id, enabled)

    // Reset or seed data
    suspend fun seedInitialData() {
        LifeOsDatabase.populateInitialData(database)
    }
}
