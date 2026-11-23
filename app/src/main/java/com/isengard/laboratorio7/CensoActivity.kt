package com.isengard.laboratorio7

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CensoActivity : AppCompatActivity()
{
    private lateinit var adminSQLite: AdminSQLiteOpenHelper
    private lateinit var nombreBatallon: EditText
    private lateinit var etsoldados: EditText
    private lateinit var listadoCensoTexto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_censo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCenso)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adminSQLite = AdminSQLiteOpenHelper(this)

        nombreBatallon = findViewById(R.id.nombreBatallon)
        etsoldados = findViewById(R.id.etSoldados)
        listadoCensoTexto = findViewById(R.id.listado)

        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        val btnBaja: Button = findViewById(R.id.btnBajaBatallon)
        val btnVolver: Button = findViewById(R.id.btnVolver)

        btnGuardar.setOnClickListener { registrarOActualizar() }
        btnBaja.setOnClickListener { darDeBaja() }
        btnVolver.setOnClickListener { finish() }

        consultarCenso()
    }

    private fun registrarOActualizar() {
        val nombre = nombreBatallon.text.toString().trim()
        val soldadosStr = etsoldados.text.toString().trim()

        if (nombre.isEmpty()) {
            nombreBatallon.error = "Introduce el nombre del batallón"
            return
        }
        val soldados = soldadosStr.toIntOrNull()
        if (soldados == null || soldados < 0) {
            etsoldados.error = "Introduce una cantidad válida"
            return
        }

        val db: SQLiteDatabase = adminSQLite.writableDatabase
        try {
            val values = ContentValues().apply {
                put(AdminSQLiteOpenHelper.COLUMN_NOMBRE, nombre)
                put(AdminSQLiteOpenHelper.COLUMN_SOLDADOS, soldados)
            }

            val id = db.insertWithOnConflict(
                AdminSQLiteOpenHelper.TABLE_BATALLONES,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )

            if (id != -1L) {
                Toast.makeText(this, "Batallón '$nombre' censado", Toast.LENGTH_SHORT).show()
                nombreBatallon.text.clear()
                etsoldados.text.clear()
                consultarCenso()
            }
        } finally {
            db.close()
        }
    }

    private fun darDeBaja() {
        val nombre = nombreBatallon.text.toString().trim()
        if (nombre.isEmpty()) {
            nombreBatallon.error = "Escribe el nombre del batallón a licenciar"
            return
        }

        val db: SQLiteDatabase = adminSQLite.writableDatabase
        try {
            val filasBorradas = db.delete(
                AdminSQLiteOpenHelper.TABLE_BATALLONES,
                "${AdminSQLiteOpenHelper.COLUMN_NOMBRE} = ?",
                arrayOf(nombre)
            )

            if (filasBorradas > 0) {
                Toast.makeText(this, "Batallón '$nombre' dado de baja", Toast.LENGTH_SHORT).show()
                nombreBatallon.text.clear()
                etsoldados.text.clear()
                consultarCenso()
            } else {
                Toast.makeText(this, "No existe ningún batallón con ese nombre", Toast.LENGTH_SHORT).show()
            }
        } finally {
            db.close()
        }
    }

    private fun consultarCenso() {
        val db: SQLiteDatabase = adminSQLite.readableDatabase
        var cursor: Cursor? = null
        val sb = StringBuilder()

        try {
            val sql = """
                SELECT ${AdminSQLiteOpenHelper.COLUMN_NOMBRE}, ${AdminSQLiteOpenHelper.COLUMN_SOLDADOS}
                FROM ${AdminSQLiteOpenHelper.TABLE_BATALLONES}
                ORDER BY ${AdminSQLiteOpenHelper.COLUMN_SOLDADOS} DESC
            """.trimIndent()

            cursor = db.rawQuery(sql, null)

            if (cursor.moveToFirst()) {
                val idxNombre = cursor.getColumnIndexOrThrow(AdminSQLiteOpenHelper.COLUMN_NOMBRE)
                val idxSoldados = cursor.getColumnIndexOrThrow(AdminSQLiteOpenHelper.COLUMN_SOLDADOS)

                do {
                    val nombre = cursor.getString(idxNombre)
                    val soldados = cursor.getInt(idxSoldados)
                    sb.append("• $nombre: $soldados efectivos\n")
                } while (cursor.moveToNext())

                listadoCensoTexto.text = sb.toString()
            } else {
                listadoCensoTexto.text = "No hay batallones desplegados."
            }
        } finally {
            cursor?.close()
            db.close()
        }
    }


}
