package com.example.zapatostiendaapp.database.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tienda_zapatos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_SHOES = "shoes"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_SIZE = "size"
        const val COLUMN_LOCATION = "location"  // Para almacenar la ubicación
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_SHOES_TABLE = ("CREATE TABLE $TABLE_SHOES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_PRICE REAL," +
                "$COLUMN_SIZE REAL," +
                "$COLUMN_LOCATION TEXT" + ")")
        db?.execSQL(CREATE_SHOES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SHOES")
        onCreate(db)
    }
}
