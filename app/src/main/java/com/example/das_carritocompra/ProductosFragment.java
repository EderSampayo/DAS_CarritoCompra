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

import androidx.appcompat.app.AppCompatActivity;
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

        // Establecer el idioma de la aplicación
        Utilidades.cargarIdioma((AppCompatActivity) getActivity());

        // Configurar modo claro/oscuro
        Utilidades.configurarTema((AppCompatActivity) getActivity());

        productos = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProductoAdapter(getActivity(), productos);
        recyclerView.setAdapter(adapter);

        // Cargar productos de la BBDD
        cargarProductosDesdeBD();

        return view;
    }

    private void cargarProductosDesdeBD() {
        // Obtener los productos de la base de datos
        Cursor cursor = DatabaseHelper.getMiDatabaseHelper(getActivity()).getTodosLosProductos();

        // Limpiar la lista actual de productos
        productos.clear();

        // Iterar a través de los resultados del cursor y agregar productos a la lista
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
            productos.add(new Producto(nombre, tipo));
        }

        adapter.notifyDataSetChanged();

        cursor.close();
    }
}
