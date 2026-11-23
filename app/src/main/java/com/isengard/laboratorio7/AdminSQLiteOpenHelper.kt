package com.isengard.laboratorio7

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context,
    name: String = DATABASE_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = DATABASE_VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        const val DATABASE_NAME = "ejercito.db"
        const val DATABASE_VERSION = 1
        const val TABLE_BATALLONES = "batallones"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_SOLDADOS = "soldados"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_BATALLONES (
                $COLUMN_NOMBRE TEXT PRIMARY KEY,
                $COLUMN_SOLDADOS INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BATALLONES")
        onCreate(db)
    }
}