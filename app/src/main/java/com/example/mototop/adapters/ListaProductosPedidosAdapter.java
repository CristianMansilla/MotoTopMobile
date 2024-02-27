package com.example.mototop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.mototop.R;
import com.example.mototop.entidades.Productos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListaProductosPedidosAdapter extends ArrayAdapter<Productos> {
    Context context;
    List<Productos> productosList;
    private List<Productos> productosListFull;  // Lista original sin filtrar

    private int cantidadTotalProductos = 0;
    public ListaProductosPedidosAdapter(@NonNull Context context, List<Productos> productosList , List<Productos> productosListFull) {
        super(context, R.layout.lista_item_productos_pedidos, productosList);
        this.context = context;
        this.productosList = productosList;
        this.productosListFull = new ArrayList<>(productosList);  // Copia de la lista original
    }

    public static class ViewHolder {
        public TextView txtNombreProducto;
        public EditText txtCantidadProducto;
        FloatingActionButton fabAumentar;
        FloatingActionButton fabDisminuir;
    }

    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.lista_item_productos_pedidos, parent, false);

            holder = new ViewHolder();
            holder.txtNombreProducto = view.findViewById(R.id.txtNombreProducto);
            holder.txtCantidadProducto = view.findViewById(R.id.txtCantidadProducto);
            holder.fabAumentar = view.findViewById(R.id.fabAumentarProducto);
            holder.fabDisminuir = view.findViewById(R.id.fabDisminuirProducto);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Obtén el objeto Productos para esta posición
        Productos productos = getItem(position);

        // Asigna el nombre del producto al TextView correspondiente
        if (productos != null) {
            holder.txtNombreProducto.setText(productos.getNombre());
            holder.txtCantidadProducto.setText(String.valueOf(productos.getCantidadActual()));

            // Verifica si la cantidad seleccionada supera el stock disponible
            if (productos.getCantidadActual() > productos.getStock()) {
                // Cambia el color del EditText
                holder.txtCantidadProducto.setTextColor(ContextCompat.getColor(context, R.color.colorExceededStock));
            } else {
                // Restaura el color predeterminado del EditText
                holder.txtCantidadProducto.setTextColor(ContextCompat.getColor(context, R.color.blue));
            }
        }

        // Asigna OnClickListener a los botones con referencias al EditText correspondiente
        holder.fabAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aumentarCantidad(productos);
                notifyDataSetChanged(); // Notificar cambios en el conjunto de datos
            }
        });

        holder.fabDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disminuirCantidad(productos);
                notifyDataSetChanged(); // Notificar cambios en el conjunto de datos
            }
        });

        return view;
    }

    private void aumentarCantidad(Productos productos) {
        // Incrementar la cantidad en el modelo
        productos.setCantidadActual(productos.getCantidadActual() + 1);
        // Incrementar la cantidad total
        cantidadTotalProductos++;
    }

    private void disminuirCantidad(Productos productos) {
        // Asegurarse de que la cantidad no sea negativa
        if (productos.getCantidadActual() > 0) {
            // Disminuir la cantidad en el modelo
            productos.setCantidadActual(productos.getCantidadActual() - 1);
            // Disminuir la cantidad total
            cantidadTotalProductos--;
        }
    }

    public int getCantidadTotalProductos() {
        return cantidadTotalProductos;
    }

    public double getTotalProductos() {
        double total = 0.0;

        for (Productos producto : productosList) {
            // Realiza el cálculo del total de cada producto (cantidad * precio)
            total += producto.getPrecio() * producto.getCantidadActual();
        }

        return total;
    }

    // Método para filtrar la lista según el texto ingresado
    public void filterList(List<Productos> filteredList) {
        productosList.clear();
        productosList.addAll(filteredList);
        notifyDataSetChanged();
    }
}
