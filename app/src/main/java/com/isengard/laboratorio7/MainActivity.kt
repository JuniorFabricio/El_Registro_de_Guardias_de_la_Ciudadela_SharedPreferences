package com.isengard.laboratorio7

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PREFS_NAME = "RegistroGuardia"
        private const val KEY_SOLDADO = "KEY_SOLDADO"
        private const val KEY_RANGO = "KEY_RANGO"
        private const val KEY_ESTADO = "KEY_ESTADO"
    }
    private lateinit var nombre: EditText
    private lateinit var rango: RadioGroup
    private lateinit var estado: SwitchCompat
    private lateinit var btn: Button
    private lateinit var sharedPreferences: SharedPreferences
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

        // Validación defensiva: el nombre no puede estar vacío
        if (textoNombre.isEmpty()) {
            nombre.error = "Introduce el nombre del efectivo"
            return
        }

        // Mapeo de tus RadioButtons (0, 1, 2)
        val rangoSeleccionado = when (rango.checkedRadioButtonId) {
            R.id.recluta -> 0
            R.id.capitan -> 1
            R.id.veterano -> 2
            else -> 0
        }

        val estadoActivo = estado.isChecked

        // Guardado en disco de forma asíncrona con .apply()
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SOLDADO, textoNombre)
        editor.putInt(KEY_RANGO, rangoSeleccionado)
        editor.putBoolean(KEY_ESTADO, estadoActivo)
        editor.apply()

        // Control defensivo: limpiar EditText tras ejecutar .apply()
        nombre.text.clear()

        Toast.makeText(this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun cargarDatosTacticos() {
        val soldadoGuardado = sharedPreferences.getString(KEY_SOLDADO, "")
        val rangoGuardado = sharedPreferences.getInt(KEY_RANGO, 0)
        val estadoGuardado = sharedPreferences.getBoolean(KEY_ESTADO, false)

        // Rellenar la interfaz con los datos recuperados
        nombre.setText(soldadoGuardado)

        when (rangoGuardado) {
            0 -> rango.check(R.id.recluta)
            1 -> rango.check(R.id.capitan)
            2 -> rango.check(R.id.veterano)
        }

        estado.isChecked = estadoGuardado
    }
}