package com.example.zapatostiendaapp

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.platform.LocalContext


@Composable
fun SucursalMapDropdownMenuComponent(
    sucursales: List<String>,
    selectedSucursal: String,
    onSucursalSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(Pair(0.0, 0.0)) }
    val context = LocalContext.current

    // Coordenadas de las sucursales
    val ubicaciones = mapOf(
        "Sucursal A" to Pair(-0.180653, -78.467834), // Quito
        "Sucursal B" to Pair(-12.046374, -77.042793), // Lima
        "Sucursal C" to Pair(40.712776, -74.005974)  // Nueva York
    )

    Box {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (selectedSucursal.isEmpty()) "Selecciona una sucursal" else selectedSucursal)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sucursales.forEach { sucursal ->
                DropdownMenuItem(
                    text = { Text(sucursal) },
                    onClick = {
                        onSucursalSelected(sucursal)
                        expanded = false
                        selectedLocation = ubicaciones[sucursal] ?: Pair(0.0, 0.0)
                        showMap = true
                    }
                )
            }
        }
    }

    if (showMap) {
        SucursalMapDialog(
            context = context,
            sucursal = selectedSucursal,
            lat = selectedLocation.first,
            lon = selectedLocation.second,
            onDismiss = { showMap = false }
        )
    }
}
