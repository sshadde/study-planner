package ru.studyplanner.mobile.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.studyplanner.mobile.di.AppContainer
import ru.studyplanner.mobile.state.AssignmentViewModel
import ru.studyplanner.mobile.state.AuthViewModel
import ru.studyplanner.mobile.state.CourseViewModel
import ru.studyplanner.mobile.state.ReminderViewModel
import ru.studyplanner.mobile.ui.screens.AssignmentDetailsScreen
import ru.studyplanner.mobile.ui.screens.AssignmentEditScreen
import ru.studyplanner.mobile.ui.screens.AssignmentListScreen
import ru.studyplanner.mobile.ui.screens.CoursesScreen
import ru.studyplanner.mobile.ui.screens.LoginScreen
import ru.studyplanner.mobile.ui.screens.ProfileScreen

private object Routes {
    const val LOGIN = "login"
    const val LIST = "assignments"
    const val DETAILS = "assignments/{id}"
    const val CREATE = "assignments/create"
    const val EDIT = "assignments/{id}/edit"
    const val PROFILE = "profile"
    const val COURSES = "courses"

    fun details(id: Long) = "assignments/$id"

    fun edit(id: Long) = "assignments/$id/edit"
}

@Composable
fun StudyPlannerApp(container: AppContainer) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(container.authRepository) }
    val assignmentViewModel = remember { AssignmentViewModel(container.assignmentRepository) }
    val courseViewModel = remember { CourseViewModel(container.courseRepository) }
    val reminderViewModel = remember { ReminderViewModel(container.reminderRepository) }
    val authState by authViewModel.state.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in setOf(Routes.LIST, Routes.COURSES, Routes.PROFILE)

    LaunchedEffect(authState.user) {
        if (authState.user != null) {
            courseViewModel.loadCourses()
            assignmentViewModel.loadAssignments()
            navController.navigate(Routes.LIST) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.LIST,
                        onClick = { navController.singleTopNavigate(Routes.LIST) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Задания") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.COURSES,
                        onClick = { navController.singleTopNavigate(Routes.COURSES) },
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                        label = { Text("Дисциплины") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.PROFILE,
                        onClick = { navController.singleTopNavigate(Routes.PROFILE) },
                        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        label = { Text("Профиль") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    state = authState,
                    onLogin = authViewModel::login,
                    onRegister = authViewModel::register
                )
            }
            composable(Routes.LIST) {
                AssignmentListScreen(
                    viewModel = assignmentViewModel,
                    courseViewModel = courseViewModel,
                    onCreate = { navController.navigate(Routes.CREATE) },
                    onOpen = { id -> navController.navigate(Routes.details(id)) }
                )
            }
            composable(
                route = Routes.DETAILS,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = requireNotNull(entry.arguments?.getLong("id"))
                AssignmentDetailsScreen(
                    id = id,
                    viewModel = assignmentViewModel,
                    reminderViewModel = reminderViewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.edit(id)) }
                )
            }
            composable(Routes.CREATE) {
                AssignmentEditScreen(
                    assignmentId = null,
                    assignmentViewModel = assignmentViewModel,
                    courseViewModel = courseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.EDIT,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                AssignmentEditScreen(
                    assignmentId = requireNotNull(entry.arguments?.getLong("id")),
                    assignmentViewModel = assignmentViewModel,
                    courseViewModel = courseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    user = authState.user,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.COURSES) {
                CoursesScreen(viewModel = courseViewModel)
            }
        }
    }
}

private fun androidx.navigation.NavController.singleTopNavigate(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
