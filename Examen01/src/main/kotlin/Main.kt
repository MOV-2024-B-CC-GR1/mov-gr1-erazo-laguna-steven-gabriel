package org.example

import java.io.File

import java.util.*

data class Sucursal(
    val identificador: Int,
    var direccion: String,
    var calzado: MutableList<Producto>
)

data class Producto(
    val codigo: Int,
    var marca: String,
    var modelo: String,
    var talla: Int,
    var precio: Float,
    var disponible: Boolean
)

fun main() {
    val archivo = File("sucursales.txt")
    val sucursales = if (archivo.exists()) {
        leerDesdeArchivo(archivo)
    } else {
        mutableListOf()
    }

    var opcion: Int

    do {
        println("\n--- MENÚ ADMINISTRACIÓN DE SUCURSALES ---")
        println("1. CREAR NUEVA SUCURSAL")
        println("2. LISTAR SUCURSALES")
        println("3. ACTUALIZAR SUCURSAL")
        println("4. ELIMINAR SUCURSAL")
        println("5. AGREGAR PRODUCTOS A SUCURSAL")
        println("6. MOSTRAR PRODUCTOS DE SUCURSAL")
        println("7. ACTUALIZAR CARACTERÍSTICAS DE PRODUCTO")
        println("8. ELIMINAR PRODUCTO")
        println("9. SALIR")
        print("Selecciona una opción: ")

        opcion = readLine()?.toIntOrNull() ?: 0

        when (opcion) {
            1 -> crearSucursal(sucursales)
            2 -> listarSucursales(sucursales)
            3 -> actualizarSucursal(sucursales)
            4 -> eliminarSucursal(sucursales)
            5 -> agregarProducto(sucursales)
            6 -> mostrarProductos(sucursales)
            7 -> actualizarProducto(sucursales)
            8 -> eliminarProducto(sucursales)
            9 -> {
                println("Guardando datos y saliendo...")
                guardarEnArchivo(archivo, sucursales)
            }
            else -> println("Opción inválida. Intente de nuevo.")
        }

    } while (opcion != 9)
}

// Funciones para guardar y leer desde archivo
fun guardarEnArchivo(archivo: File, sucursales: List<Sucursal>) {
    archivo.printWriter().use { writer ->
        sucursales.forEach { sucursal ->
            writer.println("${sucursal.identificador}|${sucursal.direccion}")
            sucursal.calzado.forEach { producto ->
                writer.println(
                    "${producto.codigo}|${producto.marca}|${producto.modelo}|${producto.talla}|${producto.precio}|${producto.disponible}"
                )
            }
        }
    }
}

fun leerDesdeArchivo(archivo: File): MutableList<Sucursal> {
    val sucursales = mutableListOf<Sucursal>()
    val lineas = archivo.readLines()
    var sucursalActual: Sucursal? = null

    for (linea in lineas) {
        val datos = linea.split("|")
        if (datos.size == 2) {
            // Es una sucursal
            sucursalActual = Sucursal(datos[0].toInt(), datos[1], mutableListOf())
            sucursales.add(sucursalActual)
        } else if (datos.size == 6) {
            // Es un producto
            val producto = Producto(
                datos[0].toInt(),
                datos[1],
                datos[2],
                datos[3].toInt(),
                datos[4].toFloat(),
                datos[5].toBooleanStrictOrNull() ?: true
            )
            sucursalActual?.calzado?.add(producto)
        }
    }
    return sucursales
}

