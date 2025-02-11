package com.example.zapatostiendaapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zapatostiendaapp.database.model.Shoe
import com.example.zapatostiendaapp.ui.theme.ZapatosTiendaAppTheme
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.viewinterop.AndroidView



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZapatosTiendaAppTheme {
                MainScreen()
            }
        }
    }
}


// Composable principal con menú de sucursal y botones
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedSucursal by remember { mutableStateOf("") }
    val sucursales = listOf("Sucursal A", "Sucursal B", "Sucursal C") // Ejemplo de sucursales
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var mostrarListaZapatos by remember { mutableStateOf(false) }

    // Mapa de ubicaciones para cada sucursal
    val ubicaciones = mapOf(
        "Sucursal A" to Pair(-0.180653, -78.467834), // Quito
        "Sucursal B" to Pair(-12.046374, -77.042793), // Lima
        "Sucursal C" to Pair(40.712776, -74.005974)  // Nueva York
    )

    // Estado para mostrar el mapa
    var showMap by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(Pair(0.0, 0.0)) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Seleccionar Sucursal") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Menú para seleccionar sucursal
                DropdownMenuComponent(
                    sucursales = sucursales,
                    selectedSucursal = selectedSucursal,
                    onSucursalSelected = {
                        selectedSucursal = it
                        // Asignar la ubicación de la sucursal seleccionada
                        selectedLocation = ubicaciones[it] ?: Pair(0.0, 0.0)
                        showMap = true // Mostrar el mapa
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botón para agregar zapato
                Button(onClick = { showDialog = true }) {
                    Text("Agregar Zapato")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mostrar zapatos
                Button(onClick = { mostrarListaZapatos = true }) {
                    Text("Mostrar Zapatos")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Editar zapatos
                Button(onClick = { /* Navegar a actividad de editar zapatos */ }) {
                    Text("Editar Zapatos")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Eliminar zapatos
                Button(onClick = { /* Navegar a actividad de eliminar zapatos */ }) {
                    Text("Eliminar Zapatos")
                }

                // Mostrar la lista de zapatos si se selecciona
                if (mostrarListaZapatos) {
                    MostrarZapatos(context)
                }

                // Mostrar mapa si se selecciona una sucursal
                if (showMap) {
                    MostrarMapa(location = selectedLocation)
                }
            }
        }
    )

    // Muestra el diálogo si `showDialog` es true
    if (showDialog) {
        AgregarZapatoDialog(context) { showDialog = false }
    }
}

@Composable
fun MostrarMapa(location: Pair<Double, Double>) {
    val context = LocalContext.current
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                // Establecer la fuente de las tiles (mapas)
                setTileSource(TileSourceFactory.MAPNIK)
                setBuiltInZoomControls(true)  // Habilitar controles de zoom
                setMultiTouchControls(true)   // Habilitar el soporte para multi-touch
                controller.setZoom(15)        // Establecer el nivel de zoom
                controller.setCenter(GeoPoint(location.first, location.second))  // Establecer la posición inicial

                // Añadir marcador
                val marker = Marker(this)
                marker.position = GeoPoint(location.first, location.second)
                marker.title = "Ubicación seleccionada"
                overlays.add(marker)  // Añadir el marcador al mapa
            }
        },
        modifier = Modifier.fillMaxSize()  // El mapa ocupa toda la pantalla
    )

    // Mostrar texto debajo del mapa con las coordenadas
    Text(
        text = "Mostrando mapa para la ubicación: Lat: ${location.first}, Lng: ${location.second}",
        modifier = Modifier.padding(16.dp)
    )
}



// Composable para el menú desplegable de sucursales
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuComponent(
    sucursales: List<String>,
    selectedSucursal: String,
    onSucursalSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Menú desplegable
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (selectedSucursal.isEmpty()) "Selecciona una sucursal" else selectedSucursal)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            sucursales.forEach { sucursal ->
                DropdownMenuItem(
                    text = { Text(sucursal) },
                    onClick = {
                        onSucursalSelected(sucursal)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MostrarZapatos(context: Context) {
    var mostrarLista by remember { mutableStateOf(false) }
    val zapatos = remember { mutableStateListOf<Shoe>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            zapatos.clear()
            zapatos.add(Shoe(name = "Nike", size = 35.0, price = 100.0, location = "Almacén A"))
            zapatos.add(Shoe(name = "Adidas", size = 40.0, price = 150.0, location = "Almacén B"))

            zapatos.forEach { println("Zapato: ${it.name}, Talla: ${it.size}, Precio: ${it.price}") }

            mostrarLista = true // 🔹 Ahora activamos la visibilidad de la lista
        }) {
            Text("Mostrar Zapatos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mostrarLista) { // 🔹 Solo se muestra la lista si `mostrarLista` es `true`
            LazyColumn {
                items(zapatos) { zapato ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Zapato: ${zapato.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Talla: ${zapato.size}")
                            Text(text = "Precio: $${zapato.price}")
                            Text(text = "Ubicación: ${zapato.location}")
                        }
                    }
                }
            }
        }
    }
}

// Composable para agregar un zapato
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarZapatoDialog(context: Context, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Zapato") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) price = it
                    },
                    label = { Text("Precio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = size,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) size = it
                    },
                    label = { Text("Talla") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                agregarZapato(context, name, price.toDoubleOrNull() ?: 0.0, size.toDoubleOrNull() ?: 0.0, location)
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



// Función para insertar un zapato en la base de datos
fun agregarZapato(context: Context, name: String, price: Double, size: Double, location: String) {
    val db: SQLiteDatabase = context.openOrCreateDatabase("ZapatosDB", Context.MODE_PRIVATE, null)
    db.execSQL("CREATE TABLE IF NOT EXISTS zapatos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, precio REAL, talla REAL, ubicacion TEXT)")

    val values = ContentValues().apply {
        put("nombre", name)
        put("precio", price)
        put("talla", size)
        put("ubicacion", location)
    }
    db.insert("zapatos", null, values)
    db.close()
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZapatosTiendaAppTheme {
        MainScreen()
    }
}
