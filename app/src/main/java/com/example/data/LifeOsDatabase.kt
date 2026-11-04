package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        ReminderEntity::class,
        MemoryEntity::class,
        GoalEntity::class,
        HabitEntity::class,
        FinanceEntity::class,
        DocumentEntity::class,
        ChatMessageEntity::class,
        AutomationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeOsDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun reminderDao(): ReminderDao
    abstract fun memoryDao(): MemoryDao
    abstract fun goalDao(): GoalDao
    abstract fun habitDao(): HabitDao
    abstract fun financeDao(): FinanceDao
    abstract fun documentDao(): DocumentDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun automationDao(): AutomationDao

    companion object {
        @Volatile
        private var INSTANCE: LifeOsDatabase? = null

        fun getDatabase(context: Context): LifeOsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOsDatabase::class.java,
                    "lifeos_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial executive seed data
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    populateInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(db: LifeOsDatabase) {
            // Seed Tasks
            db.taskDao().insertTasks(
                listOf(
                    TaskEntity(
                        title = "Finalize Q4 Strategic Product Roadmap",
                        description = "Align engineering milestones and AI department deliverables with core KPIs",
                        priority = "Urgent",
                        status = "In Progress",
                        category = "Executive",
                        dueDate = "Today, 4:00 PM",
                        subtasksRaw = "Draft technical specs\nReview security boundaries\nExecutive presentation"
                    ),
                    TaskEntity(
                        title = "Review AI Executive Team Orchestration Metrics",
                        description = "Verify multi-agent query response latency and accuracy",
                        priority = "High",
                        status = "Todo",
                        category = "Work",
                        dueDate = "Today, 6:30 PM",
                        subtasksRaw = "Inspect token usage\nEvaluate memory retrieval confidence"
                    ),
                    TaskEntity(
                        title = "Quarterly Investment Portfolio Balancing",
                        description = "Rebalance asset distribution per fiscal target guidelines",
                        priority = "Medium",
                        status = "Todo",
                        category = "Finance",
                        dueDate = "Tomorrow, 11:00 AM",
                        subtasksRaw = "Check index performance\nAllocate tech reserves"
                    ),
                    TaskEntity(
                        title = "Deep Focus: Read Research on Autonomous Systems",
                        description = "Extract key takeaways on autonomous agent scheduling and memory hierarchies",
                        priority = "Low",
                        status = "Completed",
                        category = "Personal",
                        dueDate = "Yesterday",
                        subtasksRaw = "Read paper\nAdd highlights to Knowledge Vault"
                    )
                )
            )

            // Seed Reminders
            db.reminderDao().insertReminders(
                listOf(
                    ReminderEntity(
                        title = "Executive Sync with Product Architecture Team",
                        time = "10:00 AM",
                        date = "Today",
                        category = "Business",
                        isCompleted = false,
                        isRecurring = true,
                        recurrenceRule = "Weekdays"
                    ),
                    ReminderEntity(
                        title = "Hydration & Posture Reset",
                        time = "02:00 PM",
                        date = "Today",
                        category = "Health",
                        isCompleted = false,
                        isRecurring = true,
                        recurrenceRule = "Daily"
                    ),
                    ReminderEntity(
                        title = "Review Weekly Goals and Habit Streaks",
                        time = "07:00 PM",
                        date = "Friday",
                        category = "Personal",
                        isCompleted = false,
                        isRecurring = true,
                        recurrenceRule = "Weekly on Fri"
                    )
                )
            )

            // Seed Memories
            db.memoryDao().insertMemories(
                listOf(
                    MemoryEntity(
                        title = "Deep Work Schedule Preference",
                        content = "Prefers uninterrupted deep work blocks in the evening between 8:00 PM and 11:00 PM. Avoid scheduling non-urgent meetings during this window.",
                        category = "Work Routines",
                        isApproved = true,
                        confidence = 0.98f,
                        source = "User Confirmed"
                    ),
                    MemoryEntity(
                        title = "Dietary & Health Profile",
                        content = "Low-glycemic Mediterranean nutrition, black coffee only before noon, aiming for 8 hours recovery sleep.",
                        category = "Personal Preferences",
                        isApproved = true,
                        confidence = 0.95f,
                        source = "User Confirmed"
                    ),
                    MemoryEntity(
                        title = "Primary Key Stakeholders",
                        content = "Direct collaboration with Head of Engineering, AI Research Director, and Product Strategy Leads.",
                        category = "Important People",
                        isApproved = true,
                        confidence = 0.92f,
                        source = "Conversation Insight"
                    ),
                    MemoryEntity(
                        title = "LIFEOS Core Philosophy",
                        content = "One unified intelligent operating system across personal & professional life, maximum privacy, zero friction.",
                        category = "Ongoing Projects",
                        isApproved = true,
                        confidence = 0.99f,
                        source = "User Confirmed"
                    )
                )
            )

            // Seed Goals
            db.goalDao().insertGoals(
                listOf(
                    GoalEntity(
                        title = "Launch Autonomous AI Executive System",
                        description = "Achieve full orchestration of tasks, schedule optimization, and zero-loss personal memory.",
                        targetDate = "Dec 2026",
                        progressPercent = 75,
                        category = "Career",
                        milestonesRaw = "Complete multi-department router\nIntegrate voice synthesis\nDeploy Room local persistence\nAudit latency benchmarks"
                    ),
                    GoalEntity(
                        title = "Achieve 30-Day Unbroken Habit Streak",
                        description = "Consistent meditation, daily 5km running, and 90min focused reading blocks.",
                        targetDate = "Nov 30",
                        progressPercent = 60,
                        category = "Health",
                        milestonesRaw = "First 10 days complete\nReach 20 consecutive days\nComplete full 30 days challenge"
                    ),
                    GoalEntity(
                        title = "Build $100k Liquid Growth Reserve",
                        description = "Systematic monthly savings and high-yield asset preservation.",
                        targetDate = "Q1 2027",
                        progressPercent = 45,
                        category = "Financial",
                        milestonesRaw = "Set up automated sweep\nOptimize operating expenses\nReview tax efficiency"
                    )
                )
            )

            // Seed Habits
            db.habitDao().insertHabits(
                listOf(
                    HabitEntity(
                        title = "Daily Executive Planning & Review",
                        frequency = "Daily",
                        currentStreak = 14,
                        bestStreak = 28,
                        isCompletedToday = true,
                        category = "Productivity"
                    ),
                    HabitEntity(
                        title = "Cold Plunge & Morning Sunlight",
                        frequency = "Daily",
                        currentStreak = 9,
                        bestStreak = 15,
                        isCompletedToday = false,
                        category = "Health"
                    ),
                    HabitEntity(
                        title = "90-Min Zero-Distraction Deep Block",
                        frequency = "Weekdays",
                        currentStreak = 12,
                        bestStreak = 21,
                        isCompletedToday = true,
                        category = "Focus"
                    ),
                    HabitEntity(
                        title = "Read 20 Pages Non-Fiction",
                        frequency = "Daily",
                        currentStreak = 6,
                        bestStreak = 18,
                        isCompletedToday = false,
                        category = "Mindset"
                    )
                )
            )

            // Seed Finances
            db.financeDao().insertFinances(
                listOf(
                    FinanceEntity(
                        title = "Consulting & Retainer Revenue",
                        amount = 4500.00,
                        type = "Income",
                        category = "Business",
                        date = "Nov 1"
                    ),
                    FinanceEntity(
                        title = "Cloud Infrastructure & GPUs",
                        amount = 320.50,
                        type = "Expense",
                        category = "Business",
                        date = "Nov 2"
                    ),
                    FinanceEntity(
                        title = "Executive Nutrition & Meals",
                        amount = 85.00,
                        type = "Expense",
                        category = "Food",
                        date = "Today"
                    ),
                    FinanceEntity(
                        title = "High-Speed Fiber Connection",
                        amount = 110.00,
                        type = "Expense",
                        category = "Bills",
                        date = "Oct 28"
                    )
                )
            )

            // Seed Knowledge Documents
            db.documentDao().insertDocuments(
                listOf(
                    DocumentEntity(
                        title = "LIFEOS Core Architecture & Principles",
                        category = "Strategy",
                        content = "LIFEOS AI is engineered around an executive team model: Chief of Staff orchestrating specialized agents for productivity, memory, research, and finance. All operations prioritize user sovereignty and low latency.",
                        summary = "System overview of LIFEOS multi-agent personal operating system.",
                        tags = "architecture, vision, ai",
                        dateAdded = "Nov 1"
                    ),
                    DocumentEntity(
                        title = "Executive Decision Framework (First Principles)",
                        category = "Notes",
                        content = "1. Is this reversible? (Type 1 vs Type 2 decisions).\n2. What is the fundamental limiting factor?\n3. How does this compound over a 5-year timeline?\n4. What can be delegated or automated?",
                        summary = "Mental models for high-velocity executive decision making.",
                        tags = "decision-making, mental-models",
                        dateAdded = "Nov 2"
                    )
                )
            )

            // Seed Automations
            db.automationDao().insertAutomations(
                listOf(
                    AutomationEntity(
                        title = "Executive Morning Briefing",
                        description = "Synthesizes today's calendar, priority tasks, and habit status into an AI briefing at 8:00 AM.",
                        trigger = "Daily at 08:00 AM",
                        action = "Generate briefing & update Dashboard card",
                        isEnabled = true,
                        lastRun = "Today, 8:00 AM"
                    ),
                    AutomationEntity(
                        title = "Evening Habit & Task Retrospective",
                        description = "Audits uncompleted tasks, recalculates streaks, and proposes schedule adjustments for tomorrow.",
                        trigger = "Daily at 09:30 PM",
                        action = "Generate day summary & notify",
                        isEnabled = true,
                        lastRun = "Yesterday, 9:30 PM"
                    ),
                    AutomationEntity(
                        title = "Urgent Task Escalation",
                        description = "Flags any overdue task with priority 'Urgent' to top of the dashboard feed.",
                        trigger = "On Task Deadline Past",
                        action = "Elevate priority & trigger warning state",
                        isEnabled = true,
                        lastRun = "Active"
                    )
                )
            )

            // Seed Initial Welcome Chat Message
            db.chatMessageDao().insertMessage(
                ChatMessageEntity(
                    sender = "ai",
                    department = "Chief of Staff",
                    text = "Welcome to LIFEOS AI, your personal executive command center. I have loaded your schedule, active tasks, approved memories, and habit trackers.\n\nYou can speak or type commands like:\n• 'Plan my day'\n• 'Add task: Finalize pitch deck with High priority'\n• 'Remind me tomorrow at 9 AM to review budget'\n• 'Log expense $45 for lunch'\n• 'Remember that I prefer morning workouts'\n\nHow can the Executive Team assist you today?",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
