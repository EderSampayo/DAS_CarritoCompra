package com.example.das_carritocompra;

import android.os.Bundle;

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
    }
}
