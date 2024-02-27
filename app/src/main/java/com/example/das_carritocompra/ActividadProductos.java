package com.example.das_carritocompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class ActividadProductos extends AppCompatActivity {
    private List<Producto> productos;
    private ProductoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_productos);

        // Añadir las opciones del toolbar (Carrito, Productos, Usuario)
        Toolbar toolbar = findViewById(R.id.labarra);
        setSupportActionBar(toolbar);
        // Aquí desactivamos el título en la barra
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        productos = new ArrayList<>();

        //productos.add(new Producto("Pera", "Carbohidrato"));
        //productos.add(new Producto("Carne de vaca", "Proteína"));
        //productos.add(new Producto("Cacahuete", "Grasa"));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProductoAdapter(getApplicationContext(), productos);
        recyclerView.setAdapter(adapter);

        // Cargar productos de la BBDD
        cargarProductosDesdeBD();

        Button addButton = findViewById(R.id.btnAnadirProducto);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTaskActivity(productos);
            }
        });

        Button editButton = findViewById(R.id.btnEditarProducto);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditProductDialog(productos);
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

                    ProductoAdapter adapter = new ProductoAdapter(getApplicationContext(), productos);
                    //recyclerView.setAdapter(adapter);

                    // Obtener el tipo seleccionado
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonId);
                    String tipoSeleccionado = radioButton.getText().toString();

                    Producto elProducto = new Producto(nombreProducto, tipoSeleccionado);
                    productos.add(elProducto);

                    // Añadir el producto a la BBDD
                    DatabaseHelper.getMiDatabaseHelper(ActividadProductos.this).anadirProducto(nombreProducto, tipoSeleccionado);

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

    private void openEditProductDialog(List<Producto> productos) {
        // Obtener la lista de nombres de productos para mostrar en el Spinner
        String[] nombresProductos = obtenerNombresProductos();

        // Crear un cuadro de diálogo de alerta con lista de selección
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Producto");

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
        nuevoNombreEditText.setHint("Nuevo Nombre");
        layout.addView(nuevoNombreEditText);

        // Crear el grupo de radio buttons
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // Crear los radio buttons
        String[] opciones = {"Carbohidrato", "Proteína", "Grasa", "Otro"};
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
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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
                    mostrarToast(nombreProductoSeleccionado + " actualizado a " + nuevoNombreProducto);

                    // Cerrar el diálogo
                    dialog.dismiss();
                } else {
                    mostrarToast("Por favor, selecciona un producto y proporciona un nuevo nombre");
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_carrito) {
            // Iniciar Actividad 1
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (itemId == R.id.menu_productos) {
            // No hace nada, es la propia clase
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
