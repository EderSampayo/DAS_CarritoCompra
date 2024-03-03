package com.example.das_carritocompra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> productos;
    private Context context;

    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
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
                // Obtener el nombre del producto desde la lista
                String nombreProducto = producto.getNombre();

                // Llama al método en DatabaseHelper para agregar el producto al carrito
                boolean exito = DatabaseHelper.getMiDatabaseHelper(view.getContext()).anadirAlCarrito(nombreProducto);
                if (exito) {
                    mostrarToast(nombreProducto + " " + context.getString(R.string.anadidoAlCarrito));
                }
                else {
                    mostrarToast(context.getString(R.string.errorAnadirAlCarrito));
                }
            }
        });

        holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el nombre del producto desde la lista
                String nombreProducto = producto.getNombre();

                // Crear un cuadro de diálogo de confirmación para borrar el producto
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.confirmarBorrado);
                builder.setMessage(context.getString(R.string.estasSeguroBorrado) + " " + nombreProducto + "?");

                // Configurar el botón "Sí"
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Llama al método en DatabaseHelper para borrar el producto
                        boolean exito = DatabaseHelper.getMiDatabaseHelper(view.getContext()).borrarProducto(nombreProducto);

                        if (exito) {
                            // Si se borra correctamente, actualiza la lista y notifica al adaptador
                            productos.remove(producto);
                            notifyDataSetChanged();
                            mostrarToast(nombreProducto + " " + context.getString(R.string.borradoExitoso));
                        } else {
                            mostrarToast(context.getString(R.string.errorBorrado) + " " + nombreProducto);
                        }
                    }
                });

                // Configurar el botón "No"
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // No hacer nada, simplemente cerrar el cuadro de diálogo
                    }
                });

                // Mostrar el cuadro de diálogo
                builder.show();
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
        public Button btnBorrar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textNombre);
            textViewTipo = itemView.findViewById(R.id.textTipo);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
