package com.example.a210122_nazatul_lab1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a210122_nazatul_lab1.ui.theme.A210122_NAZATUL_Lab1Theme

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var usernameInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    fun attemptLogin() {///
        if (usernameInput.trim().isNotBlank()) {
            showError = false
            onLoginSuccess(usernameInput.trim())
        } else {
            showError = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "App Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "SmartTenaga",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Your energy, your control.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(40.dp))
            OutlinedTextField(
                value = usernameInput,
                onValueChange = {
                    usernameInput = it
                    if (showError) showError = false
                },
                label = { Text("Username") },
                placeholder = {  },
                singleLine = true,
                isError = showError,
                supportingText = {
                    if (showError) Text("Please enter a username.")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { attemptLogin() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { attemptLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    A210122_NAZATUL_Lab1Theme {
        LoginScreen(onLoginSuccess = {})
    }
}