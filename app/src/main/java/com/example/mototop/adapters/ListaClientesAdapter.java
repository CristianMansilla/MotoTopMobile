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
import com.example.mototop.entidades.Clientes;

import java.util.List;

public class ListaClientesAdapter extends ArrayAdapter<Clientes> {

    Context context;
    List<Clientes> clientesList;
    public ListaClientesAdapter(@NonNull Context context, List<Clientes> clientesList) {
        super(context, R.layout.lista_item_clientes, clientesList);
        this.context = context;
        this.clientesList = clientesList;
    }

    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_item_clientes, parent, false);

        TextView txtNombre = view.findViewById(R.id.txtNombre);
        TextView txtApellido = view.findViewById(R.id.txtApellido);
        TextView txtTelefono = view.findViewById(R.id.txtTelefono);


        txtNombre.setText(clientesList.get(position).getNombre());
        txtApellido.setText(clientesList.get(position).getApellido());
        txtTelefono.setText(String.valueOf(clientesList.get(position).getTelefono()));

        return view;
    }


}
