package com.example.das_carritocompra;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> carrito;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper databaseHelper;
    private static final int REQUEST_SAVE_FILE = 1;
    private static final int REQUEST_IMPORT_FILE = 2;
    private StringBuilder contenidoArchivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener las preferencias
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // Obtener la preferencia del idioma guardada
        String idiomaSeleccionado = prefs.getString("idioma", "es"); // Por defecto, es castellano

        // Establecer el idioma de la aplicación
        Locale nuevaloc = new Locale(idiomaSeleccionado);
        Locale.setDefault(nuevaloc);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = createConfigurationContext(configuration);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Obtén el valor de preferencia de "estadoSwitch" (el segundo parámetro es el valor predeterminado si la clave no está presente)
        boolean switchEstado = prefs.getBoolean("estadoSwitch", false);
        if (switchEstado) {
            setTheme(R.style.ModoOscuro);
        }
        else {
            setTheme(R.style.ModoClaro);
        }

        // Verificar si hay elementos en el carrito
        databaseHelper = DatabaseHelper.getMiDatabaseHelper(this);
        Cursor cartItems = databaseHelper.obtenerCarrito();

        if (cartItems.moveToFirst()) {
            // Mostrar notificación local
            mostrarNotificacion();
        }

        setContentView(R.layout.activity_main);

        // Añadir las opciones del toolbar (Carrito, Productos, Usuario)
        Toolbar toolbar = findViewById(R.id.labarra);
        setSupportActionBar(toolbar);
        // Aquí desactivamos el título en la barra
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        carrito = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carrito);

        // Cargar la BBDD en el carrito
        databaseHelper = DatabaseHelper.getMiDatabaseHelper(this);
        cartItems = databaseHelper.obtenerCarrito();

        ArrayList<String> carrito = new ArrayList<>();
        if (cartItems.moveToFirst()) {
            do {
                String producto = cartItems.getString(cartItems.getColumnIndex("producto"));
                carrito.add(producto);
            } while (cartItems.moveToNext());
        }

        comprobarRepeticionesCarrito(carrito);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carrito);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Manejo de pulsación larga para eliminar tarea
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String productoTachado = carrito.get(i); // Guarda el nombre de la tarea eliminada
                carrito.remove(i);

                // Obtener la última parte del string
                String productoAEliminar = obtenerSegundaParteDelString(productoTachado);

                // Eliminar de la base de datos
                DatabaseHelper.getMiDatabaseHelper(view.getContext()).borrarDelCarrito(productoAEliminar);

                adapter.notifyDataSetChanged();

                mostrarToast(productoTachado + " " + getString(R.string.mensaje_eliminado)); // Muestra el Toast con el mensaje
                return true;
            }
        });

        Button btnExportar = findViewById(R.id.btnExportar);
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportarCarrito();
            }
        });

        Button btnImportar = findViewById(R.id.btnImportar);
        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importarCarrito();
            }
        });

        // Verificar la orientación de la pantalla
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // La pantalla está en modo horizontal, por lo tanto, agregamos el Fragment
            agregarFragment();
        } else {
            listView = findViewById(R.id.listView);

            // Configurar parámetros de diseño para que el ListView ocupe todo el ancho
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            listView.setLayoutParams(layoutParams);

            // La pantalla está en modo vertical, por lo tanto, ocultamos el Fragment si está presente
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProductosFragment fragment = (ProductosFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);

            if (fragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(fragment);
                transaction.commit();
            }
        }
    }

    private void comprobarRepeticionesCarrito(ArrayList<String> carrito) {
        // Utilizar un HashMap para llevar un registro de la frecuencia de cada producto
        HashMap<String, Integer> frecuenciaProductos = new HashMap<>();

        // Iterar sobre el carrito y contar la frecuencia de cada producto
        for (String producto : carrito) {
            frecuenciaProductos.put(producto, frecuenciaProductos.getOrDefault(producto, 0) + 1);
        }

        // Construir una nueva lista con los productos ajustados
        ArrayList<String> carritoAjustado = new ArrayList<>();
        for (String producto : carrito) {
            int frecuencia = frecuenciaProductos.get(producto);

            // Añadir el producto solo una vez seguido por el número de repeticiones
            if (!carritoAjustado.contains(frecuencia + " x " + producto)) {
                carritoAjustado.add(frecuencia + " x " + producto);
            }
        }

        // Limpiar el carrito original y agregar los elementos ajustados
        carrito.clear();
        carrito.addAll(carritoAjustado);

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();
    }

    private String obtenerSegundaParteDelString(String pProductoTachado) {
        // Obtener la última parte del string
        String[] partes = pProductoTachado.split(" ");

        // Obtener la posición a partir de la cual deseas concatenar
        int posicionInicio = 2;

        // Verificar si la posición de inicio es válida
        String resultadoFinal = "";
        if (posicionInicio >= 0 && posicionInicio < partes.length) {
            StringBuilder resultadoConcatenado = new StringBuilder();

            // Iterar a través de las partes y concatenarlas
            for (int j = posicionInicio; j < partes.length; j++) {
                resultadoConcatenado.append(partes[j]);
                if (j < partes.length - 1) {
                    resultadoConcatenado.append(" "); // Agregar un espacio entre las partes
                }
            }
            // El resultado final
            resultadoFinal = resultadoConcatenado.toString();
        }
        return resultadoFinal;
    }

    // Método para mostrar un Toast
    private void mostrarToast(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Método para enseñar definicion_menu.xml **/
        getMenuInflater().inflate(R.menu.definicion_menu,menu);

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

    // Método para mostrar la notificación local
    private void mostrarNotificacion() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            // PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");

        // Crear el canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            elManager.createNotificationChannel(elCanal);
        }

        // Configurar el NotificationCompat.Builder
        elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle("Recordatorio")
                .setContentText("¡No te olvides de comprar los productos del carrito!")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        // Acción para ver el carrito
        Intent verIntent = new Intent(this, MainActivity.class);
        verIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        verIntent.setAction("Ver");

        // Usar FLAG_IMMUTABLE o FLAG_MUTABLE aquí
        PendingIntent verPendingIntent = PendingIntent.getActivity(this, 0, verIntent, PendingIntent.FLAG_IMMUTABLE);

        // Agregar acciones a la notificación
        elBuilder.addAction(android.R.drawable.ic_menu_revert, "Ver Carrito", verPendingIntent);

        elManager.notify(1, elBuilder.build());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "Ver":
                    // Eliminar la notificación
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.cancel(1);

                    // Iniciar la aplicación (en este caso, MainActivity)
                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(mainActivityIntent);
                    break;
            }
        }
    }

    private void exportarCarrito() {
        Cursor cartItems = databaseHelper.obtenerCarrito();

        ArrayList<String> elCarrito = new ArrayList<>();
        if (cartItems.moveToFirst()) {
            do {
                String producto = cartItems.getString(cartItems.getColumnIndex("producto"));
                elCarrito.add(producto);
            } while (cartItems.moveToNext());
        }

        // Verificar si hay elementos en el carrito
        if (elCarrito.isEmpty()) {
            mostrarToast("El carrito está vacío.");
            return;
        }

        // Crear una cadena que contendrá los elementos del carrito
        contenidoArchivo = new StringBuilder();
        for (String producto : elCarrito) {
            contenidoArchivo.append(producto).append("\n");
        }

        // Abrir el explorador de documentos para que el usuario elija la ubicación
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "carrito_exportado.txt");

        startActivityForResult(intent, REQUEST_SAVE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SAVE_FILE && resultCode == RESULT_OK && data != null) {
            // Obtener la URI del archivo seleccionado por el usuario
            Uri uri = data.getData();

            // Guardar el contenido en el archivo seleccionado
            if (guardarContenidoEnUri(contenidoArchivo.toString(), uri)) {
                mostrarToast("Carrito exportado con éxito.");
            } else {
                mostrarToast("Error al exportar el carrito.");
            }
        }

        if (requestCode == REQUEST_IMPORT_FILE && resultCode == RESULT_OK && data != null) {
            // Obtener la URI del archivo seleccionado por el usuario
            Uri uri = data.getData();

            // Leer el contenido del archivo seleccionado
            String contenidoImportado = leerContenidoDesdeUri(uri);

            // Procesar el contenido importado como desees (por ejemplo, agregarlo al carrito)
            procesarContenidoImportado(contenidoImportado);
        }
    }

    private boolean guardarContenidoEnUri(String contenido, Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(contenido);
            writer.close();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void importarCarrito() {
        // Abrir el explorador de documentos para que el usuario seleccione el archivo a importar
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, REQUEST_IMPORT_FILE);
    }

    private String leerContenidoDesdeUri(Uri uri) {
        try {
            StringBuilder contenido = new StringBuilder();
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            reader.close();
            inputStream.close();
            return contenido.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void procesarContenidoImportado(String contenidoImportado) {
        // Aquí puedes procesar el contenido importado según tus necesidades
        // Por ejemplo, agregar elementos al carrito o realizar alguna otra acción

        // Ejemplo: Dividir el contenido por líneas y agregar cada línea al carrito
        String[] lineas = contenidoImportado.split("\n");
        for (String linea : lineas) {
            // Agregar la línea al carrito (si es necesario)
            // Puedes llamar a tu método para agregar elementos al carrito aquí
            agregarAlCarrito(linea);
        }

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();

        mostrarToast("Carrito importado con éxito.");

        // Recargar la actividad para aplicar/visualizar los cambios
        finish();
        startActivity(getIntent());
    }

    private void agregarAlCarrito(String producto) {
        // Lógica para agregar elementos al carrito
        // Por ejemplo, agregar a la base de datos y actualizar la lista
        carrito.add(producto);
        DatabaseHelper.getMiDatabaseHelper(this).anadirAlCarrito(producto);
    }

    private void agregarFragment() {
        // Crea una instancia del Fragment y realiza la transacción
        ProductosFragment tuFragment = new ProductosFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, tuFragment);
        transaction.commit();
    }
}
