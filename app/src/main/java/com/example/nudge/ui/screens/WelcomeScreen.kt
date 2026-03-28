package com.example.nudge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nudge.ui.theme.GlassyBlack

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    val plans = listOf(
        PlanInfo(
            "Free",
            "El Observador",
            "$0/mes",
            listOf("1 cuenta bancaria", "1 meta activa", "4 Nudges al día", "30 días historial")
        ),
        PlanInfo(
            "Pro",
            "El Viajero",
            "$9.99/mes",
            listOf("Cuentas ilimitadas", "Análisis Predictivo IA", "Nudge Agresivo", "Multimetas")
        ),
        PlanInfo(
            "Familiar",
            "Dúo Dinámico",
            "$14.99/mes",
            listOf("Bóveda compartida", "Mentoría cruzada", "Progreso común", "Diplomacia IA")
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassyBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            "¡Bienvenido a Nudge!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Elige tu camino hacia la libertad financiera",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().height(350.dp)
        ) {
            items(plans) { plan ->
                PlanCard(plan)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Continuar", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class PlanInfo(val type: String, val title: String, val price: String, val features: List<String>)

@Composable
fun PlanCard(plan: PlanInfo) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                plan.type.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Text(
                plan.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                plan.price,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            plan.features.forEach { feature ->
                Text(
                    "• $feature",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
