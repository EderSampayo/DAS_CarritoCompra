package com.example.das_carritocompra;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductosFragment extends Fragment {
    private List<Producto> productos;
    private ProductoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        // Obtener las preferencias
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        // Obtener la preferencia del idioma guardada
        String idiomaSeleccionado = prefs.getString("idioma", "es"); // Por defecto, es castellano

        // Establecer el idioma de la aplicación
        Locale nuevaloc = new Locale(idiomaSeleccionado);
        Locale.setDefault(nuevaloc);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getActivity().createConfigurationContext(configuration);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Obtén el valor de preferencia de "estadoSwitch" (el segundo parámetro es el valor predeterminado si la clave no está presente)
        boolean modoOscuroActivado = prefs.getBoolean("estadoSwitch", false);
        if (modoOscuroActivado) {
            getActivity().setTheme(R.style.ModoOscuro);
        } else {
            getActivity().setTheme(R.style.ModoClaro);
        }

        // Resto del código de inicialización de la vista y configuraciones

        productos = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProductoAdapter(getActivity(), productos);
        recyclerView.setAdapter(adapter);

        // Cargar productos de la BBDD
        cargarProductosDesdeBD();

        // Resto del código de inicialización de botones y eventos

        return view;
    }

    private void cargarProductosDesdeBD() {
        // Obtener los productos de la base de datos utilizando el nuevo método obtenerProductos
        Cursor cursor = DatabaseHelper.getMiDatabaseHelper(getActivity()).getTodosLosProductos();

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

    // Resto del código de la clase
}
