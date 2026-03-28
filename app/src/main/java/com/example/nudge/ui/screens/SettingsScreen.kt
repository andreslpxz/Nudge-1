package com.example.nudge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.nudge.ui.viewmodel.NudgeViewModel
import com.example.nudge.data.models.Goal
import com.example.nudge.ui.theme.LightGreyBorder

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

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Tu Destino de Ahorro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, LightGreyBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = goalName,
                    onValueChange = { goalName = it },
                    label = { Text("Nombre de la Meta (ej: Viaje a Japón)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("Monto Objetivo ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = currentSaved,
                    onValueChange = { currentSaved = it },
                    label = { Text("Monto Ahorrado ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val target = targetAmount.toDoubleOrNull() ?: 0.0
                val saved = currentSaved.toDoubleOrNull() ?: 0.0
                viewModel.saveGoal(Goal(name = goalName, targetAmount = target, currentSaved = saved))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
        }
    }
}
