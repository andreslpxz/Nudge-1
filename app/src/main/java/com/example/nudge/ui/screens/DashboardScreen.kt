package com.example.nudge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.nudge.ui.theme.LightGreyBorder
import com.example.nudge.ui.viewmodel.NudgeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: NudgeViewModel) {
    val goal by viewModel.goal.collectAsState(initial = null)
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Tu Meta de Ahorro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        goal?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, LightGreyBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(it.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (it.currentSaved / it.targetAmount).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "${formatCurrency(it.currentSaved)} de ${formatCurrency(it.targetAmount)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } ?: run {
            Text("No hay meta configurada. Ve a Ajustes.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Transacciones Recientes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions.reversed()) { tx ->
                TransactionCard(tx.merchant, tx.category, tx.amount)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.simulateTransaction() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                "¿Vale la pena?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionCard(merchant: String, category: String, amount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, LightGreyBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(merchant, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(category, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }
            Text(
                "-${formatCurrency(amount)}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935),
                fontSize = 16.sp
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    return String.format(Locale.US, "$%.2f", amount)
}
