package com.labajada.app.presentation.buyer.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.core.validation.PasswordRuleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerRegisterScreen(
    viewModel: BuyerRegisterViewModel,
    onRegistrationComplete: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val isEmailValid = remember(uiState.email) {
        uiState.email.isEmpty() || PasswordValidator.isValidEmail(uiState.email)
    }
    val isPhoneValid = remember(uiState.phone) {
        uiState.phone.isEmpty() || PeruValidators.isValidPhone(uiState.phone)
    }
    val passwordCheck = remember(uiState.password) {
        PasswordValidator.validate(uiState.password)
    }
    val showPasswordChecklist = uiState.password.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "¡Estás a un paso!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Queremos conocerte para mostrarte un huarique al toque.",
                fontSize = 15.sp,
                color = Color(0xFF757575)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Tu Nombre o Apodo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F))
            )

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = { if (it.length <= 9 && it.all(Char::isDigit)) viewModel.onPhoneChange(it) },
                label = { Text("Número de Celular") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = !isPhoneValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F))
            )
            if (!isPhoneValid) {
                Text(
                    text = "Celular inválido (9 dígitos, empieza con 9)",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico (Gmail)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = !isEmailValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F))
            )
            if (!isEmailValid) {
                Text(
                    text = "Ingresa un formato de correo válido",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = showPasswordChecklist && !passwordCheck.isValid,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD32F2F),
                    focusedLabelColor = Color(0xFFD32F2F)
                )
            )
            if (showPasswordChecklist) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    PasswordRuleRow("Mínimo 8 caracteres", passwordCheck.hasMinLength)
                    PasswordRuleRow("Una letra mayúscula", passwordCheck.hasUppercase)
                }
            }
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.registerBuyer(onRegistrationComplete) },
            enabled = uiState.name.isNotBlank() &&
                    isPhoneValid && uiState.phone.isNotBlank() &&
                    isEmailValid && uiState.email.isNotBlank() &&
                    passwordCheck.isValid && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(bottom = 16.dp),
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
                    text = "Finalizar y Buscar Comida",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}