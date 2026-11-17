package com.isengard.laboratorio7

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PREFS_NAME = "RegistroGuardia"
        private const val KEY_SOLDADO = "KEY_SOLDADO"
        private const val KEY_RANGO = "KEY_RANGO"
        private const val KEY_ESTADO = "KEY_ESTADO"
        private const val FILE_INTERNO = "cronica.txt"
        private const val FILE_EXTERNO = "mapa_campana.txt"

    }




    private lateinit var nombre: EditText
    private lateinit var rango: RadioGroup
    private lateinit var estado: SwitchCompat
    private lateinit var btn: Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var btnPergaminos: Button
    private lateinit var lectura: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombre = findViewById(R.id.nombre)
        rango = findViewById(R.id.rango)
        estado = findViewById(R.id.swEstadoTurno)
        btn = findViewById(R.id.btn)



        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        btnPergaminos = findViewById(R.id.btnPergaminos)
        lectura = findViewById(R.id.lectura)

        btnPergaminos.setOnClickListener {
            procesarPergaminos()
        }

        btn.setOnClickListener {
            guardarDatosTacticos()
        }

    }
    override fun onResume() {
        super.onResume()
        cargarDatosTacticos()
    }


    private fun guardarDatosTacticos() {
        val textoNombre = nombre.text.toString().trim()
        //Aqui le ponemos una pequeña validación
        if (textoNombre.isEmpty()) {
            nombre.error = "Introduce el nombre del efectivo"
            return
        }


        val rangoSeleccionado = when (rango.checkedRadioButtonId) {
            R.id.recluta -> 0
            R.id.capitan -> 1
            R.id.veterano -> 2
            else -> 0
        }

        val estadoActivo = estado.isChecked

        // Se guarda en disco de forma asíncrona con .apply()
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SOLDADO, textoNombre)
        editor.putInt(KEY_RANGO, rangoSeleccionado)
        editor.putBoolean(KEY_ESTADO, estadoActivo)
        editor.apply()

        nombre.text.clear()

        Toast.makeText(this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun cargarDatosTacticos() {
        val soldadoGuardado = sharedPreferences.getString(KEY_SOLDADO, "")
        val rangoGuardado = sharedPreferences.getInt(KEY_RANGO, 0)
        val estadoGuardado = sharedPreferences.getBoolean(KEY_ESTADO, false)


           //Lo que hacemos es rellenar los datos del personaje creado
        nombre.setText(soldadoGuardado)

        when (rangoGuardado) {
            0 -> rango.check(R.id.recluta)
            1 -> rango.check(R.id.capitan)
            2 -> rango.check(R.id.veterano)
        }

        estado.isChecked = estadoGuardado
    }

    private fun procesarPergaminos() {
        val texto = nombre.text.toString().trim()
        if (texto.isEmpty()) {
            nombre.error = "Escribe un reporte antes de guardar"
            return
        }

        try {
            openFileOutput(FILE_INTERNO, Context.MODE_PRIVATE).use { output ->
                output.write(texto.toByteArray(Charsets.UTF_8))
            }

            val textoInterno = openFileInput(FILE_INTERNO).bufferedReader(Charsets.UTF_8).use { reader ->
                reader.readText()
            }

            var textoExterno = "Almacenamiento externo no disponible"
            val estado = Environment.getExternalStorageState()

            if (estado == Environment.MEDIA_MOUNTED) {
                val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val file = File(dir, FILE_EXTERNO)
                file.outputStream().use { output ->
                    output.write(texto.toByteArray(Charsets.UTF_8))
                }
                if (file.exists()) {
                    textoExterno = file.bufferedReader(Charsets.UTF_8).use { reader ->
                        reader.readText()
                    }
                }
            }

            nombre.text.clear()
            lectura.text = "INTERNO:\n$textoInterno\n\nEXTERNO:\n$textoExterno"
            Toast.makeText(this, "Pergaminos guardados y verificados", Toast.LENGTH_SHORT).show()

        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Archivo no encontrado", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }
}