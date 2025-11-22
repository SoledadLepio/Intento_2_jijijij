package com.example.intento2jijijij

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.intento2jijijij.ui.theme.* // Importamos tus colores

@Composable
fun PantallaLogin(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Lógica directa aquí

    // Usamos Surface para el fondo oscuro
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite // Texto blanco
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Campo de Correo (Estilo Oscuro)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedTextColor = TextWhite, // Letra blanca al escribir
                    unfocusedTextColor = TextWhite,
                    focusedLabelColor = OrangePrimary,
                    unfocusedLabelColor = TextGray,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = TextGray,
                    cursorColor = OrangePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña (Estilo Oscuro)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedTextColor = TextWhite, // Letra blanca al escribir
                    unfocusedTextColor = TextWhite,
                    focusedLabelColor = OrangePrimary,
                    unfocusedLabelColor = TextGray,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = TextGray,
                    cursorColor = OrangePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = DangerRed, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = OrangePrimary)
            } else {
                // Botón Naranja con lógica de Firebase integrada
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            // 1. Intentar Iniciar Sesión
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        isLoading = false
                                        navController.navigate("inicio") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        // 2. Si falla, intentar Registrar
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { taskRegistro ->
                                                isLoading = false
                                                if (taskRegistro.isSuccessful) {
                                                    Toast.makeText(context, "Usuario creado. Ingresando...", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("inicio") {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                } else {
                                                    errorMessage = "Error: ${task.exception?.message}"
                                                }
                                            }
                                    }
                                }
                        } else {
                            errorMessage = "Ingresa correo y contraseña"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Ingresar / Registrarse", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}