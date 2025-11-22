package com.example.intento2jijijij

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { PantallaInicio(navController) }
        composable("umbrales") { PantallaUmbrales() }
        composable("volumen") { PantallaVolumen() }
        composable("sonido") { PantallaSonido() }
    }
}