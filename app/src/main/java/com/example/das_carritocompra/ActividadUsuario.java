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

        // Obtener las preferencias
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // Obtener la preferencia del idioma guardada
        String idiomaSeleccionado = prefs.getString("idioma", "es"); // Por defecto, es castellano

        // Obtén el valor de preferencia de "estadoSwitch" (el segundo parámetro es el valor predeterminado si la clave no está presente)
        boolean switchEstado = prefs.getBoolean("estadoSwitch", false);
        if (switchEstado) {
            setTheme(R.style.ModoOscuro);
        }
        else {
            setTheme(R.style.ModoClaro);
        }

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
        getMenuInflater().inflate(R.menu.definicion_menu, menu);

        // Obtener la referencia a los elementos del menú
        MenuItem carritoItem = menu.findItem(R.id.menu_carrito);
        MenuItem productosItem = menu.findItem(R.id.menu_productos);
        MenuItem usuarioItem = menu.findItem(R.id.menu_usuario);

        // Configurar el ícono según el modo claro u oscuro
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        boolean modoOscuroActivado = prefs.getBoolean("estadoSwitch", false);
        if (modoOscuroActivado) {
            carritoItem.setIcon(R.drawable.carrito_blanco);
            productosItem.setIcon(R.drawable.uvas_blanco);
            usuarioItem.setIcon(R.drawable.usuario_blanco);
        } else {
            carritoItem.setIcon(R.drawable.carrito);
            productosItem.setIcon(R.drawable.uvas);
            usuarioItem.setIcon(R.drawable.usuario);
        }

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
