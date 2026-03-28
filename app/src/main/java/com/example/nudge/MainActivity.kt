package com.example.nudge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.nudge.ui.theme.GlassyBlack

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
                    "dashboard" -> "Dashboard"
                    "chat" -> "Mentor Nudge"
                    "settings" -> "Ajustes"
                    else -> "Nudge"
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    screenTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = GlassyBlack,
                                titleContentColor = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 0.dp)
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                label = { Text("Resumen") },
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
                                label = { Text("Ajustes") },
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
