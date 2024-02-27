package com.example.das_carritocompra;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActividadUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_usuario);

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
