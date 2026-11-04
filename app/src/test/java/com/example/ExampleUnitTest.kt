package com.example

import com.example.data.model.TaskEntity
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun taskEntity_priorityAndStatus_defaultCorrect() {
        val task = TaskEntity(
            title = "Executive Strategic Review",
            priority = "Urgent",
            status = "Todo",
            category = "Executive"
        )
        assertEquals("Executive Strategic Review", task.title)
        assertEquals("Urgent", task.priority)
        assertEquals("Todo", task.status)
        assertEquals("Executive", task.category)
    }

    @Test
    fun taskCompletion_togglesStatus() {
        val task = TaskEntity(title = "Launch LIFEOS AI", status = "Todo")
        val completedTask = task.copy(status = "Completed")
        assertEquals("Completed", completedTask.status)
    }
}
