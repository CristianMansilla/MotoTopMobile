package com.example.mototop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class PedidosAgregarActivity extends AppCompatActivity {
    Spinner spinnerClientes;
    EditText edtPedidoDireccionEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_agregar);

        spinnerClientes = findViewById(R.id.spinnerClientes);
        edtPedidoDireccionEntrega = findViewById(R.id.edtPedidoDireccionEntrega);

        // Configurar el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
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
                            String nombre = clienteObject.getString("nombre");
                            String apellido = clienteObject.getString("apellido");

                            // Agregar nombre y apellido al adaptador
                            adapter.add(nombre + " " + apellido);
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
        String urlObtenerDireccion = "http://192.168.56.1/ws_mototop/pedidos/obtenerDireccionEntrega.php?nombre_cliente=" + clienteSeleccionado;

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
                            edtPedidoDireccionEntrega.setText(direccionEntrega);
                        } else {
                            // Limpiar el EditText si no se encuentra la dirección de entrega
                            edtPedidoDireccionEntrega.setText("");
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
}