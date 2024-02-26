package com.example.das_carritocompra;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> carrito;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Añadir las opciones del toolbar (Carrito, Productos, Usuario)
        Toolbar toolbar = findViewById(R.id.labarra);
        setSupportActionBar(toolbar);
        // Aquí desactivamos el título en la barra
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        carrito = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carrito);

        // Cargar la BBDD en el carrito
        DatabaseHelper databaseHelper = DatabaseHelper.getMiDatabaseHelper(this);
        Cursor cartItems = databaseHelper.obtenerCarrito();

        ArrayList<String> carrito = new ArrayList<>();
        if (cartItems.moveToFirst()) {
            do {
                String producto = cartItems.getString(cartItems.getColumnIndex("producto"));
                carrito.add(producto);
            } while (cartItems.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carrito);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Manejo de pulsación larga para eliminar tarea
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String productoTachado = carrito.get(i); // Guarda el nombre de la tarea eliminada
                carrito.remove(i);

                // Eliminar de la base de datos
                DatabaseHelper.getMiDatabaseHelper(view.getContext()).borrarDelCarrito(productoTachado);

                adapter.notifyDataSetChanged();

                mostrarToast(productoTachado + " eliminado"); // Muestra el Toast con el mensaje
                return true;
            }
        });
    }

    // Método para mostrar un Toast
    private void mostrarToast(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Método para enseñar definicion_menu.xml **/
        getMenuInflater().inflate(R.menu.definicion_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_carrito) {
            // No hace nada, es la propia clase
            return true;
        } else if (itemId == R.id.menu_productos) {
            // Iniciar Actividad 2
            startActivity(new Intent(this, ActividadProductos.class));
            return true;
        } else if (itemId == R.id.menu_usuario) {
            // Iniciar Actividad 3
            startActivity(new Intent(this, ActividadUsuario.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
