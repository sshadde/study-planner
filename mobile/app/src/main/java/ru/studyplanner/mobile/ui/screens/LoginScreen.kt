package ru.studyplanner.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ru.studyplanner.mobile.state.AuthUiState

@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String) -> Unit
) {
    var registerMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("student@example.com") }
    var password by remember { mutableStateOf("password123") }
    var fullName by remember { mutableStateOf("Иван Иванов") }
    var groupName by remember { mutableStateOf("ПИ-3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Учебный планировщик", style = MaterialTheme.typography.headlineLarge)
        Text("Задания, дисциплины и дедлайны студента", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Email") }
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Пароль") }
        )
        if (registerMode) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("ФИО") }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Группа") }
            )
        }
        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (registerMode) onRegister(email, password, fullName, groupName) else onLogin(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            if (state.loading) CircularProgressIndicator() else Text(if (registerMode) "Создать аккаунт" else "Войти")
        }
        TextButton(onClick = { registerMode = !registerMode }) {
            Text(if (registerMode) "У меня уже есть аккаунт" else "Зарегистрироваться")
        }
    }
}
