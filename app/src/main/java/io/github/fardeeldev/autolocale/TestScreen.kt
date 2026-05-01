package io.github.fardeeldev.autolocale

import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TestScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Fardeel Azhar")
        Text("Kotlin Developer")
        Text("Compose Developer")
        Text("Software Engineer")
        Text("Android Engineer")
        Text("Auto Locale Second Library For native android")
    }
}