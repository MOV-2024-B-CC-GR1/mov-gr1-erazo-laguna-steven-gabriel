package com.example.tiendazapatos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class InsertarZapatoActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextTalla: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var editTextCantidad: EditText
    private lateinit var buttonGuardarZapato: Button
    private lateinit var buttonMostrarZapatos: Button
    private lateinit var buttonActualizarZapato: Button
    private lateinit var buttonEliminarZapato: Button
    private lateinit var textViewZapatos: TextView
    private lateinit var editTextId: EditText  // Nuevo EditText para el ID

    private lateinit var dbHelper: ZapatoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertar_zapato)

        // Inicializar los elementos de la interfaz
        inicializarVistas()

        // Inicializar la base de datos
        dbHelper = ZapatoDbHelper(this)

        // Configurar los listeners de los botones
        configurarBotones()

        // Mostrar los zapatos al iniciar
        mostrarZapatos()
    }

    private fun inicializarVistas() {
        editTextId = findViewById(R.id.editTextId)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextTalla = findViewById(R.id.editTextTalla)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        editTextCantidad = findViewById(R.id.editTextCantidad)
        buttonGuardarZapato = findViewById(R.id.buttonGuardarZapato)
        buttonMostrarZapatos = findViewById(R.id.buttonMostrarZapatos)
        buttonActualizarZapato = findViewById(R.id.buttonActualizarZapato)
        buttonEliminarZapato = findViewById(R.id.buttonEliminarZapato)
        textViewZapatos = findViewById(R.id.textViewZapatos)
    }

    private fun configurarBotones() {
        buttonGuardarZapato.setOnClickListener { guardarZapato() }
        buttonMostrarZapatos.setOnClickListener { mostrarZapatos() }
        buttonActualizarZapato.setOnClickListener { mostrarDialogoActualizar() }
        buttonEliminarZapato.setOnClickListener { mostrarDialogoEliminar() }
    }

    private fun guardarZapato() {
        try {
            val nombre = editTextNombre.text.toString().trim()
            val talla = editTextTalla.text.toString().trim()
            val precioText = editTextPrecio.text.toString().trim()
            val cantidadText = editTextCantidad.text.toString().trim()

            if (nombre.isEmpty() || talla.isEmpty() || precioText.isEmpty() || cantidadText.isEmpty()) {
                mostrarMensaje("Por favor, complete todos los campos")
                return
            }

            val precio = precioText.toDoubleOrNull()
            val cantidad = cantidadText.toIntOrNull()

            if (precio == null || cantidad == null) {
                mostrarMensaje("Por favor, ingrese valores numéricos válidos")
                return
            }

            val zapato = Zapato(nombre = nombre, talla = talla, precio = precio, cantidad = cantidad)
            val resultado = dbHelper.insertarZapato(zapato)

            if (resultado != -1L) {
                mostrarMensaje("Zapato guardado exitosamente")
                limpiarCampos()
                mostrarZapatos()
            } else {
                mostrarMensaje("Error al guardar el zapato")
            }
        } catch (e: Exception) {
            mostrarMensaje("Error: ${e.message}")
        }
    }

    private fun mostrarZapatos() {
        try {
            val zapatos = dbHelper.obtenerZapatos()
            if (zapatos.isEmpty()) {
                textViewZapatos.text = "No hay zapatos registrados"
                return
            }

            val zapatosText = StringBuilder()
            zapatos.forEach { zapato ->
                zapatosText.append("""
                    ID: ${zapato.id}
                    Nombre: ${zapato.nombre}
                    Talla: ${zapato.talla}
                    Precio: $${String.format("%.2f", zapato.precio)}
                    Cantidad: ${zapato.cantidad}
                    ------------------------
                    
                """.trimIndent())
            }
            textViewZapatos.text = zapatosText.toString()
        } catch (e: Exception) {
            mostrarMensaje("Error al mostrar zapatos: ${e.message}")
        }
    }

    private fun mostrarDialogoActualizar() {
        val idText = editTextId.text.toString().trim()
        if (idText.isEmpty()) {
            mostrarMensaje("Por favor, ingrese el ID del zapato a actualizar")
            return
        }

        try {
            val id = idText.toInt()
            val nombre = editTextNombre.text.toString().trim()
            val talla = editTextTalla.text.toString().trim()
            val precio = editTextPrecio.text.toString().trim().toDoubleOrNull()
            val cantidad = editTextCantidad.text.toString().trim().toIntOrNull()

            if (nombre.isEmpty() || talla.isEmpty() || precio == null || cantidad == null) {
                mostrarMensaje("Por favor, complete todos los campos con valores válidos")
                return
            }

            AlertDialog.Builder(this)
                .setTitle("Actualizar Zapato")
                .setMessage("¿Está seguro que desea actualizar este zapato?")
                .setPositiveButton("Sí") { _, _ ->
                    actualizarZapato(id, nombre, talla, precio, cantidad)
                }
                .setNegativeButton("No", null)
                .show()
        } catch (e: Exception) {
            mostrarMensaje("Error: ${e.message}")
        }
    }

    private fun actualizarZapato(id: Int, nombre: String, talla: String, precio: Double, cantidad: Int) {
        try {
            val zapato = Zapato(nombre = nombre, talla = talla, precio = precio, cantidad = cantidad)
            val filasActualizadas = dbHelper.actualizarZapato(id, zapato)

            if (filasActualizadas > 0) {
                mostrarMensaje("Zapato actualizado exitosamente")
                limpiarCampos()
                mostrarZapatos()
            } else {
                mostrarMensaje("No se encontró el zapato con ID: $id")
            }
        } catch (e: Exception) {
            mostrarMensaje("Error al actualizar: ${e.message}")
        }
    }

    private fun mostrarDialogoEliminar() {
        val idText = editTextId.text.toString().trim()
        if (idText.isEmpty()) {
            mostrarMensaje("Por favor, ingrese el ID del zapato a eliminar")
            return
        }

        try {
            val id = idText.toInt()
            AlertDialog.Builder(this)
                .setTitle("Eliminar Zapato")
                .setMessage("¿Está seguro que desea eliminar este zapato?")
                .setPositiveButton("Sí") { _, _ ->
                    eliminarZapato(id)
                }
                .setNegativeButton("No", null)
                .show()
        } catch (e: Exception) {
            mostrarMensaje("Error: ${e.message}")
        }
    }

    private fun eliminarZapato(id: Int) {
        try {
            val filasEliminadas = dbHelper.eliminarZapato(id)
            if (filasEliminadas > 0) {
                mostrarMensaje("Zapato eliminado exitosamente")
                limpiarCampos()
                mostrarZapatos()
            } else {
                mostrarMensaje("No se encontró el zapato con ID: $id")
            }
        } catch (e: Exception) {
            mostrarMensaje("Error al eliminar: ${e.message}")
        }
    }

    private fun limpiarCampos() {
        editTextId.text.clear()
        editTextNombre.text.clear()
        editTextTalla.text.clear()
        editTextPrecio.text.clear()
        editTextCantidad.text.clear()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}