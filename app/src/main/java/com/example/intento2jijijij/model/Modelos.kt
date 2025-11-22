package com.example.intento2jijijij.model

data class AppConfig(
    val minUmbral: Int = 25,
    val maxUmbral: Int = 75,
    val volumen: Float = 50f,
    val tipoSonido: String = "Alarma Estándar"
) {
    // Constructor vacío necesario para Firebase
    constructor() : this(25, 75, 50f, "Alarma Estándar")
}

data class SensorData(
    val raw_value: Int = 0,
    val device: String = ""
) {
    // Constructor vacío necesario para Firebase
    constructor() : this(0, "")
}