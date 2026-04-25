package io.github.fardeeldev.autolocale.runtime

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object AutoLocaleManager {

    /**
     * Set the application language at runtime.
     * This uses the modern Android 13+ approach (Per-app language preferences)
     * which works on older versions via AppCompat.
     */
    fun setLanguage(context: Context, languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
        
        // Compose state update karo taake foran UI refresh ho
        AutoLocaleState.currentLocale.value = Locale(languageCode)
    }

    /**
     * Get the current application language code.
     */
    fun getCurrentLanguage(): String {
        return AppCompatDelegate.getApplicationLocales().toLanguageTags()
            .ifEmpty { Locale.getDefault().language }
    }
}
