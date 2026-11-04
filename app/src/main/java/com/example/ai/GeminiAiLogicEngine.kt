package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAiLogic"

class GeminiAiLogicEngine {

    // General tasks (Default): gemini-3.8-flash
    private val generalModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are LIFEOS AI, the user's personal AI executive operating system.
                    You act as their AI Executive Team across 7 departments:
                    1. Chief of Staff: Strategic daily planning and executive summaries.
                    2. Productivity Manager: Task triage, deadlines, and execution velocity.
                    3. Research Assistant: Knowledge Vault synthesis, notes, and strategic frameworks.
                    4. Finance Assistant: Budget tracking and expense management.
                    5. AI Goal Coach: Milestone acceleration and habit streak cultivation.
                    6. Memory Manager: User preferences, context recall, and privacy safeguards.
                    7. Communication Assistant: High-impact executive drafts and diplomatic messages.
                    
                    Tone: Ultra-sharp, sophisticated, concise, highly structured, proactive.
                    Use bullet points, bold key terms, and always provide actionable next steps.
                    """.trimIndent()
                )
            }
        )
    }

    // Fast tasks: gemini-3.5-flash-lite
    private val fastModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(modelName = "gemini-3.5-flash-lite")
    }

    // Complex reasoning tasks: gemini-4-argon
    private val complexModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(modelName = "gemini-4-argon")
    }

    suspend fun generateExecutiveResponse(
        prompt: String,
        department: String,
        isComplex: Boolean = false,
        isFast: Boolean = false
    ): String {
        return try {
            val model = when {
                isComplex -> complexModel
                isFast -> fastModel
                else -> generalModel
            }

            withTimeout(45_000L) {
                val enhancedPrompt = "[$department perspective] $prompt"
                val response = model.generateContent(enhancedPrompt)
                response.text ?: "Executive core processed your request."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini AI Logic inference error: ${e.message}", e)
            throw e
        }
    }

    fun streamExecutiveResponse(prompt: String, department: String): Flow<String> = flow {
        try {
            val enhancedPrompt = "[$department perspective] $prompt"
            generalModel.generateContentStream(enhancedPrompt).collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Streaming error: ${e.message}", e)
            throw e
        }
    }
}
