package com.labajada.app.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String) -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val isEmailValid = remember(uiState.email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email.trim()).matches()
    }
    val isPasswordValid = remember(uiState.password) {
        uiState.password.isNotBlank()
    }

    //  controla si la contraseña se ve en texto plano o oculta
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Bloque Superior: Identidad de Marca "La Bajada"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "La Bajada",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFD32F2F),
                letterSpacing = (-1).sp
            )
            Text(
                text = "¡Qué bueno verte de nuevo!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF757575),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Bloque Central: Formulario de Login
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ingresa tus credenciales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            // Input de Correo conectado al ViewModel
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico (Gmail)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF757575)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.email.isNotEmpty() && !isEmailValid,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD32F2F),
                    focusedLabelColor = Color(0xFFD32F2F)
                )
            )
            if (uiState.email.isNotEmpty() && !isEmailValid) {
                Text(
                    text = "Ingresa un formato de correo válido",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Input de Contraseña conectado al ViewModel
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF757575)) },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val descripcion = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = descripcion, tint = Color(0xFF757575))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !uiState.isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // ◄ CAMBIO
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD32F2F),
                    focusedLabelColor = Color(0xFFD32F2F)
                )
            )

            // ◄ COMENTADO TEMPORALMENTE: "¿Olvidaste tu contraseña?" — descomentar cuando esté lista la función
            /*
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF757575),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !uiState.isLoading) { /* TODO */ }
            )
            */

            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { viewModel.login(onLoginSuccess) },
                enabled = isEmailValid && isPasswordValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),
                    disabledContainerColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = "Regístrate aquí",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) { onNavigateToOnboarding() }
                )
            }
        }
    }
}