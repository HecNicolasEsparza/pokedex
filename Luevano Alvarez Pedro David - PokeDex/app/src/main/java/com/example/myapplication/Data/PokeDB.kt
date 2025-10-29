package com.example.myapplication.Data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PokeDB(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    override fun onCreate(db: SQLiteDatabase){
            val crearTablaUsuarios = """
                CREATE TABLE $TABLE_Pokemon (
                    $ID_pokemon INTEGER PRIMARY KEY AUTOINCREMENT,
                    $nombre_pokemon TEXT,
                    $type_pokemon INTEGER
                 )
            """.trimIndent()
            db.execSQL(crearTablaUsuarios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS $TABLE_Pokemon")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "pokedex.db"
        const val DATABASE_VERSION = 1
        const val TABLE_Pokemon = "pokemon"
        const val ID_pokemon = "id"
        const val nombre_pokemon = "nombre"
        const val type_pokemon = "tipo"
    }



}

