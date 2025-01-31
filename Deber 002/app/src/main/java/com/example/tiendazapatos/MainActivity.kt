package com.example.tiendazapatos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiendazapatos.ui.theme.TiendaZapatosTheme
import android.widget.Toast


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiendaZapatosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current // Obtener contexto para lanzar la actividad

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido a Tienda de Zapatos", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            try {
                val intent = Intent(context, InsertarZapatoActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al abrir la actividad", Toast.LENGTH_SHORT).show()
            }
        }
        ) {
            Text("Añadir Zapato")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TiendaZapatosTheme {
        MainScreen()
    }
}
