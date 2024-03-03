package com.example.das_carritocompra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class ActividadUsuario extends AppCompatActivity {
    private RadioGroup radioGroupIdioma;
    private TextView textViewIdioma;
    private Switch switchEstilo;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el idioma de la aplicación
        Utilidades.cargarIdioma(this);

        // Configurar modo claro/oscuro
        Utilidades.configurarTema(this);

        // Continuar con la creación de la actividad
        setContentView(R.layout.actividad_usuario);

        radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
        RadioButton radioButtonCastellano = findViewById(R.id.radioButtonCastellano);
        RadioButton radioButtonIngles = findViewById(R.id.radioButtonIngles);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String idiomaSeleccionado = prefs.getString("idioma", "es"); // Por defecto, es castellano
        if (idiomaSeleccionado.equals("es")) {
            radioButtonCastellano.setChecked(true);
        } else {
            radioButtonIngles.setChecked(true);
        }

        radioGroupIdioma.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String nuevoIdioma = (checkedId == R.id.radioButtonCastellano) ? "es" : "en";

                // Guardar la preferencia compartida para el nuevo idioma
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("idioma", nuevoIdioma);
                editor.apply();

                // Actualizar la configuración de la aplicación
                Utilidades.establecerIdiomaNuevo(ActividadUsuario.this, nuevoIdioma);

                // Recargar la actividad para aplicar los cambios de localización
                finish();
                startActivity(getIntent());
            }
        });

        // Obtén una referencia al Switch en el layout
        Switch switchEstilo = findViewById(R.id.switchEstilo);

        // Configura un listener para el cambio de estado del Switch
        switchEstilo.setChecked(prefs.getBoolean("estadoSwitch", false)); // Cargar el estado del Switch

        // Configura un listener para el cambio de estado del Switch
        switchEstilo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar el estado del Switch
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("estadoSwitch", isChecked);
                editor.apply();

                // Reiniciar la actividad para aplicar el nuevo estilo
                finish();
                startActivity(getIntent());
            }
        });


        Toolbar toolbar = findViewById(R.id.labarra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Método para enseñar definicion_menu.xml **/
        getMenuInflater().inflate(R.menu.definicion_menu, menu);

        // Configurar el ícono según el modo claro u oscuro
        Utilidades.configurarIconosMenu(this, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // El || hace que si alguno de los 2 devuelve true se devuelva true, si no se devuelve false
        return Utilidades.manejarItemClick(item, this) || super.onOptionsItemSelected(item);
    }
}
