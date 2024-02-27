package com.example.das_carritocompra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActividadUsuario extends AppCompatActivity {
    private RadioGroup radioGroupIdioma;
    private TextView textViewIdioma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_usuario);

        Toolbar toolbar = findViewById(R.id.labarra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewIdioma = findViewById(R.id.textViewIdioma);

        // Cambiar el texto del TextView según el idioma seleccionado
        RadioButton radioButtonCastellano = findViewById(R.id.radioButtonCastellano);
        RadioButton radioButtonIngles = findViewById(R.id.radioButtonIngles);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int idiomaSeleccionado = preferences.getInt("IDIOMA_SELECCIONADO", R.id.radioButtonCastellano);

        if (idiomaSeleccionado == R.id.radioButtonIngles) {
            textViewIdioma.setText("Language");
            radioButtonCastellano.setText("Spanish");
            radioButtonIngles.setText("English");
        } else {
            textViewIdioma.setText("Idioma");
            radioButtonCastellano.setText("Castellano");
            radioButtonIngles.setText("Inglés");
        }

        // Configurar listener para los cambios en los RadioButtons
        radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
        radioGroupIdioma.setOnCheckedChangeListener((group, checkedId) -> {
            // Guardar la selección en las preferencias
            guardarIdiomaSeleccionado(checkedId);

            // Cambiar el texto del TextView según el idioma seleccionado
            if (checkedId == R.id.radioButtonIngles) {
                textViewIdioma.setText("Language");
                radioButtonCastellano.setText("Spanish");
                radioButtonIngles.setText("English");
            } else {
                textViewIdioma.setText("Idioma");
                radioButtonCastellano.setText("Castellano");
                radioButtonIngles.setText("Inglés");
            }
        });

        // Restaurar la selección previa después de configurar el listener
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
