package com.example.zapatostiendaapp.database.database

import android.content.ContentValues
import android.content.Context
import com.example.zapatostiendaapp.database.model.Shoe

class ShoeRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Insertar un nuevo zapato
    fun insertShoe(shoe: Shoe): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, shoe.name)
            put(DatabaseHelper.COLUMN_PRICE, shoe.price)
            put(DatabaseHelper.COLUMN_SIZE, shoe.size)
            put(DatabaseHelper.COLUMN_LOCATION, shoe.location)
        }
        return db.insert(DatabaseHelper.TABLE_SHOES, null, values)
    }

    // Obtener todos los zapatos
    fun getAllShoes(): List<Shoe> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_SHOES, // Tabla
            null, // Seleccionamos todas las columnas
            null, // Sin filtro
            null, // Sin argumentos
            null, // Sin agrupar
            null, // Sin ordenar
            null  // Sin límite
        )

        val shoes = mutableListOf<Shoe>()
        while (cursor.moveToNext()) {
            // Obtener el índice de las columnas
            val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)
            val priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE)
            val sizeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SIZE)
            val locationIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION)

            // Verificar si los índices son válidos (mayores o iguales a 0)
            if (idIndex >= 0 && nameIndex >= 0 && priceIndex >= 0 && sizeIndex >= 0 && locationIndex >= 0) {
                // Crear el objeto Shoe con los datos obtenidos del cursor
                val shoe = Shoe(
                    id = cursor.getLong(idIndex),
                    name = cursor.getString(nameIndex),
                    price = cursor.getDouble(priceIndex),
                    size = cursor.getDouble(sizeIndex),
                    location = cursor.getString(locationIndex)
                )
                shoes.add(shoe)
            }
        }
        cursor.close()
        return shoes
    }


    // Actualizar un zapato
    fun updateShoe(shoe: Shoe): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, shoe.name)
            put(DatabaseHelper.COLUMN_PRICE, shoe.price)
            put(DatabaseHelper.COLUMN_SIZE, shoe.size)
            put(DatabaseHelper.COLUMN_LOCATION, shoe.location)
        }
        return db.update(
            DatabaseHelper.TABLE_SHOES,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(shoe.id.toString())
        )
    }

    // Eliminar un zapato
    fun deleteShoe(shoeId: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_SHOES,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(shoeId.toString())
        )
    }
}