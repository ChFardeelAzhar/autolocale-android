package io.github.fardeeldev.autolocale.runtime

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

// Default locale English hai
val LocalAutoLocale = compositionLocalOf { Locale.ENGLISH }

/**
 * Wraps content with AutoLocale state management.
 * Overrides LocalContext to ensure stringResource() reacts to locale changes instantly.
 */
@Composable
fun AutoLocaleProvider(content: @Composable () -> Unit) {
    val locale = AutoLocaleState.currentLocale.value
    val context = LocalContext.current
    
    // Create an updated context with the new locale configuration
    val updatedContext = remember(locale) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    // key(locale) forces full recomposition of children when locale changes
    key(locale) {
        CompositionLocalProvider(
            LocalAutoLocale provides locale,
            LocalContext provides updatedContext
        ) {
            content()
        }
    }
}
