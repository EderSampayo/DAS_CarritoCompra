package com.example.das_carritocompra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> productos;

    public ProductoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_productos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);

        holder.textViewNombre.setText(producto.getNombre());
        holder.textViewTipo.setText(producto.getTipo());

        // Manejar clic en el botón de suma
        holder.btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llama al método en DatabaseHelper para agregar el producto al carrito

                // Obtener el nombre del producto desde la lista
                String nombreProducto = producto.getNombre();

                // Llama al método en DatabaseHelper para agregar el producto al carrito
                DatabaseHelper.getMiDatabaseHelper(view.getContext()).anadirAlCarrito(nombreProducto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNombre;
        public TextView textViewTipo;
        public ImageButton btnAgregar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textNombre);
            textViewTipo = itemView.findViewById(R.id.textTipo);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }
}
