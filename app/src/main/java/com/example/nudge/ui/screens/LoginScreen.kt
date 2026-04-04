package com.example.nudge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nudge.ui.viewmodel.AuthViewModel
import com.example.nudge.ui.viewmodel.AuthState
import com.example.nudge.ui.theme.GlassyBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otpToken by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var loginType by remember { mutableStateOf(LoginType.EMAIL) }
    var isSignUp by remember { mutableStateOf(false) }
    var isPhoneOtpSent by remember { mutableStateOf(false) }

    val state by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassyBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nudge",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu mentor financiero inteligente",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (loginType == LoginType.EMAIL) {
            if (isSignUp) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = textFieldColors()
            )
        } else {
            // Phone Login
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono (con código de país)", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = textFieldColors(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            if (isPhoneOtpSent) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = otpToken,
                    onValueChange = { otpToken = it },
                    label = { Text("Código OTP", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = textFieldColors(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (loginType) {
                    LoginType.EMAIL -> {
                        if (isSignUp) {
                            viewModel.signUpWithEmail(email, password, username)
                        } else {
                            viewModel.signInWithEmail(email, password)
                        }
                    }
                    LoginType.PHONE -> {
                        if (!isPhoneOtpSent) {
                            viewModel.signInWithPhone(phone)
                            isPhoneOtpSent = true
                        } else {
                            viewModel.verifyPhoneOtp(phone, otpToken)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            if (state is AuthState.Loading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
            } else {
                val label = when {
                    loginType == LoginType.PHONE && !isPhoneOtpSent -> "Enviar Código"
                    loginType == LoginType.PHONE && isPhoneOtpSent -> "Verificar"
                    isSignUp -> "Registrarse"
                    else -> "Iniciar Sesión"
                }
                Text(label, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.signInWithGoogle() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Text("Continuar con Google", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            TextButton(onClick = {
                loginType = if (loginType == LoginType.EMAIL) LoginType.PHONE else LoginType.EMAIL
                isPhoneOtpSent = false
            }) {
                Text(
                    if (loginType == LoginType.EMAIL) "Usar Teléfono" else "Usar Email",
                    color = Color.LightGray
                )
            }

            if (loginType == LoginType.EMAIL) {
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(onClick = { isSignUp = !isSignUp }) {
                    Text(
                        if (isSignUp) "¿Ya tienes cuenta?" else "¿No tienes cuenta?",
                        color = Color.LightGray
                    )
                }
            }
        }

        if (state is AuthState.Error) {
            Text(
                text = (state as AuthState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

enum class LoginType { EMAIL, PHONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.Gray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.Gray
)
