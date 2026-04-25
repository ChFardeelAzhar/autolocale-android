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
        Text(stringResource(R.string.al_welcome_to_our_app))
        Text(stringResource(R.string.al_please_login_to_continue))
        Text(stringResource(R.string.al_forgot_your_password))
        Text(stringResource(R.string.al_don_t_have_an_account_sign_up))
        Text(text = stringResource(R.string.al_settings))
        Text(text = stringResource(R.string.al_dark_mode))
        Text(text = stringResource(R.string.al_logout))
    }
}