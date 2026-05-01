package io.github.fardeeldev.autolocale

import android.annotation.SuppressLint
import androidx.compose.ui.res.stringResource
 
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.fardeeldev.autolocale.ui.theme.AutoLocaleTheme
import io.github.fardeeldev.autolocale.runtime.AutoLocaleManager
import io.github.fardeeldev.autolocale.runtime.AutoLocaleProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
 
class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoLocaleProvider {
                AutoLocaleTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) {
                        TestScreen()
                    }
                }
            }
        }
    }
}
 
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    androidx.compose.foundation.layout.Column(modifier = modifier.padding(16.dp)) {
        Text(stringResource(R.string.al_welcome_to_autolocale))
        Text(
            text = stringResource(R.string.al_this_is_a_multiline_hardcoded_string).trimIndent()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(stringResource(R.string.al_switch_language))
        Row {
            Button(onClick = { AutoLocaleManager.setLanguage(context, "en") }) {
                Text(stringResource(R.string.al_english))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { AutoLocaleManager.setLanguage(context, "ar") }) {
                Text(stringResource(R.string.al_arabic))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { AutoLocaleManager.setLanguage(context, "fr") }) {
                Text(stringResource(R.string.al_french))
            }
        }
        Text(stringResource(R.string.al_wellcome_to_our_app))
        Text(stringResource(R.string.al_please_login_to_continue))
        Text(stringResource(R.string.al_forget_your_password))
        Text(stringResource(R.string.al_don_t_have_account_sign_up))

        Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.material3.Button(onClick = {}) {
            Text(stringResource(R.string.al_settings))
        }
        androidx.compose.material3.Button(onClick = {}) {
            Text(stringResource(R.string.al_dark_mode))
        }
        androidx.compose.material3.Button(onClick = {}) {
            Text(stringResource(R.string.al_logout))
        }
    }
}
 
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoLocaleTheme {
        Greeting("Android")
    }
}