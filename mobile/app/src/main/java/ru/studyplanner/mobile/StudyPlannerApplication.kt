package ru.studyplanner.mobile

import android.app.Application
import ru.studyplanner.mobile.di.AppContainer

class StudyPlannerApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
