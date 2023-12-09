package com.example.mototop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
public class MenuActivity extends AppCompatActivity {

    CardView btnPedidos, btnComprobantes, btnClientes, btnProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Obtener el dni del intent
        Bundle recibirDni = getIntent().getExtras();
        String dni = recibirDni.getString("DNI");
        //int dni = getIntent().getIntExtra("DNI", 0);
        Log.d("MenuActivity", "DNI enviado a PedidosActivity: " + dni);

        btnPedidos = findViewById(R.id.btnPedidos);
        btnComprobantes = findViewById(R.id.btnComprobantes);
        btnClientes = findViewById(R.id.btnClientes);
        btnProductos = findViewById(R.id.btnProductos);

        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PedidosActivity.class);
                Bundle enviarDni = new Bundle();
                enviarDni.putString("DNI", dni);
                intent.putExtras(enviarDni);
                startActivity(intent);
            }
        });

        btnClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ClientesActivity.class);
                startActivity(intent);
            }
        });

        btnProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProductosActivity.class);
                startActivity(intent);
            }
        });

    }
}