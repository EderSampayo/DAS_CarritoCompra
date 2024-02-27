package com.example.das_carritocompra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class ActividadUsuario extends AppCompatActivity {
    private RadioGroup radioGroupIdioma;
    private TextView textViewIdioma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener la preferencia del idioma guardada
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String idiomaSeleccionado = prefs.getString("idioma", "es"); // Por defecto, es español

        // Establecer el idioma de la aplicación
        Locale nuevaloc = new Locale(idiomaSeleccionado);
        Locale.setDefault(nuevaloc);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = createConfigurationContext(configuration);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Continuar con la creación de la actividad
        setContentView(R.layout.actividad_usuario);

        radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
        RadioButton radioButtonCastellano = findViewById(R.id.radioButtonCastellano);
        RadioButton radioButtonIngles = findViewById(R.id.radioButtonIngles);

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
                // Utilizar código para cambiar la localización
                Locale nuevaloc = new Locale(nuevoIdioma);
                Locale.setDefault(nuevaloc);

                Configuration configuration = getBaseContext().getResources().getConfiguration();
                configuration.setLocale(nuevaloc);
                configuration.setLayoutDirection(nuevaloc);

                Context context = getBaseContext().createConfigurationContext(configuration);
                getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

                // Recargar la actividad para aplicar los cambios de localización
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
        getMenuInflater().inflate(R.menu.definicion_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_carrito) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (itemId == R.id.menu_productos) {
            startActivity(new Intent(this, ActividadProductos.class));
            return true;
        } else if (itemId == R.id.menu_usuario) {
            // No hace nada, ya estamos en la actividad de usuario
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
