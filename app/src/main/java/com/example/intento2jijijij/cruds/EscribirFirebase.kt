package com.example.intento2jijijij.cruds

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

fun escribirFirebase(
    field: String,
    value: Any,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val database = Firebase.database
    val myRef = database.getReference(field)

    Log.d("FirebaseWrite", "Iniciando escritura en: $field")
    Log.d("FirebaseWrite", "Objeto a escribir: $value")

    myRef.setValue(value)
        .addOnSuccessListener {
            Log.d("FirebaseWrite", "✅ Datos escritos exitosamente en $field")
            onSuccess()
        }
        .addOnFailureListener { error ->
            Log.e("FirebaseWrite", "❌ Error escribiendo en $field", error)
            onError("Error: ${error.message}")
        }
}