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
import com.example.nudge.ui.screens.*
import com.example.nudge.ui.theme.NudgeTheme
import com.example.nudge.ui.viewmodel.NudgeViewModel
import com.example.nudge.ui.viewmodel.AuthViewModel
import com.example.nudge.ui.viewmodel.NudgeViewModelFactory
import com.example.nudge.data.local.AppDatabase
import com.example.nudge.data.repository.MockFinanceProvider
import com.example.nudge.data.repository.NudgeRepository
import com.example.nudge.data.repository.AuthRepository
import com.example.nudge.ui.theme.GlassyBlack
import com.example.nudge.ui.viewmodel.AuthState

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        val application = application as NudgeApplication
        val authRepository = AuthRepository(application.supabaseClient, applicationContext)
        NudgeViewModelFactory(authRepository = authRepository)
    }

    private val viewModel: NudgeViewModel by viewModels {
        val application = application as NudgeApplication
        val database = AppDatabase.getDatabase(applicationContext)
        val authRepository = AuthRepository(application.supabaseClient, applicationContext)
        val repository = NudgeRepository(database.nudgeDao(), MockFinanceProvider(), authRepository)
        NudgeViewModelFactory(repository = repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by authViewModel.isDarkMode.collectAsState()

            NudgeTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route
                val authState by authViewModel.authState.collectAsState()

                LaunchedEffect(authState) {
                    when(authState) {
                        is AuthState.Authenticated -> {
                            if (currentDestination == "login" || currentDestination == null) {
                                navController.navigate("welcome") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                }

                val screenTitle = when(currentDestination) {
                    "dashboard" -> "Dashboard"
                    "chat" -> "Mentor Nudge"
                    "settings" -> "Ajustes"
                    "welcome" -> "Planes"
                    else -> "Nudge"
                }

                val showBars = currentDestination in listOf("dashboard", "chat", "settings")

                Scaffold(
                    topBar = {
                        if (showBars) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        screenTitle,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { navController.navigate("settings") }) {
                                        Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = if(isDarkMode) Color.White else Color.Black)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = if(isDarkMode) GlassyBlack else MaterialTheme.colorScheme.surface,
                                    titleContentColor = if(isDarkMode) Color.White else Color.Black
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (showBars) {
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
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (authState is AuthState.Authenticated) "dashboard" else "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") { LoginScreen(authViewModel) }
                        composable("welcome") {
                            WelcomeScreen(onContinue = {
                                navController.navigate("dashboard") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            })
                        }
                        composable("dashboard") { DashboardScreen(viewModel) }
                        composable("chat") { ChatScreen(viewModel) }
                        composable("settings") { SettingsScreen(authViewModel) }
                    }
                }
            }
        }
    }
}
