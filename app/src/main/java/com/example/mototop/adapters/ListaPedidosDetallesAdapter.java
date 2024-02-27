package com.example.mototop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mototop.R;
import com.example.mototop.entidades.ListaPedidos;
import com.example.mototop.entidades.Pedidos;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ListaPedidosDetallesAdapter extends ArrayAdapter<ListaPedidos> {
    Context context;
    List<ListaPedidos> listaPedidosList;
    public ListaPedidosDetallesAdapter(@NonNull Context context, List<ListaPedidos> listaPedidosList) {
        super(context, R.layout.lista_item_productos_detalle, listaPedidosList);
        this.context = context;
        this.listaPedidosList = listaPedidosList;
    }

    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_item_productos_detalle, parent, false);

        TextView txtNombreProducto = view.findViewById(R.id.txtNombreProductoDetalle);
        TextView txtCantidadProducto = view.findViewById(R.id.txtCantidadProductoDetalle);
        TextView txtPrecioProducto = view.findViewById(R.id.txtPrecioProductoDetalle);


        txtNombreProducto.setText(listaPedidosList.get(position).getNombre());
        txtCantidadProducto.setText(String.valueOf(listaPedidosList.get(position).getCantidad()));
        txtPrecioProducto.setText(String.valueOf(listaPedidosList.get(position).getPrecio()));

        return view;
    }
}
