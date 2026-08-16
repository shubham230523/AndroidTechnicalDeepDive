package com.shubhamthorat.androidtechnicaldeepdive

/**
 * ANDROID GRADLE & BUILD SYSTEM MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * This guide is written in Kotlin syntax (KTS) to help you master the modern Android build system.
 * Gradle is an open-source build automation tool that is highly customizable.
 */

// =========================================================================================
// PART 1: THE BUILD LIFECYCLE (Crucial Interview Question!)
// =========================================================================================

/**
 * Gradle works in 3 distinct phases:
 * 1. INITIALIZATION: Gradle determines which projects/modules will take part in the build.
 *    (It looks at settings.gradle.kts)
 * 2. CONFIGURATION: Gradle evaluates the build scripts of all projects and builds a "Task Graph".
 *    (This is where your code in the 'android' block runs!)
 * 3. EXECUTION: Gradle runs the specific tasks you requested (e.g., assembleDebug).
 */

// =========================================================================================
// PART 2: DEPENDENCY CONFIGURATIONS (implementation vs api vs kapt)
// =========================================================================================

/**
 * implementation: Dependencies are private to the module. Fast builds (less recompilation).
 * api: Dependencies are exposed to consumers of this module. Slow builds (leaks types).
 * kapt / ksp: Used for annotation processing (e.g., Room, Hilt).
 * testImplementation: Used only for unit tests in src/test.
 * androidTestImplementation: Used for instrumented tests in src/androidTest.
 */

// =========================================================================================
// PART 3: BUILD VARIANTS (Build Types + Product Flavors)
// =========================================================================================

/**
 * A Build Variant = One Build Type + One Product Flavor.
 * Example: 'freeDebug', 'paidRelease', etc.
 */
/*
android {
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false // No R8/Proguard in debug for faster builds
            applicationIdSuffix = ".debug" // Install debug and release apps side-by-side
        }
        getByName("release") {
            isMinifyEnabled = true // Enable R8 for code shrinking and obfuscation
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationId = "com.app.free"
            versionNameSuffix = "-free"
        }
        create("paid") {
            dimension = "version"
            applicationId = "com.app.paid"
            versionNameSuffix = "-paid"
        }
    }
}
*/

// =========================================================================================
// PART 4: VERSION CATALOGS (Modern way to manage dependencies)
// =========================================================================================

/**
 * Instead of hardcoding versions in build.gradle, we use 'libs.versions.toml'.
 * Why? Centralized control and type-safe access in KTS.
 */
/*
// libs.versions.toml example:
[versions]
retrofit = "2.9.0"

[libraries]
retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }

// build.gradle.kts usage:
dependencies {
    implementation(libs.retrofit.core)
}
*/

// =========================================================================================
// PART 5: CUSTOM GRADLE TASKS (Advanced)
// =========================================================================================

/**
 * You can write custom tasks to automate anything (uploading APKs, cleaning files).
 */
/*
tasks.register("myCustomTask") {
    group = "Custom"
    description = "A simple custom task for the interview"
    doLast {
        println("Custom Gradle Task Executed Successfully!")
    }
}
*/

// =========================================================================================
// INTERVIEW TIPS:
// 1. What is R8? It's the default compiler that replaces ProGuard for shrinking and obfuscation.
// 2. Groovy vs KTS? KTS (Kotlin Script) provides better IDE support, auto-completion, and error checking.
// 3. What is 'gradlew'? The Gradle Wrapper. It ensures everyone uses the same Gradle version.
// 4. Incremental Builds: Gradle only runs tasks whose inputs have changed, saving massive time.
// 5. Build Cache: Gradle stores outputs of previous builds to reuse them locally or across teams.
// =========================================================================================
