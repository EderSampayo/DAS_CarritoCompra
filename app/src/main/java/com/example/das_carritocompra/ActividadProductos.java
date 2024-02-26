package com.example.das_carritocompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActividadProductos extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_productos);

        List<Producto> productos = new ArrayList<>();
        productos.add(new Producto("Pera", "Carbohidrato"));
        productos.add(new Producto("Carne de vaca", "Proteína"));
        productos.add(new Producto("Cacahuete", "Grasa"));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ProductoAdapter adapter = new ProductoAdapter(productos);
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.btnAnadirProducto);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTaskActivity(productos);
            }
        });
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void openAddTaskActivity(List<Producto> productos) {
        // Crear un cuadro de diálogo de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir Producto");

        // Configurar el diseño del cuadro de diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Agregar EditText al layout
        final EditText input = new EditText(this);
        layout.addView(input);

        // Crear el grupo de radio buttons
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // Crear los radio buttons
        String[] opciones = {"Carbohidrato", "Proteína", "Grasa", "Otro"};
        for (int i = 0; i < opciones.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opciones[i]);
            radioGroup.addView(radioButton);

            // Seleccionar "Carbohidrato" por defecto
            if (opciones[i].equals("Carbohidrato")) {
                radioButton.setChecked(true);
            }
        }
        // Agregar el grupo de radio buttons al layout
        layout.addView(radioGroup);

        // Establecer el layout personalizado en el cuadro de diálogo
        builder.setView(layout);

        // Configurar el botón "Añadir"
        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el texto ingresado
                String nombreProducto = input.getText().toString();

                // Validar que el texto no esté vacío
                if (!nombreProducto.isEmpty()) {
                    // Agregar el nuevo elemento a la lista

                    ProductoAdapter adapter = new ProductoAdapter(productos);
                    //recyclerView.setAdapter(adapter);

                    // Obtener el tipo seleccionado
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonId);
                    String tipoSeleccionado = radioButton.getText().toString();

                    Producto elProducto = new Producto(nombreProducto, tipoSeleccionado);
                    productos.add(elProducto);

                    // Mostrar el mensaje Toast
                    mostrarToast(nombreProducto + " añadido");
                } else {
                    mostrarToast("Por favor, ingresa un texto");
                }
            }
        });

        // Configurar el botón "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cerrar el cuadro de diálogo
            }
        });

        // Mostrar el cuadro de diálogo de alerta
        builder.show();
    }
}
