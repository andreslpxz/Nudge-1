package com.example.nudge.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nudge.ui.viewmodel.NudgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: NudgeViewModel) {
    val goal by viewModel.goal.collectAsState(initial = null)
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tu Meta de Ahorro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        goal?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(it.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (it.currentSaved / it.targetAmount).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$${it.currentSaved} de $${it.targetAmount}")
                }
            }
        } ?: run {
            Text("No hay meta configurada. Ve a Ajustes.")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Transacciones Recientes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(transactions.reversed()) { tx ->
                ListItem(
                    headlineContent = { Text(tx.merchant) },
                    supportingContent = { Text(tx.category) },
                    trailingContent = { Text("-$${tx.amount}", color = Color.Red) }
                )
                HorizontalDivider()
            }
        }

        Button(
            onClick = { viewModel.simulateTransaction() },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("Simular Gasto")
        }
    }
}
