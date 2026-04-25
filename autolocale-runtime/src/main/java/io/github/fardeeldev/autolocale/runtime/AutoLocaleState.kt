package io.github.fardeeldev.autolocale.runtime

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

internal object AutoLocaleState {
    // Current system locale se initialize karo
    val currentLocale: MutableState<Locale> = mutableStateOf(Locale.getDefault())
}
