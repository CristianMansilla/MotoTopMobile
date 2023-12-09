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
import com.example.mototop.entidades.Pedidos;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ListaPedidosAdapter extends ArrayAdapter<Pedidos> {
    Context context;
    List<Pedidos> pedidosList;
    public ListaPedidosAdapter(@NonNull Context context, List<Pedidos> pedidosList) {
        super(context, R.layout.lista_item_pedidos, pedidosList);
        this.context = context;
        this.pedidosList = pedidosList;
    }

    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_item_pedidos, parent, false);

        TextView txtNumeroPedido = view.findViewById(R.id.txtNumeroPedido);
        TextView txtNombreClientePedido = view.findViewById(R.id.txtNombreClientePedido);
        TextView txtDniClientePedido = view.findViewById(R.id.txtDniClientePedido);
        TextView txtPrecioPedido = view.findViewById(R.id.txtPrecioPedido);
        TextView txtFechaGeneradoPedido = view.findViewById(R.id.txtFechaGeneradoPedido);

        txtNumeroPedido.setText(String.valueOf(pedidosList.get(position).getID()));
        txtNombreClientePedido.setText(pedidosList.get(position).getCliente_nombre());
        txtDniClientePedido.setText(String.valueOf(pedidosList.get(position).getCliente_DNI()));
        txtPrecioPedido.setText(String.valueOf(pedidosList.get(position).getPrecio_total()));
        // Formatear la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaFormateada = dateFormat.format(pedidosList.get(position).getFecha_generado());
        txtFechaGeneradoPedido.setText(fechaFormateada);

        return view;
    }
}
