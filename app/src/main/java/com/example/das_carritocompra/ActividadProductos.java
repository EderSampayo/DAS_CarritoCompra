package com.example.das_carritocompra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActividadProductos extends AppCompatActivity {
    private List<Producto> productos;
    private ProductoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el idioma de la aplicación
        Utilidades.cargarIdioma(this);

        // Configurar modo claro/oscuro
        Utilidades.configurarTema(this);

        // Añadir al contentView actividad_productos.xml
        setContentView(R.layout.actividad_productos);

        // Añadir las opciones del toolbar (Carrito, Productos, Usuario)
        Toolbar toolbar = findViewById(R.id.labarra);
        Utilidades.configurarToolbar(this, toolbar);

        productos = new ArrayList<>();

        // Añadir LinearLayoutManager al RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Añadir adapter al RecyclerView
        adapter = new ProductoAdapter(this, productos);
        recyclerView.setAdapter(adapter);

        // Cargar productos de la BBDD
        cargarProductosDesdeBD();

        Button btnAnadirProducto = findViewById(R.id.btnAnadirProducto);
        btnAnadirProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogoAnadirProducto(productos);
            }
        });

        Button btnEditarProducto = findViewById(R.id.btnEditarProducto);
        btnEditarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogoEditarProducto(productos);
            }
        });
    }






    private void cargarProductosDesdeBD() {
        // Obtener los productos de la base de datos
        Cursor cursor = DatabaseHelper.getMiDatabaseHelper(this).getTodosLosProductos();

        // Limpiar la lista actual de productos
        productos.clear();

        // Iterar a través de los resultados del cursor y agregar productos a la lista
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
            productos.add(new Producto(nombre, tipo));
        }

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();

        // Cerrar el cursor
        cursor.close();
    }

    private void abrirDialogoAnadirProducto(List<Producto> productos) {
        // Crear un cuadro de diálogo de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.btnAnadirProductoTexto);

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
        String[] opciones = {
                getString(R.string.Carbohidrato),
                getString(R.string.Proteina),
                getString(R.string.Grasa),
                getString(R.string.Otro)
        };
        for (int i = 0; i < opciones.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opciones[i]);
            radioGroup.addView(radioButton);

            // Seleccionar "Carbohidrato" por defecto
            if (opciones[i].equals(R.string.Carbohidrato)) {
                radioButton.setChecked(true);
            }
        }
        // Agregar el grupo de radio buttons al layout
        layout.addView(radioGroup);

        // Establecer el layout personalizado en el cuadro de diálogo
        builder.setView(layout);

        // Configurar el botón "Añadir"
        builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el texto ingresado
                String nombreProducto = input.getText().toString();

                // Validar que el texto no esté vacío
                if (!nombreProducto.isEmpty()) {
                    // Obtener el tipo seleccionado
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonId);
                    String tipoSeleccionado = radioButton.getText().toString();

                    Producto elProducto = new Producto(nombreProducto, tipoSeleccionado);
                    productos.add(elProducto);

                    // Añadir el producto a la BBDD
                    DatabaseHelper.getMiDatabaseHelper(ActividadProductos.this).anadirProducto(nombreProducto, tipoSeleccionado);

                    // Mostrar el mensaje Toast
                    Utilidades.mostrarToast(getApplicationContext(),nombreProducto + " " + getString(R.string.anadido));
                } else {
                    Utilidades.mostrarToast(getApplicationContext(), getString(R.string.errorAnadir));
                }
            }
        });

        // Configurar el botón "Cancelar"
        builder.setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cerrar el cuadro de diálogo
            }
        });

        // Mostrar el cuadro de diálogo de alerta
        builder.show();
    }

    private void abrirDialogoEditarProducto(List<Producto> productos) {
        // Obtener la lista de nombres de productos para mostrar en el Spinner
        String[] nombresProductos = obtenerNombresProductos();

        // Crear un cuadro de diálogo de alerta con lista de selección
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.btnEditarProductoTexto);

        // Configurar el layout del cuadro de diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Crear el Spinner
        Spinner spinnerProductos = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresProductos);
        spinnerProductos.setAdapter(adapter);
        layout.addView(spinnerProductos);

        // Crear un EditText para el nuevo nombre del producto
        final EditText nuevoNombreEditText = new EditText(this);
        nuevoNombreEditText.setHint(R.string.nuevoNombre);
        layout.addView(nuevoNombreEditText);

        // Crear el grupo de radio buttons
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // Crear los radio buttons
        String[] opciones = {
                getString(R.string.Carbohidrato),
                getString(R.string.Proteina),
                getString(R.string.Grasa),
                getString(R.string.Otro)
        };
        for (int i = 0; i < opciones.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opciones[i]);
            radioGroup.addView(radioButton);
        }
        // Agregar el grupo de radio buttons al layout
        layout.addView(radioGroup);

        // Establecer el layout personalizado en el cuadro de diálogo
        builder.setView(layout);

        // Configurar el botón "Aceptar"
        builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el nombre del producto seleccionado
                String nombreProductoSeleccionado = spinnerProductos.getSelectedItem().toString();

                // Obtener el nuevo nombre del producto
                String nuevoNombreProducto = nuevoNombreEditText.getText().toString().trim();

                // Validar que se haya seleccionado un producto y se haya ingresado un nuevo nombre
                if (!nombreProductoSeleccionado.isEmpty() && !nuevoNombreProducto.isEmpty()) {
                    // Obtener el tipo seleccionado
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonId);
                    String tipoSeleccionado = radioButton.getText().toString();

                    // Actualizar el producto en la lista y en la base de datos
                    actualizarProducto(nombreProductoSeleccionado, nuevoNombreProducto, tipoSeleccionado);

                    // Mostrar el mensaje Toast
                    Utilidades.mostrarToast(getApplicationContext(),nombreProductoSeleccionado + " " + getString(R.string.actualizadoA) + " " + nuevoNombreProducto);

                    // Cerrar el diálogo
                    dialog.dismiss();
                } else {
                    Utilidades.mostrarToast(getApplicationContext(), getString(R.string.errorEditar));
                }
            }
        });

        // Configurar el botón "Cancelar"
        builder.setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cerrar el cuadro de diálogo
            }
        });

        // Mostrar el cuadro de diálogo de alerta
        builder.show();
    }

    // Método para actualizar un producto en la lista y en la base de datos
    private void actualizarProducto(String nombreAntiguo, String nuevoNombre, String tipo) {
        // Actualizar en la lista
        for (Producto producto : productos) {
            if (producto.getNombre().equals(nombreAntiguo)) {
                producto.setNombre(nuevoNombre);
                producto.setTipo(tipo);
                break;
            }
        }

        // Actualizar en la base de datos
        DatabaseHelper.getMiDatabaseHelper(ActividadProductos.this).actualizarProducto(nombreAntiguo, nuevoNombre, tipo);

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();
    }

    // Método para obtener los nombres de todos los productos
    private String[] obtenerNombresProductos() {
        List<String> nombresProductos = new ArrayList<>();
        for (Producto producto : productos) {
            nombresProductos.add(producto.getNombre());
        }
        return nombresProductos.toArray(new String[0]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Método para enseñar definicion_menu.xml **/
        getMenuInflater().inflate(R.menu.definicion_menu,menu);

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
