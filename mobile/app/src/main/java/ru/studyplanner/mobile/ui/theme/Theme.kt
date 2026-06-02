package ru.studyplanner.mobile.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StudyPlannerColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF1C6B5A),
    onPrimary = Color.White,
    secondary = Color(0xFF5E6C2F),
    tertiary = Color(0xFF9B4D3D),
    background = Color(0xFFF7FAF8),
    surface = Color.White,
    surfaceVariant = Color(0xFFE6EEE9),
    onSurface = Color(0xFF18201D)
)

@Composable
fun StudyPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StudyPlannerColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
