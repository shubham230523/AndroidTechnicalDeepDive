package com.shubhamthorat.androidtechnicaldeepdive

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * COMPOSE MULTIPLATFORM MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Compose Multiplatform (CMP) is a declarative UI framework that allows you to share 
 * UI code across Android, iOS, Desktop (JVM), and Web (Wasm/JS).
 *
 * KEY CONCEPTS:
 * 1. Skia / Skiko: The rendering engine used to draw the UI pixel-perfectly on all platforms.
 * 2. commonMain: Now contains both Logic AND UI code.
 * 3. KMP vs CMP: KMP shares business logic; CMP shares UI components.
 */

// =========================================================================================
// PART 1: SHARED UI IN commonMain
// =========================================================================================

/**
 * This Composable lives in commonMain and looks exactly the same on Android and iOS.
 */
@Composable
fun SharedCounterApp() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text(text = "Multiplatform Count: $count")
        Button(onClick = { count++ }) {
            Text("Click Me!")
        }
    }
}

// =========================================================================================
// PART 2: PLATFORM-SPECIFIC UI (expect/actual)
// =========================================================================================

/**
 * Sometimes you need a native UI component that CMP doesn't support yet, 
 * like a Google Map or a platform-specific Camera preview.
 */
@Composable
expect fun PlatformSpecificWidget(modifier: androidx.compose.ui.Modifier)

/*
// androidMain implementation
@Composable
actual fun PlatformSpecificWidget(modifier: Modifier) {
    // Wrap a traditional Android View using AndroidView
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { context -> android.widget.CalendarView(context) },
        modifier = modifier
    )
}

// iosMain implementation
@Composable
actual fun PlatformSpecificWidget(modifier: Modifier) {
    // Wrap a UIKit view using UIKitView
    androidx.compose.ui.interop.UIKitView(
        factory = { platform.UIKit.UIDatePicker() },
        modifier = modifier
    )
}
*/

// =========================================================================================
// PART 3: SHARED RESOURCES
// =========================================================================================

/**
 * CMP uses a specialized library to share resources like images, fonts, and strings.
 * You place assets in 'commonMain/composeResources/'.
 */
/*
@Composable
fun ResourceDemo() {
    // Generated accessors for multiplatform resources
    val image = painterResource(Res.drawable.my_icon)
    val string = stringResource(Res.string.app_name)
    
    Image(painter = image, contentDescription = string)
}
*/

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * Q: How does Compose Multiplatform differ from Flutter?
 * A: Flutter uses Dart and its own rendering engine; CMP uses Kotlin and the Compose compiler. 
 *    CMP has better interoperability with native code (Swift/Kotlin) and allows you to 
 *    mix-and-match: you can share logic only (KMP) or share UI as well (CMP).
 *
 * Q: What is Skia's role in CMP?
 * A: Skia is the 2D graphics library (the same one used in Chrome and Flutter). 
 *    CMP uses it to draw UI components manually, ensuring that a Button on Android 
 *    looks identical to a Button on iOS.
 *
 * Q: What are the performance considerations on iOS?
 * A: Because CMP draws its own UI (unlike React Native which uses native components), 
 *    it needs to handle touch events and animations efficiently. iOS rendering is 
 *    done via Metal (using Skiko), which is generally very fast, but initial 
 *    startup time can be slightly higher than pure native apps.
 *
 * Q: How do you handle Navigation in CMP?
 * A: Standard Jetpack Navigation doesn't work in CMP yet. Developers use multiplatform 
 *    libraries like 'Voyager' or 'Decompose' which are designed to handle 
 *    navigation stacks across Android and iOS lifecycle.
 *
 * Q: Can I use CMP for just parts of my app?
 * A: Yes! This is a major advantage. You can build your core features in CMP 
 *    and keep the complex platform-specific screens (like deep OS integrations) 
 *    in pure native Swift/SwiftUI or Kotlin/Jetpack Compose.
 */
