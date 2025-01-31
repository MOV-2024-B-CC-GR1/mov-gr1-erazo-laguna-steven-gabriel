package com.example.tiendazapatos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor

class ZapatoDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_ZAPATOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT,
                $COLUMN_TALLA TEXT,
                $COLUMN_PRECIO REAL,
                $COLUMN_CANTIDAD INTEGER
            );
        """
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ZAPATOS")
        onCreate(db)
    }

    // Insertar un zapato en la base de datos
    fun insertarZapato(zapato: Zapato): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, zapato.nombre)
            put(COLUMN_TALLA, zapato.talla)
            put(COLUMN_PRECIO, zapato.precio)
            put(COLUMN_CANTIDAD, zapato.cantidad)
        }
        return db.insert(TABLE_ZAPATOS, null, values)
    }

    // Obtener todos los zapatos de la base de datos
    fun obtenerZapatos(): List<Zapato> {
        val zapatos = mutableListOf<Zapato>()
        val db = readableDatabase

        // Define las columnas que quieres obtener
        val columnas = arrayOf(
            COLUMN_ID,
            COLUMN_NOMBRE,
            COLUMN_TALLA,
            COLUMN_PRECIO,
            COLUMN_CANTIDAD
        )

        val cursor = db.query(
            TABLE_ZAPATOS,
            columnas,
            null,
            null,
            null,
            null,
            null
        )

        cursor.use { // Esto asegura que el cursor se cierre automáticamente
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                    val talla = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TALLA))
                    val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO))
                    val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANTIDAD))

                    zapatos.add(Zapato(id, nombre, talla, precio, cantidad))
                } while (cursor.moveToNext())
            }
        }

        return zapatos

    }

    // Actualizar los detalles de un zapato en la base de datos
    fun actualizarZapato(id: Int, zapato: Zapato): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, zapato.nombre)
            put(COLUMN_TALLA, zapato.talla)
            put(COLUMN_PRECIO, zapato.precio)
            put(COLUMN_CANTIDAD, zapato.cantidad)
        }
        return db.update(TABLE_ZAPATOS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Eliminar un zapato de la base de datos
    fun eliminarZapato(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_ZAPATOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    companion object {
        const val DATABASE_NAME = "zapatos.db"
        const val DATABASE_VERSION = 1
        const val TABLE_ZAPATOS = "zapatos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_TALLA = "talla"
        const val COLUMN_PRECIO = "precio"
        const val COLUMN_CANTIDAD = "cantidad"
    }
}
