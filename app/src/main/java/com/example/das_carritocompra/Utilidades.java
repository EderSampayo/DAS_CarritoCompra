package com.example.das_carritocompra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class Utilidades {
    public static void configurarTema(AppCompatActivity activity) {
        // Obtener las preferencias
        SharedPreferences prefs = activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        // Obtener el valor de preferencia de "estadoSwitch" (el segundo parámetro es el valor predeterminado si la clave no está presente)
        boolean modoOscuroActivado = prefs.getBoolean("estadoSwitch", false);

        if (modoOscuroActivado) {
            activity.setTheme(R.style.ModoOscuro);
        } else {
            activity.setTheme(R.style.ModoClaro);
        }
    }

    public static void cargarIdioma(AppCompatActivity activity) {
        // Obtener las preferencias
        SharedPreferences prefs = activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        // Obtener la preferencia del idioma guardada
        String idiomaSeleccionado = prefs.getString("idioma", "es"); // Por defecto, es castellano

        // Establecer idioma, en este caso el de las preferencias, no uno nuevo
        Utilidades.establecerIdiomaNuevo(activity, idiomaSeleccionado);
    }

    public static void establecerIdiomaNuevo(AppCompatActivity activity, String idiomaSeleccionado)
    {
        // Establecer el idioma de la aplicación
        Locale nuevaloc = new Locale(idiomaSeleccionado);
        Locale.setDefault(nuevaloc);

        Configuration configuration = activity.getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = activity.createConfigurationContext(configuration);
        activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
    }

    public static void configurarToolbar(AppCompatActivity activity, Toolbar toolbar) {
        // Añadir las opciones del toolbar (Carrito, Productos, Usuario)
        activity.setSupportActionBar(toolbar);

        // Aquí desactivamos el título en la barra
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public static void configurarIconosMenu(AppCompatActivity activity, Menu menu) {
        SharedPreferences prefs = activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean modoOscuroActivado = prefs.getBoolean("estadoSwitch", false);

        // Obtener la referencia a los elementos del menú
        MenuItem carritoItem = menu.findItem(R.id.menu_carrito);
        MenuItem productosItem = menu.findItem(R.id.menu_productos);
        MenuItem usuarioItem = menu.findItem(R.id.menu_usuario);

        if (modoOscuroActivado) {
            carritoItem.setIcon(R.drawable.carrito_blanco);
            productosItem.setIcon(R.drawable.uvas_blanco);
            usuarioItem.setIcon(R.drawable.usuario_blanco);
        } else {
            carritoItem.setIcon(R.drawable.carrito);
            productosItem.setIcon(R.drawable.uvas);
            usuarioItem.setIcon(R.drawable.usuario);
        }
    }

    public static boolean manejarItemClick(MenuItem item, AppCompatActivity activity) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_carrito) {
            // Iniciar MainActivity si no estamos ya en ella
            if (!(activity instanceof MainActivity)) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                return true;
            }
        } else if (itemId == R.id.menu_productos) {
            // Iniciar ActividadProductos si no estamos ya en ella
            if (!(activity instanceof ActividadProductos)) {
                activity.startActivity(new Intent(activity, ActividadProductos.class));
                return true;
            }
        } else if (itemId == R.id.menu_usuario) {
            // Iniciar ActividadUsuario si no estamos ya en ella
            if (!(activity instanceof ActividadUsuario)) {
                activity.startActivity(new Intent(activity, ActividadUsuario.class));
                return true;
            }
        }

        return false;
    }

    public static void mostrarToast(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
