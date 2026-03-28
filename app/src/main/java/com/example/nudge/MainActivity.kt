package com.example.nudge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nudge.ui.screens.ChatScreen
import com.example.nudge.ui.screens.DashboardScreen
import com.example.nudge.ui.screens.SettingsScreen
import com.example.nudge.ui.theme.NudgeTheme
import com.example.nudge.ui.viewmodel.NudgeViewModel
import com.example.nudge.ui.viewmodel.NudgeViewModelFactory
import com.example.nudge.data.local.AppDatabase
import com.example.nudge.data.repository.MockFinanceProvider
import com.example.nudge.data.repository.NudgeRepository

class MainActivity : ComponentActivity() {
    private val viewModel: NudgeViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NudgeRepository(database.nudgeDao(), MockFinanceProvider())
        NudgeViewModelFactory(repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NudgeTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route

                val screenTitle = when(currentDestination) {
                    "dashboard" -> "Nudge Dashboard"
                    "chat" -> "Mentor Nudge"
                    "settings" -> "Configuración"
                    else -> "Nudge"
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(screenTitle) },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                label = { Text("Dashboard") },
                                selected = currentDestination == "dashboard",
                                onClick = { navController.navigate("dashboard") }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Email, contentDescription = "Chat") },
                                label = { Text("Mentor") },
                                selected = currentDestination == "chat",
                                onClick = { navController.navigate("chat") }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Configuración") },
                                selected = currentDestination == "settings",
                                onClick = { navController.navigate("settings") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") { DashboardScreen(viewModel) }
                        composable("chat") { ChatScreen(viewModel) }
                        composable("settings") { SettingsScreen(viewModel) }
                    }
                }
            }
        }
    }
}
