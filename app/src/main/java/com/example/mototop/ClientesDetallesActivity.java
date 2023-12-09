package com.example.mototop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ClientesDetallesActivity extends AppCompatActivity {
    EditText edtDetalleDni, edtDetalleNombre, edtDetalleApellido, edtDetalleTelefono, edtDetalleCorreo, edtDetalleDomicilio, edtDetalleDireccionEntrega, edtDetalleDebe;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_detalles);
        edtDetalleDni = findViewById(R.id.edtDetalleDni);
        edtDetalleNombre = findViewById(R.id.edtDetalleNombre);
        edtDetalleApellido = findViewById(R.id.edtDetalleApellido);
        edtDetalleTelefono = findViewById(R.id.edtDetalleTelefono);
        edtDetalleCorreo = findViewById(R.id.edtDetalleCorreo);
        edtDetalleDomicilio = findViewById(R.id.edtDetalleDomicilio);
        edtDetalleDireccionEntrega = findViewById(R.id.edtDetalleDireccionEntrega);
        edtDetalleDebe = findViewById(R.id.edtDetalleDebe);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");
        Integer dniValue = ClientesActivity.clientesArrayList.get(position).getDNI();
        if (dniValue != null) {
            Log.d("DNI_Value", String.valueOf(dniValue));
            edtDetalleDni.setText("DNI: " + String.valueOf(dniValue));
        } else {
            edtDetalleDni.setText("");
        }
        edtDetalleNombre.setText("Nombre: " + ClientesActivity.clientesArrayList.get(position).getNombre());
        edtDetalleApellido.setText("Apellido: " + ClientesActivity.clientesArrayList.get(position).getApellido());
        Long telefonoValue = ClientesActivity.clientesArrayList.get(position).getTelefono();
        if (telefonoValue != null) {
            edtDetalleTelefono.setText("Teléfono: " + String.valueOf(telefonoValue));
        } else {
            edtDetalleTelefono.setText("");
        }
        edtDetalleCorreo.setText("Correo: " + ClientesActivity.clientesArrayList.get(position).getCorreo());
        edtDetalleDomicilio.setText("Domicilio: " + ClientesActivity.clientesArrayList.get(position).getDomicilio());
        edtDetalleDireccionEntrega.setText("Dirección de entrega: " + ClientesActivity.clientesArrayList.get(position).getDireccion_entrega());
        Double debeValue = ClientesActivity.clientesArrayList.get(position).getDebe();
        if (debeValue != null) {
            edtDetalleDebe.setText("Debe: " + String.valueOf(debeValue));
        } else {
            edtDetalleDebe.setText("");
        }
    }
}