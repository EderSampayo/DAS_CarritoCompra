package com.example.das_carritocompra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> carrito;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carrito = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carrito);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button addButton = findViewById(R.id.btnAnadirProducto);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTaskActivity();
            }
        });

        // Manejo de pulsación larga para eliminar tarea
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String productoTachado = carrito.get(i); // Guarda el nombre de la tarea eliminada
                carrito.remove(i);
                adapter.notifyDataSetChanged();

                mostrarToast(productoTachado + " eliminado"); // Muestra el Toast con el mensaje
                return true;
            }
        });
    }

    // Método para abrir la segunda actividad
    private void openAddTaskActivity() {
        // Abrir el diálogo
        // TODO
    }

    // Método para mostrar un Toast
    private void mostrarToast(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
