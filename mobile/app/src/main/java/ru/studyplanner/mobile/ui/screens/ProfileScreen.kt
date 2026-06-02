package ru.studyplanner.mobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.studyplanner.mobile.remote.CurrentUserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: CurrentUserDto?,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Профиль") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(user?.fullName ?: "Студент", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Email: ${user?.email ?: "-"}")
            Text("Группа: ${user?.groupName ?: "-"}")
            Spacer(Modifier.height(24.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Выйти")
            }
        }
    }
}
