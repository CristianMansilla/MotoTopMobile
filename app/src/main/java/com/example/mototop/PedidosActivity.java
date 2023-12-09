package com.example.mototop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mototop.adapters.ListaPedidosAdapter;
import com.example.mototop.adapters.ListaProductosAdapter;
import com.example.mototop.entidades.Clientes;
import com.example.mototop.entidades.Pedidos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PedidosActivity extends AppCompatActivity {
    ListView listViewPedidos;
    ListaPedidosAdapter listaPedidosAdapter;

    public static ArrayList<Pedidos> pedidosArrayList = new ArrayList<>();
    private static final int CODIGO_DE_SOLICITUD = 1;

    Pedidos pedidos;

    int dni;
    int dniVendedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtener el dni del intent
        Bundle recibirDni = getIntent().getExtras();
        String dni = recibirDni.getString("DNI");
        Log.d("PedidosActivity", "DNI obtenido: " + dni);

        dniVendedor = Integer.parseInt(dni);
        listViewPedidos = findViewById(R.id.listViewPedidos);
        listaPedidosAdapter = new ListaPedidosAdapter(this, pedidosArrayList);
        listViewPedidos.setAdapter(listaPedidosAdapter);
        listarPedidos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void abrirAgregarPedido(View view){
        Intent intent = new Intent(PedidosActivity.this, PedidosAgregarActivity.class);
        startActivityForResult(intent, CODIGO_DE_SOLICITUD);
    }

    public void listarPedidos(){
        String url = "http://192.168.56.1/ws_mototop/pedidos/mostrar.php?dni=" + dniVendedor;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pedidosArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String exito = jsonObject.getString("exito");
                    JSONArray jsonArray = jsonObject.getJSONArray("datos");

                    if ("1".equals(exito)) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int ID = Integer.parseInt(object.getString("ID"));
                            int cliente_DNI = Integer.parseInt(object.getString("cliente_DNI"));
                            int vendedor_DNI = Integer.parseInt(object.getString("vendedor_DNI"));
                            String cliente_nombre = object.getString("cliente_nombre");
                            String vendedor_nombre = object.getString("vendedor_nombre");

                            String fechaGeneradoStr = object.getString("fecha_generado");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date fechaGenerado = null;
                            try {
                                fechaGenerado = sdf.parse(fechaGeneradoStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String fechaEnvioStr = object.getString("fecha_envio");
                            SimpleDateFormat sdfSinHora = new SimpleDateFormat("yyyy-MM-dd");
                            Date fechaEnvio = null;
                            try {
                                fechaEnvio = sdfSinHora.parse(fechaEnvioStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String fechaEntregaStr = object.getString("fecha_entrega");
                            Date fechaEntrega = null;
                            try {
                                fechaEntrega = sdfSinHora.parse(fechaEntregaStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            int estado = Integer.parseInt(object.getString("estado"));
                            String direccion_entrega = object.getString("direccion_entrega");
                            double precio_total = Double.parseDouble(object.getString("precio_total"));

                            pedidos = new Pedidos(ID, cliente_DNI, vendedor_DNI, cliente_nombre, vendedor_nombre, fechaGenerado, fechaEnvio, fechaEntrega, estado, direccion_entrega, precio_total);
                            pedidosArrayList.add(pedidos);
                        }
                        listaPedidosAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PedidosActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PedidosActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(PedidosActivity.this);
        requestQueue.add(request);
    }
}