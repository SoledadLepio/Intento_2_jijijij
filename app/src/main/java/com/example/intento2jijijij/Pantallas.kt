package com.example.intento2jijijij

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.intento2jijijij.cruds.LeerFirebase
import com.example.intento2jijijij.cruds.escribirFirebase
import com.example.intento2jijijij.model.AppConfig
import com.example.intento2jijijij.model.SensorData


@Composable
fun PantallaInicio(navController: NavController) {

    val (sensorData, isLoading, error) = LeerFirebase("sensor_data", SensorData::class.java)


    val ppmGas = sensorData?.raw_value ?: 0


    val estadoTexto = when {
        ppmGas > 75 -> "PELIGRO"
        ppmGas > 25 -> "ALERTA"
        else -> "Nivel Normal"
    }

    val colorEstado = when {
        ppmGas > 75 -> Color.Red
        ppmGas > 25 -> Color(0xFFFFC107) // Ambar
        else -> Color(0xFF4CAF50) // Verde
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Monitoreo de Gas", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(30.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "$ppmGas PPM",
                    fontSize = 60.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = colorEstado
                )

                Text(
                    text = estadoTexto,
                    fontSize = 28.sp,
                    color = colorEstado
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Dispositivo: ${sensorData?.device ?: "Desconocido"}", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text("Configuración", fontSize = 18.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            BotonMenu("Configurar Umbrales") { navController.navigate("umbrales") }
            BotonMenu("Ajustar Volumen") { navController.navigate("volumen") }
            BotonMenu("Elegir Sonido") { navController.navigate("sonido") }
        }
    }
}

@Composable
fun BotonMenu(texto: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)) {
        Text(texto)
    }
}


@Composable
fun PantallaUmbrales() {

    val (configActual, _, _) = LeerFirebase("settings", AppConfig::class.java)


    var minVal by rememberSaveable { mutableStateOf(25f) }
    var maxVal by rememberSaveable { mutableStateOf(75f) }


    LaunchedEffect(configActual) {
        configActual?.let {
            minVal = it.minUmbral.toFloat()
            maxVal = it.maxUmbral.toFloat()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nivel de Alerta (Min): ${minVal.toInt()}")
        Slider(value = minVal, onValueChange = { minVal = it }, valueRange = 0f..100f)

        Text("Nivel de Peligro (Max): ${maxVal.toInt()}")
        Slider(value = maxVal, onValueChange = { maxVal = it }, valueRange = 0f..100f)

        Button(onClick = {

            val nuevaConfig = configActual?.copy(
                minUmbral = minVal.toInt(),
                maxUmbral = maxVal.toInt()
            ) ?: AppConfig(minVal.toInt(), maxVal.toInt()) // Si es nulo, crea uno nuevo

            escribirFirebase("settings", nuevaConfig)
        }) {
            Text("Guardar Umbrales")
        }
    }
}


@Composable
fun PantallaVolumen() {
    val (configActual, _, _) = LeerFirebase("settings", AppConfig::class.java)
    var volumen by rememberSaveable { mutableStateOf(50f) }

    LaunchedEffect(configActual) {
        configActual?.let { volumen = it.volumen }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Volumen del Buzzer: ${volumen.toInt()}%")


        Slider(
            value = volumen,
            onValueChange = { volumen = it },
            valueRange = 0f..100f
        )

        Button(onClick = {
            val nuevaConfig = configActual?.copy(volumen = volumen) ?: AppConfig(volumen = volumen)
            escribirFirebase("settings", nuevaConfig)
        }) {
            Text("Guardar Volumen")
        }
    }
}


@Composable
fun PantallaSonido() {
    val (configActual, _, _) = LeerFirebase("settings", AppConfig::class.java)
    var sonidoSeleccionado by rememberSaveable { mutableStateOf("Alarma Estándar") }
    val opciones = listOf("Alarma Estándar", "Tono Agudo", "Bip Intermitente")

    LaunchedEffect(configActual) {
        configActual?.let { sonidoSeleccionado = it.tipoSonido }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selecciona un Sonido:")


        opciones.forEach { opcion ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (opcion == sonidoSeleccionado),
                    onClick = { sonidoSeleccionado = opcion }
                )
                Text(text = opcion, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Button(onClick = {
            val nuevaConfig = configActual?.copy(tipoSonido = sonidoSeleccionado) ?: AppConfig(tipoSonido = sonidoSeleccionado)
            escribirFirebase("settings", nuevaConfig)
        }) {
            Text("Guardar Sonido")
        }
    }
}
