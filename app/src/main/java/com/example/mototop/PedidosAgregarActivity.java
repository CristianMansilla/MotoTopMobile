package com.example.mototop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PedidosAgregarActivity extends AppCompatActivity {
    Spinner spinnerClientes;
    TextView txtPedidoDireccionEntrega;

    // Mapa para almacenar el DNI correspondiente a cada cliente
    private Map<String, String> dniClientesMap = new HashMap<>();

    private static final int CODIGO_DE_SOLICITUD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_agregar);

        configurarFechaInicial();

        spinnerClientes = findViewById(R.id.spinnerClientes);
        txtPedidoDireccionEntrega = findViewById(R.id.txtPedidoDireccionEntrega);

        // Configurar el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,  R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClientes.setAdapter(adapter);

        // Configurar la selección del Spinner
        spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtener el nombre del cliente seleccionado
                String clienteSeleccionado = spinnerClientes.getSelectedItem().toString();

                // Llamar a la función para obtener la dirección de entrega del cliente
                obtenerDireccionEntrega(clienteSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar el caso en que no se selecciona nada
            }
        });

        // Llamar a la función para obtener la lista de nombres de clientes
        obtenerClientes();
    }

    private void establecerFechaActual() {
        EditText txtPedidoFechaGenerado = findViewById(R.id.txtPedidoFechaGenerado);

        // Obtener la fecha y hora actual
        Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Configurar la zona horaria a America/Argentina/Buenos_Aires (UTC-3)
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        String fechaActual = dateFormat.format(calendar.getTime());

        // Establecer la fecha en el EditText
        txtPedidoFechaGenerado.setText(fechaActual);
    }
    private void configurarFechaInicial() {
        establecerFechaActual();
    }

    private void obtenerClientes() {
        String urlClientes = "http://192.168.56.1/ws_mototop/pedidos/obtenerDatosClientes.php";

        hacerSolicitudHTTP(urlClientes, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String exito = jsonObject.getString("exito");

                    if ("Success".equals(exito)) {
                        JSONArray clientesArray = jsonObject.getJSONArray("clientes");

                        // Limpiar el adaptador y agregar la indicación predeterminada
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerClientes.getAdapter();
                        adapter.clear();
                        adapter.add("Seleccione un cliente"); // Indicación predeterminada

                        for (int i = 0; i < clientesArray.length(); i++) {
                            JSONObject clienteObject = clientesArray.getJSONObject(i);
                            int dni = Integer.parseInt(clienteObject.getString("DNI"));
                            String nombre = clienteObject.getString("nombre");
                            String apellido = clienteObject.getString("apellido");

                            // Agregar nombre y apellido al adaptador
                            String nombreCompleto = nombre + " " + apellido;
                            adapter.add(nombreCompleto);

                            // Almacenar el DNI correspondiente al cliente
                            dniClientesMap.put(nombreCompleto, String.valueOf(dni));
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();


                        //spinnerClientes.setEnabled(false);
                    } else {
                        Toast.makeText(PedidosAgregarActivity.this, "Error al obtener clientes", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PedidosAgregarActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PedidosAgregarActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerDireccionEntrega(String clienteSeleccionado) {
        // Obtener el DNI del cliente seleccionado
        String dniCliente = dniClientesMap.get(clienteSeleccionado);

        // Verificar si el DNI es válido
        if (dniCliente != null) {
            String urlObtenerDireccion = "http://192.168.56.1/ws_mototop/pedidos/obtenerDireccionEntrega.php?dni_cliente=" + dniCliente;

            hacerSolicitudHTTP(urlObtenerDireccion, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String exito = jsonObject.getString("exito");

                        if ("Success".equals(exito)) {
                            JSONArray clientesArray = jsonObject.getJSONArray("clientes");

                            if (clientesArray.length() > 0) {
                                JSONObject clienteObject = clientesArray.getJSONObject(0);
                                String direccionEntrega = clienteObject.getString("direccion_entrega");

                                // Mostrar la dirección de entrega en el EditText
                                txtPedidoDireccionEntrega.setText(direccionEntrega);
                            } else {
                                // Limpiar el EditText si no se encuentra la dirección de entrega
                                txtPedidoDireccionEntrega.setText("");
                                Toast.makeText(PedidosAgregarActivity.this, "Dirección de entrega no encontrada", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PedidosAgregarActivity.this, "Error al obtener la dirección de entrega", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PedidosAgregarActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PedidosAgregarActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PedidosAgregarActivity.this, "DNI del cliente no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void hacerSolicitudHTTP(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.GET, url, listener, errorListener);

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Tiempo de espera en milisegundos antes de que expire la solicitud
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Número máximo de intentos de reintentos
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void abrirAgregarProducto(View view){
        Intent intent = new Intent(PedidosAgregarActivity.this, PedidosListaProductosActivity.class);
        startActivityForResult(intent, CODIGO_DE_SOLICITUD);
    }
}