// Funciones de sucursales
fun crearSucursal(sucursales: MutableList<Sucursal>) {
    print("Ingrese el identificador de la sucursal: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    print("Ingrese la dirección de la sucursal: ")
    val direccion = readLine().orEmpty()

    val nuevaSucursal = Sucursal(identificador, direccion, mutableListOf())
    sucursales.add(nuevaSucursal)
    println("Sucursal creada con éxito.")
}

fun listarSucursales(sucursales: List<Sucursal>) {
    if (sucursales.isEmpty()) {
        println("No existen sucursales registradas.")
        return
    }
    sucursales.forEach { sucursal ->
        println("ID: ${sucursal.identificador}, Dirección: ${sucursal.direccion}")
    }
}

fun actualizarSucursal(sucursales: MutableList<Sucursal>) {
    print("Ingrese el identificador de la sucursal a actualizar: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    val sucursal = sucursales.find { it.identificador == identificador }

    if (sucursal != null) {
        print("Ingrese la nueva dirección de la sucursal (${sucursal.direccion}): ")
        sucursal.direccion = readLine().orEmpty()
        println("Sucursal actualizada con éxito.")
    } else {
        println("Sucursal no encontrada.")
    }
}

fun eliminarSucursal(sucursales: MutableList<Sucursal>) {
    print("Ingrese el identificador de la sucursal a eliminar: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    val sucursal = sucursales.find { it.identificador == identificador }

    if (sucursal != null) {
        sucursales.remove(sucursal)
        println("Sucursal eliminada con éxito.")
    } else {
        println("Sucursal no encontrada.")
    }
}

// Funciones de productos
fun agregarProducto(sucursales: MutableList<Sucursal>) {
    print("Ingrese el identificador de la sucursal: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    val sucursal = sucursales.find { it.identificador == identificador }

    if (sucursal != null) {
        print("Ingrese el código del producto: ")
        val codigo = readLine()?.toIntOrNull() ?: return
        print("Ingrese la marca del producto: ")
        val marca = readLine().orEmpty()
        print("Ingrese el modelo del producto: ")
        val modelo = readLine().orEmpty()
        print("Ingrese la talla del producto: ")
        val talla = readLine()?.toIntOrNull() ?: return
        print("Ingrese el precio del producto: ")
        val precio = readLine()?.toFloatOrNull() ?: return
        print("¿Está disponible? (true/false): ")
        val disponible = readLine()?.toBooleanStrictOrNull() ?: true

        val nuevoProducto = Producto(codigo, marca, modelo, talla, precio, disponible)
        sucursal.calzado.add(nuevoProducto)
        println("Producto agregado con éxito.")
    } else {
        println("Sucursal no encontrada.")
    }
}

fun mostrarProductos(sucursales: List<Sucursal>) {
    print("Ingrese el identificador de la sucursal: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    val sucursal = sucursales.find { it.identificador == identificador }

    if (sucursal != null) {
        if (sucursal.calzado.isEmpty()) {
            println("No hay productos en esta sucursal.")
            return
        }
        sucursal.calzado.forEach { producto ->
            println(
                "Código: ${producto.codigo}, Marca: ${producto.marca}, " +
                        "Modelo: ${producto.modelo}, Talla: ${producto.talla}, " +
                        "Precio: ${producto.precio}, Disponible: ${producto.disponible}"
            )
        }
    } else {
        println("Sucursal no encontrada.")
    }
}

fun actualizarProducto(sucursales: MutableList<Sucursal>) {
    print("Ingrese el identificador de la sucursal: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    val sucursal = sucursales.find { it.identificador == identificador }

    if (sucursal != null) {
        print("Ingrese el código del producto a actualizar: ")
        val codigo = readLine()?.toIntOrNull() ?: return
        val producto = sucursal.calzado.find { it.codigo == codigo }

        if (producto != null) {
            print("Ingrese la nueva marca (${producto.marca}): ")
            producto.marca = readLine().orEmpty()
            print("Ingrese el nuevo modelo (${producto.modelo}): ")
            producto.modelo = readLine().orEmpty()
            print("Ingrese la nueva talla (${producto.talla}): ")
            producto.talla = readLine()?.toIntOrNull() ?: producto.talla
            print("Ingrese el nuevo precio (${producto.precio}): ")
            producto.precio = readLine()?.toFloatOrNull() ?: producto.precio
            print("¿Está disponible? (${producto.disponible}): ")
            producto.disponible = readLine()?.toBooleanStrictOrNull() ?: producto.disponible

            println("Producto actualizado con éxito.")
        } else {
            println("Producto no encontrado.")
        }
    } else {
        println("Sucursal no encontrada.")
    }
}

fun eliminarProducto(sucursales: MutableList<Sucursal>) {
    print("Ingrese el identificador de la sucursal: ")
    val identificador = readLine()?.toIntOrNull() ?: return
    val sucursal = sucursales.find { it.identificador == identificador }

    if (sucursal != null) {
        print("Ingrese el código del producto a eliminar: ")
        val codigo = readLine()?.toIntOrNull() ?: return
        val producto = sucursal.calzado.find { it.codigo == codigo }

        if (producto != null) {
            sucursal.calzado.remove(producto)
            println("Producto eliminado con éxito.")
        } else {
            println("Producto no encontrado.")
        }
    } else {
        println("Sucursal no encontrada.")
    }
}
