package ru.studyplanner.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.studyplanner.mobile.ui.StudyPlannerApp
import ru.studyplanner.mobile.ui.theme.StudyPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as StudyPlannerApplication).container
        setContent {
            StudyPlannerTheme {
                StudyPlannerApp(container = container)
            }
        }
    }
}
