package com.shubhamthorat.androidtechnicaldeepdive

/**
 * ANDROID AGENT DEVELOPMENT KIT (AADK / ADK) MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * The Agent Development Kit (ADK) for Android is a framework for building AI-powered 
 * agents that can perform actions, not just chat.
 *
 * KEY CONCEPTS:
 * 1. AI Agent: A system that can reason, plan, and use "tools" to achieve a goal.
 * 2. Orchestrator: The "brain" that manages the plan and tool execution.
 * 3. Tools: Functions or APIs exposed to the agent via metadata (docstrings + annotations).
 * 4. Hybrid Orchestration: Balancing Cloud models (Vertex AI) and On-device models (Gemini Nano).
 */

// =========================================================================================
// PART 1: DEFINING TOOLS (@Tool Annotation)
// =========================================================================================

/**
 * A Tool is a standard Kotlin function that the Agent can call.
 * The Agent uses the function name and documentation to understand WHEN to call it.
 */
class CalendarTools {

    /**
     * @Tool annotation (Conceptual - as per ADK SDK standards)
     * 
     * INTERVIEW TIP: The documentation string is CRITICAL. The LLM uses this description 
     * to decide if this tool helps solve the user's request.
     */
    // @Tool
    fun getEvents(date: String): List<String> {
        /**
         * Documentation: "Retrieves a list of calendar events for a given date in YYYY-MM-DD format."
         */
        return listOf("Meeting at 10 AM", "Lunch with Team at 1 PM")
    }

    // @Tool
    fun addEvent(title: String, startTime: String): Boolean {
        /**
         * Documentation: "Adds a new event to the user's calendar."
         */
        println("Event Added: $title at $startTime")
        return true
    }
}

// =========================================================================================
// PART 2: ORCHESTRATION & THE AGENT LOOP
// =========================================================================================

/**
 * The Orchestrator follows a specific loop:
 * 1. INPUT: User asks "Book a lunch meeting for tomorrow."
 * 2. PLAN: Agent decides it needs to:
 *    a) Get events for tomorrow.
 *    b) Find a free slot.
 *    c) Add a new event.
 * 3. EXECUTE: Calls the 'getEvents' tool.
 * 4. OBSERVE: Result shows "Lunch at 1 PM" is free.
 * 5. RE-PLAN: Agent calls 'addEvent'.
 */

class AgentOrchestrator(
    private val calendarTools: CalendarTools
) {
    // Conceptual loop management
    suspend fun runAgentLoop(userInput: String) {
        // 1. Send input to LLM (Gemini)
        // 2. Parse LLM response for "Tool Calls"
        // 3. Invoke calendarTools.getEvents(...)
        // 4. Send tool result back to LLM
        // 5. Finalize response to user
    }
}

// =========================================================================================
// PART 3: HYBRID ORCHESTRATION (CLOUDS + ON-DEVICE)
// =========================================================================================

/**
 * INTERVIEW TOPIC: Why use Hybrid Orchestration?
 * 1. Privacy: Run sensitive tasks (reading private messages) on-device via Gemini Nano.
 * 2. Latency: Local models respond faster for simple tasks.
 * 3. Reasoning: Use Cloud models for complex planning that requires large context.
 */
object HybridStrategy {
    const val ON_DEVICE_MODEL = "Gemini Nano" // via ML Kit
    const val CLOUD_MODEL = "Gemini 1.5 Pro"   // via Vertex AI / Firebase
}

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: Difference between ADK (AADK) and AGDK?
 * A: ADK (AADK) is for AI Agent development. AGDK (Android Game Development Kit) 
 *    is for high-performance games (Oboe, Frame Pacing, GameActivity).
 *
 * Q: What makes an "Agent" different from a Chatbot?
 * A: An Agent has AGENCY—the ability to interact with the world via Tools. 
 *    A chatbot just generates text; an Agent executes code to solve problems.
 *
 * Q: How do you handle "Hallucinations" in Tool calls?
 * A: 1. Strict Type Checking: Ensure the agent provides valid arguments.
 *    2. Error Handling: Return descriptive errors (e.g., "Date format invalid") 
 *       so the agent can correct itself and retry.
 *    3. User Confirmation: For destructive actions (deleting data), always 
 *       require a manual "Confirm" step in the UI.
 *
 * Q: What are the security risks of AADK?
 * A: "Prompt Injection" or "Agentic Overreach" where the agent might call 
 *    tools it shouldn't. Fix: Implement a robust permission system and 
 *    human-in-the-loop validation for sensitive tool calls.
 *
 * Q: How does on-device Gemini Nano help developers?
 * A: It allows building agents that work OFFLINE and process PRIVATE data 
 *    without it ever leaving the device, which is a massive selling point 
 *    for privacy-conscious apps.
 */
