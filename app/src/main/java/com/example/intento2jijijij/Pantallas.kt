package com.example.intento2jijijij

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.intento2jijijij.cruds.LeerFirebase
import com.example.intento2jijijij.cruds.escribirFirebase
import com.example.intento2jijijij.model.AppConfig
import com.example.intento2jijijij.model.SensorData
import com.example.intento2jijijij.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(navController: NavController) {
    val (sensorData, isLoading, _) = LeerFirebase("sensor_data", SensorData::class.java)
    val ppmGas = sensorData?.raw_value ?: 0

    val colorEstado = when {
        ppmGas > 75 -> DangerRed
        ppmGas > 25 -> WarningAmber
        else -> SafeGreen
    }

    val textoEstado = when {
        ppmGas > 75 -> "PELIGRO"
        ppmGas > 25 -> "ALERTA"
        else -> "Nivel Normal"
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Estado del Gas", color = TextWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    drawArc(
                        color = SurfaceCard,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = colorEstado,
                        startAngle = -90f,
                        sweepAngle = (ppmGas / 100f) * 360f, // Porcentaje simple visual
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$ppmGas", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text(text = "PPM", fontSize = 14.sp, color = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = textoEstado, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(text = "Conectado y recibiendo datos", fontSize = 14.sp, color = TextGray)

            Spacer(modifier = Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Concentración de Gas", color = TextWhite)
                    Text("$ppmGas PPM", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (ppmGas / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = colorEstado,
                    trackColor = SurfaceCard,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetaMenu(
                    icon = Icons.Default.Settings,
                    title = "Configurar\nUmbrales",
                    subtitle = "Define los niveles",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate("umbrales") }

                TarjetaMenu(
                    icon = Icons.Default.VolumeUp,
                    title = "Ajustar\nVolumen",
                    subtitle = "Controla el buzzer",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate("volumen") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TarjetaMenu(
                icon = Icons.Default.MusicNote,
                title = "Elegir Sonido",
                subtitle = "Selecciona el tono de la alarma",
                modifier = Modifier.fillMaxWidth(),
                horizontal = true
            ) { navController.navigate("sonido") }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUmbrales() {
    val (configActual, _, _) = LeerFirebase("settings", AppConfig::class.java)
    var minVal by remember { mutableStateOf(25f) }
    var maxVal by remember { mutableStateOf(75f) }

    LaunchedEffect(configActual) {
        configActual?.let {
            minVal = it.minUmbral.toFloat()
            maxVal = it.maxUmbral.toFloat()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Configurar Rango", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground, navigationIconContentColor = TextWhite)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp)) {
            Text("Ajusta los niveles para recibir alertas.", color = TextGray)
            Spacer(modifier = Modifier.height(24.dp))

            // VISUALIZADOR DE RANGO (BARRA TRICOLOR)
            Row(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp))) {
                Box(modifier = Modifier.weight(minVal).fillMaxHeight().background(SafeGreen))
                Box(modifier = Modifier.weight(maxVal - minVal).fillMaxHeight().background(WarningAmber))
                Box(modifier = Modifier.weight(100f - maxVal).fillMaxHeight().background(DangerRed))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Seguro", color = TextGray, fontSize = 12.sp)
                Text("Alerta", color = TextGray, fontSize = 12.sp)
                Text("Peligro", color = TextGray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))


            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nivel de Alerta (min)", color = TextWhite, fontWeight = FontWeight.Bold)
                Text("${minVal.toInt()} ppm", color = TextWhite)
            }
            Slider(
                value = minVal,
                onValueChange = { if (it < maxVal) minVal = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nivel de Peligro (max)", color = TextWhite, fontWeight = FontWeight.Bold)
                Text("${maxVal.toInt()} ppm", color = TextWhite)
            }
            Slider(
                value = maxVal,
                onValueChange = { if (it > minVal) maxVal = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
            )

            Spacer(modifier = Modifier.weight(1f))


            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Ajusta estos umbrales según las especificaciones de tus electrodomésticos.", color = TextGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonNaranja(texto = "Guardar Cambios") {
                val nuevaConfig = configActual?.copy(minUmbral = minVal.toInt(), maxUmbral = maxVal.toInt())
                    ?: AppConfig(minVal.toInt(), maxVal.toInt())
                escribirFirebase("settings", nuevaConfig)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVolumen() {
    val (configActual, _, _) = LeerFirebase("settings", AppConfig::class.java)
    var volumen by remember { mutableStateOf(50f) }

    LaunchedEffect(configActual) {
        configActual?.let { volumen = it.volumen }
    }

    Scaffold(containerColor = DarkBackground) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Volumen de la Alerta", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            // CIRCULO ICONO
            Box(
                modifier = Modifier.size(100.dp).background(SurfaceCard, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("${volumen.toInt()}%", fontSize = 60.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            Text("Selecciona qué tan fuerte sonará", color = TextGray)

            Spacer(modifier = Modifier.height(40.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VolumeMute, contentDescription = null, tint = TextGray)
                Slider(
                    value = volumen,
                    onValueChange = { volumen = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = TextWhite, activeTrackColor = OrangePrimary),
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = TextGray)
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonNaranja(texto = "Guardar Cambios") {
                val nuevaConfig = configActual?.copy(volumen = volumen) ?: AppConfig(volumen = volumen)
                escribirFirebase("settings", nuevaConfig)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSonido() {
    val (configActual, _, _) = LeerFirebase("settings", AppConfig::class.java)
    var sonidoSeleccionado by remember { mutableStateOf("Alarma Estándar") }
    val opciones = listOf("Alarma Estándar", "Tono Agudo", "Vibración Suave", "Bip Intermitente")

    LaunchedEffect(configActual) {
        configActual?.let { sonidoSeleccionado = it.tipoSonido }
    }

    Scaffold(containerColor = DarkBackground) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(80.dp).background(SurfaceCard, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona un Sonido", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text("Elige cómo sonará la alarma", color = TextGray, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(32.dp))

            opciones.forEach { opcion ->
                val isSelected = (opcion == sonidoSeleccionado)
                val borderColor = if (isSelected) OrangePrimary else SurfaceCard

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .background(SurfaceCard.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { sonidoSeleccionado = opcion }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(opcion, color = TextWhite, fontWeight = FontWeight.Bold)

                    Row {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = TextGray)
                        Spacer(modifier = Modifier.width(16.dp))
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = OrangePrimary)
                        } else {
                            Box(modifier = Modifier.size(24.dp).border(2.dp, TextGray, CircleShape))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            BotonNaranja(texto = "Guardar Selección") {
                val nuevaConfig = configActual?.copy(tipoSonido = sonidoSeleccionado) ?: AppConfig(tipoSonido = sonidoSeleccionado)
                escribirFirebase("settings", nuevaConfig)
            }
        }
    }
}



@Composable
fun TarjetaMenu(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, horizontal: Boolean = false, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        modifier = modifier
    ) {
        if (horizontal) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
                    Text(subtitle, color = TextGray, fontSize = 12.sp)
                }
            }
        } else {
            Column(modifier = Modifier.padding(20.dp)) {
                Icon(icon, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(title, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = TextGray, fontSize = 12.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
fun BotonNaranja(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(texto, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}