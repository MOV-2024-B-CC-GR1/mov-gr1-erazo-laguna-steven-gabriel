package com.example.zapatostiendaapp

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun SucursalMapDialog(context: Context, sucursal: String, lat: Double, lon: Double, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubicación de $sucursal") },
        text = {
            AndroidView(factory = { ctx ->
                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                val mapView = MapView(ctx)
                mapView.setMultiTouchControls(true)

                val mapController = mapView.controller
                mapController.setZoom(15.0)
                mapController.setCenter(org.osmdroid.util.GeoPoint(lat, lon))

                val marker = Marker(mapView)
                marker.position = org.osmdroid.util.GeoPoint(lat, lon)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = sucursal
                mapView.overlays.add(marker)

                mapView
            }, modifier = Modifier.fillMaxWidth().height(400.dp))
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

