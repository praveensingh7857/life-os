package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiExecutiveOrchestrator
import com.example.data.LifeOsDatabase
import com.example.data.model.*
import com.example.data.repository.LifeOsRepository
import com.example.firebase.AuthManager
import com.example.firebase.FirestoreLifeOsRepository
import com.example.voice.VoiceAssistantManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class LifeOsScreen(val label: String) {
    DASHBOARD("Dashboard"),
    ASSISTANT("AI Assistant"),
    MY_DAY("My Day"),
    TASKS("Tasks"),
    REMINDERS("Reminders"),
    MEMORY("AI Memory"),
    GOALS_HABITS("Goals & Habits"),
    FINANCE("Finance"),
    KNOWLEDGE("Knowledge Vault"),
    AUTOMATIONS("Automations"),
    SETTINGS("Settings")
}

class LifeOsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LifeOsDatabase.getDatabase(application)
    val repository = LifeOsRepository(database)
    val firestoreRepository = FirestoreLifeOsRepository(application)
    val orchestrator = AiExecutiveOrchestrator(repository, firestoreRepository)
    val voiceManager = VoiceAssistantManager(application)
    val authManager = AuthManager(application)

    // Current Screen Navigation
    private val _currentScreen = MutableStateFlow(LifeOsScreen.DASHBOARD)
    val currentScreen: StateFlow<LifeOsScreen> = _currentScreen.asStateFlow()

    // Global Search Query
    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    // Selected AI Department in Assistant screen
    private val _selectedDepartment = MutableStateFlow("Chief of Staff")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    // AI is thinking / generating response
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // User Feedback Message (Snackbar)
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Firebase Auth State
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    val isAuthLoading: StateFlow<Boolean> = authManager.isLoading

    // Streams from Room DB
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTasks: StateFlow<List<TaskEntity>> = repository.pendingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderEntity>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val finances: StateFlow<List<FinanceEntity>> = repository.allFinances
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<AutomationEntity>> = repository.allAutomations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Attempt silent auto sign-in and seed local database if needed
        viewModelScope.launch {
            authManager.attemptAutoSignIn(viewModelScope)
            if (tasks.value.isEmpty()) {
                repository.seedInitialData()
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        authManager.signInWithGoogle(activity, viewModelScope)
    }

    fun signOut() {
        authManager.signOut(viewModelScope)
        showFeedback("Signed out of Google Account")
    }

    fun navigateTo(screen: LifeOsScreen) {
        _currentScreen.value = screen
    }

    fun setGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    fun setSelectedDepartment(department: String) {
        _selectedDepartment.value = department
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    fun showFeedback(msg: String) {
        _userFeedbackMessage.value = msg
    }

    // AI Chat & Tool Processing
    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        val currentDept = _selectedDepartment.value

        viewModelScope.launch {
            val userMsg = ChatMessageEntity(
                sender = "user",
                department = currentDept,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            repository.insertChatMessage(userMsg)

            _isAiThinking.value = true

            val result = orchestrator.processUserPrompt(text, currentDept)

            val aiMsg = ChatMessageEntity(
                sender = "ai",
                department = result.department,
                text = result.replyText,
                toolActionType = result.toolExecuted,
                toolActionData = result.toolResultData,
                timestamp = System.currentTimeMillis()
            )
            repository.insertChatMessage(aiMsg)

            _isAiThinking.value = false

            voiceManager.speak(result.replyText)
        }
    }

    fun triggerVoiceInput() {
        if (voiceManager.isListening.value) {
            voiceManager.stopListening()
            return
        }

        voiceManager.startListening(
            onResult = { spokenText ->
                sendUserMessage(spokenText)
            },
            onError = { error ->
                _userFeedbackMessage.value = error
            }
        )
    }

    // Task Management
    fun addTask(title: String, description: String, priority: String, category: String, dueDate: String) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.ifBlank { "New Task" },
                description = description,
                priority = priority,
                category = category,
                dueDate = dueDate.ifBlank { "Today" }
            )
            repository.insertTask(task)
            firestoreRepository.syncTaskToFirestore(task)
            showFeedback("Task '${task.title}' added & synced")
        }
    }

    fun toggleTaskStatus(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = if (task.status == "Completed") "Todo" else "Completed"
            val updated = task.copy(status = newStatus)
            repository.updateTaskStatus(task.id, newStatus)
            firestoreRepository.syncTaskToFirestore(updated)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            showFeedback("Task removed")
        }
    }

    // Reminder Management
    fun addReminder(title: String, time: String, date: String, category: String, isRecurring: Boolean, rule: String) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title.ifBlank { "Reminder" },
                time = time,
                date = date,
                category = category,
                isRecurring = isRecurring,
                recurrenceRule = rule
            )
            repository.insertReminder(reminder)
            firestoreRepository.syncReminderToFirestore(reminder)
            showFeedback("Reminder scheduled for $time")
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.toggleReminder(reminder.id, !reminder.isCompleted)
            firestoreRepository.syncReminderToFirestore(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            repository.deleteReminder(id)
            showFeedback("Reminder deleted")
        }
    }

    // Memory Management
    fun addMemory(title: String, content: String, category: String) {
        viewModelScope.launch {
            val mem = MemoryEntity(
                title = title.ifBlank { "Personal Preference" },
                content = content,
                category = category,
                isApproved = true,
                source = "User Added"
            )
            repository.insertMemory(mem)
            firestoreRepository.syncMemoryToFirestore(mem)
            showFeedback("Memory added to AI context & Firestore")
        }
    }

    fun toggleMemoryApproval(memory: MemoryEntity) {
        viewModelScope.launch {
            val updated = memory.copy(isApproved = !memory.isApproved)
            repository.setMemoryApproved(memory.id, updated.isApproved)
            firestoreRepository.syncMemoryToFirestore(updated)
            showFeedback(if (updated.isApproved) "Memory approved for AI context" else "Memory revoked")
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
            showFeedback("Memory removed")
        }
    }

    // Goal Management
    fun addGoal(title: String, description: String, targetDate: String, category: String, milestones: String) {
        viewModelScope.launch {
            val goal = GoalEntity(
                title = title.ifBlank { "Quarterly Milestone" },
                description = description,
                targetDate = targetDate,
                category = category,
                milestonesRaw = milestones,
                progressPercent = 0
            )
            repository.insertGoal(goal)
            showFeedback("Goal '${goal.title}' created")
        }
    }

    fun updateGoalProgress(goalId: String, newProgress: Int) {
        viewModelScope.launch {
            repository.updateGoalProgress(goalId, newProgress.coerceIn(0, 100))
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            repository.deleteGoal(id)
            showFeedback("Goal deleted")
        }
    }

    // Habit Management
    fun addHabit(title: String, frequency: String, category: String) {
        viewModelScope.launch {
            val habit = HabitEntity(
                title = title.ifBlank { "New Habit" },
                frequency = frequency,
                category = category,
                currentStreak = 0,
                bestStreak = 0,
                isCompletedToday = false
            )
            repository.insertHabit(habit)
            showFeedback("Habit '${habit.title}' initialized")
        }
    }

    fun toggleHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val wasDone = habit.isCompletedToday
            val newDone = !wasDone
            val newStreak = if (newDone) habit.currentStreak + 1 else (habit.currentStreak - 1).coerceAtLeast(0)
            val best = maxOf(habit.bestStreak, newStreak)
            repository.updateHabit(
                habit.copy(
                    isCompletedToday = newDone,
                    currentStreak = newStreak,
                    bestStreak = best,
                    lastCompletedDate = if (newDone) SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) else ""
                )
            )
            if (newDone) {
                showFeedback("🔥 Streak updated! ${newStreak} consecutive days")
            }
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            repository.deleteHabit(id)
            showFeedback("Habit deleted")
        }
    }

    // Finance Management
    fun addFinance(title: String, amount: Double, type: String, category: String) {
        viewModelScope.launch {
            val fin = FinanceEntity(
                title = title.ifBlank { "Transaction" },
                amount = amount,
                type = type,
                category = category,
                date = "Today"
            )
            repository.insertFinance(fin)
            firestoreRepository.syncFinanceToFirestore(fin)
            showFeedback("${fin.type} of $${fin.amount} logged & synced")
        }
    }

    fun deleteFinance(id: String) {
        viewModelScope.launch {
            repository.deleteFinance(id)
            showFeedback("Transaction removed")
        }
    }

    // Document Management
    fun addDocument(title: String, category: String, content: String, tags: String) {
        viewModelScope.launch {
            val doc = DocumentEntity(
                title = title.ifBlank { "Document Note" },
                category = category,
                content = content,
                summary = if (content.length > 80) content.take(75) + "..." else content,
                tags = tags
            )
            repository.insertDocument(doc)
            showFeedback("Document '${doc.title}' stored in Vault")
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            repository.deleteDocument(id)
            showFeedback("Document removed")
        }
    }

    // Automation Management
    fun toggleAutomation(automation: AutomationEntity) {
        viewModelScope.launch {
            repository.toggleAutomation(automation.id, !automation.isEnabled)
            showFeedback("${automation.title} ${if (!automation.isEnabled) "activated" else "paused"}")
        }
    }

    // Reset Database to Demo
    fun resetToDemoData() {
        viewModelScope.launch {
            repository.seedInitialData()
            showFeedback("Reset database to executive configuration")
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.cleanup()
    }
}
