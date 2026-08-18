package com.shubhamthorat.androidtechnicaldeepdive

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * ADVANCED KOTLIN & INTERNALS MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide covers how Kotlin works under the hood on the JVM and advanced language 
 * features that separate mid-level from senior developers.
 */

// =========================================================================================
// PART 1: KOTLIN INTERNALS - INLINE, NOINLINE, CROSSINLINE
// =========================================================================================

/**
 * CONCEPT: Inline Functions
 * When you mark a function as 'inline', the compiler copies the function's bytecode 
 * directly into the call site. This reduces the overhead of function calls and 
 * object creation for lambdas.
 */
inline fun <T> measureTime(block: () -> T): T {
    val start = System.nanoTime()
    val result = block()
    println("Time taken: ${System.nanoTime() - start} ns")
    return result
}

/**
 * CONCEPT: noinline & crossinline
 * - noinline: Used when you have multiple lambdas but don't want one of them to be inlined.
 * - crossinline: Used when a lambda is called from another execution context (like a local object 
 *   or a nested function) and you want to disallow non-local returns.
 */
inline fun complexInline(
    inlined: () -> Unit,
    noinline notInlined: () -> Unit,
    crossinline crossInlined: () -> Unit
) {
    inlined()
    val f = notInlined // Possible because it's not inlined (it's an object)
    
    val runnable = Runnable {
        crossInlined() // Required because it's called inside a nested scope (Runnable)
    }
}

// =========================================================================================
// PART 2: REIFIED TYPE PARAMETERS
// =========================================================================================

/**
 * CONCEPT: Reified Types
 * In Java/Kotlin, type information is "erased" at runtime due to Generics.
 * By using 'inline' + 'reified', the compiler keeps the type information, 
 * allowing you to use 'is T' or 'T::class.java' inside the function.
 */
inline fun <reified T> checkType(value: Any) {
    if (value is T) {
        println("Value is of type ${T::class.java.simpleName}")
    }
}

// =========================================================================================
// PART 3: DELEGATION (THE 'BY' KEYWORD)
// =========================================================================================

/**
 * 1. Property Delegation: Standard delegates like 'lazy' or 'observable'.
 */
class DelegateDemo {
    // lazy: Computed only on first access. Thread-safe by default.
    val lazyValue: String by lazy {
        println("Computing...")
        "Hello"
    }

    // observable: Intercepts changes to a property.
    var watchedValue: String by Delegates.observable("Initial") { prop, old, new ->
        println("${prop.name} changed from $old to $new")
    }
    
    // Custom Delegate
    var customValue: String by UpperCaseDelegate()
}

class UpperCaseDelegate {
    private var value = ""
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        value = newValue.uppercase()
    }
}

/**
 * 2. Class Delegation: Composition over Inheritance.
 */
interface Base { fun printMessage() }
class BaseImpl(val x: Int) : Base {
    override fun printMessage() { println(x) }
}
// Derived delegates all methods of Base to 'b'
class Derived(b: Base) : Base by b 

// =========================================================================================
// PART 4: SCOPE FUNCTIONS (DEFINITIVE GUIDE)
// =========================================================================================

/**
 * SCOPE FUNCTIONS SUMMARY:
 * | Function | Object Reference | Return Value | Use Case |
 * |----------|------------------|--------------|----------|
 * | let      | it               | Lambda Result| Null checks, transformations |
 * | run      | this             | Lambda Result| Init + computing result |
 * | with     | this             | Lambda Result| Grouping method calls |
 * | apply    | this             | Context Object| Object configuration |
 * | also     | it               | Context Object| Side effects (logging) |
 */
fun scopeFunctionDemo() {
    val str: String? = "Hello"
    
    // apply: Configure an object and return it.
    val list = mutableListOf<String>().apply {
        add("A")
        add("B")
    }

    // let: Transform or handle nulls.
    val length = str?.let {
        println("Transforming $it")
        it.length
    }
}

// =========================================================================================
// PART 5: VALUE CLASSES (MEMORY OPTIMIZATION)
// =========================================================================================

/**
 * CONCEPT: Value Classes (formerly inline classes)
 * Wraps a primitive or object without the memory overhead of a wrapper object.
 * At runtime, the JVM uses the underlying type (Int), not the class object.
 */
@JvmInline
value class Password(val value: String)

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: How does 'inline' improve performance?
 * A: It removes the need for a function object (lambda) to be created in memory and 
 *    avoids the virtual call overhead. However, inlining large functions can increase 
 *    the binary size (code bloat).
 *
 * Q: What is a non-local return?
 * A: In a normal lambda, 'return' is not allowed. In an inlined lambda, 'return' 
 *    actually returns from the calling function (non-local). 'crossinline' prevents this.
 *
 * Q: lazy vs lateinit?
 * A: 'lazy' is for val (read-only), computed on demand. 'lateinit' is for var (mutable), 
 *    must be initialized manually before use, and only works for non-primitive types.
 *
 * Q: How does 'reified' work under the hood?
 * A: Because the function is inlined, the compiler knows the exact type at the call site 
 *    and replaces the generic type 'T' with the actual class in the bytecode.
 */
