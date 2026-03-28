package com.example.nudge.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.nudge.ui.viewmodel.NudgeViewModel
import com.example.nudge.data.models.Goal

@Composable
fun SettingsScreen(viewModel: NudgeViewModel) {
    val currentGoal by viewModel.goal.collectAsState(initial = null)
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var currentSaved by remember { mutableStateOf("") }

    LaunchedEffect(currentGoal) {
        currentGoal?.let {
            goalName = it.name
            targetAmount = it.targetAmount.toString()
            currentSaved = it.currentSaved.toString()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Configura tu Meta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = goalName,
            onValueChange = { goalName = it },
            label = { Text("Nombre de la Meta (ej: Viaje a Japón)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = targetAmount,
            onValueChange = { targetAmount = it },
            label = { Text("Monto Objetivo ($)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = currentSaved,
            onValueChange = { currentSaved = it },
            label = { Text("Monto Ahorrado ($)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val target = targetAmount.toDoubleOrNull() ?: 0.0
                val saved = currentSaved.toDoubleOrNull() ?: 0.0
                viewModel.saveGoal(Goal(name = goalName, targetAmount = target, currentSaved = saved))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Meta")
        }
    }
}
