package com.example.das_carritocompra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActividadUsuario extends AppCompatActivity {
    private RadioGroup radioGroupIdioma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_usuario);

        Toolbar toolbar = findViewById(R.id.labarra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Inicializar RadioGroup y cargar la selección guardada en las preferencias
        radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
        cargarIdiomaSeleccionado();

        // Configurar listener para los cambios en los RadioButtons
        radioGroupIdioma.setOnCheckedChangeListener((group, checkedId) -> {
            // Guardar la selección en las preferencias
            guardarIdiomaSeleccionado(checkedId);
        });
    }

    private void cargarIdiomaSeleccionado() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int idiomaSeleccionado = preferences.getInt("IDIOMA_SELECCIONADO", R.id.radioButtonCastellano);
        radioGroupIdioma.check(idiomaSeleccionado);
    }

    private void guardarIdiomaSeleccionado(int idiomaSeleccionado) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("IDIOMA_SELECCIONADO", idiomaSeleccionado);
        editor.apply();
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
