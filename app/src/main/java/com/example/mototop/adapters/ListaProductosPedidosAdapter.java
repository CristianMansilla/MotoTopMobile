package com.example.mototop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mototop.R;
import com.example.mototop.entidades.Productos;

import java.util.List;

public class ListaProductosPedidosAdapter extends ArrayAdapter<Productos> {
    Context context;
    List<Productos> productosList;
    public ListaProductosPedidosAdapter(@NonNull Context context, List<Productos> productosList) {
        super(context, R.layout.lista_item_productos_pedidos, productosList);
        this.context = context;
        this.productosList = productosList;
    }

    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_item_productos_pedidos, parent, false);

        TextView txtNombreProducto = view.findViewById(R.id.txtNombreProducto);


        txtNombreProducto.setText(productosList.get(position).getNombre());

        return view;
    }
}
