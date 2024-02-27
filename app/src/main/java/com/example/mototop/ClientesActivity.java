package com.example.mototop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mototop.adapters.ListaClientesAdapter;
import com.example.mototop.entidades.Clientes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientesActivity extends AppCompatActivity {
    ListView listViewClientes;
    ListaClientesAdapter listaClientesAdapter;
    public static ArrayList<Clientes> clientesArrayList = new ArrayList<>();

    private static final int CODIGO_DE_SOLICITUD = 1;

    Clientes clientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clientes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewClientes = findViewById(R.id.listViewClientes);
        listaClientesAdapter = new ListaClientesAdapter(this, clientesArrayList);
        listViewClientes.setAdapter(listaClientesAdapter);

        listViewClientes.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                ProgressDialog progressDialog = new ProgressDialog(view.getContext());

                CharSequence[] dialogo = {"VER MAS INFO", "EDITAR CLIENTE", "ELIMINAR CLIENTE"};
                String nombreYApellido = clientesArrayList.get(position).getNombre() + " " + clientesArrayList.get(position).getApellido();
                builder.setTitle(nombreYApellido);
                builder.setItems(dialogo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                startActivity(new Intent(getApplicationContext(), ClientesDetallesActivity.class).putExtra("position", position));
                                break;
                            case 1:
                                startActivity(new Intent(getApplicationContext(), ClientesEditarActivity.class).putExtra("position", position));
                                break;
                            case 2:
                                eliminarClientes(clientesArrayList.get(position).getDNI());
                                break;
                        }
                    }
                });

                builder.show();
            }
        });


        listarClientes();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isTaskRoot()) {
                    // Si es la actividad raíz, ir al menú principal
                    Intent intent = new Intent(this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    // Si no es la actividad raíz, simplemente cerrar la actividad actual
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_DE_SOLICITUD && resultCode == RESULT_OK) {
            listarClientes();
            //invalidateOptionsMenu();
        }
    }

    @Override
    public void onBackPressed() {
        // Aquí puedes iniciar la actividad del menú principal o cualquier otra actividad que desees
        super.onBackPressed();
        // Utiliza la bandera FLAG_ACTIVITY_CLEAR_TOP para ir al menú principal
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        // Asegúrate de finalizar la actividad actual para que no quede en la pila de actividades
        finish();
    }


    public void listarClientes() {
        String url = "http://192.168.56.1/ws_mototop/clientes/mostrar.php";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                clientesArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String exito = jsonObject.getString("exito");
                    JSONArray jsonArray = jsonObject.getJSONArray("datos");

                    if ("1".equals(exito)) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int dni = Integer.parseInt(object.getString("DNI"));
                            String nombre = object.getString("nombre");
                            String apellido = object.getString("apellido");
                            long telefono = Long.parseLong(object.getString("telefono"));
                            String correo = object.getString("correo");
                            String domicilio = object.getString("domicilio");
                            String direccion_entrega = object.getString("direccion_entrega");
                            double debe = Double.parseDouble(object.getString("debe"));

                            clientes = new Clientes(dni, nombre, apellido, telefono, correo, domicilio, direccion_entrega, debe);
                            clientesArrayList.add(clientes);
                        }
                        listaClientesAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ClientesActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ClientesActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ClientesActivity.this);
        requestQueue.add(request);

    }

    public void eliminarClientes(final int DNI) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Cliente");
        builder.setMessage("¿Estás seguro de que deseas eliminar este cliente?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Llamada al método para eliminar el cliente
                eliminarCliente(DNI);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void eliminarCliente(int DNI) {
        // Realizar la solicitud al servidor para eliminar el cliente
        String url = "http://192.168.56.1/ws_mototop/clientes/eliminar.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.toLowerCase().contains("datos eliminados")) {
                    Toast.makeText(ClientesActivity.this, "Cliente eliminado correctamente", Toast.LENGTH_LONG).show();
                    // Buscar y eliminar el cliente de la lista
                    for (Clientes cliente : clientesArrayList) {
                        if (cliente.getDNI() == DNI) {
                            clientesArrayList.remove(cliente);
                            break;
                        }
                    }
                    // Notificar al adaptador del cambio en los datos
                    listaClientesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ClientesActivity.this, "Error, no se puede eliminar el cliente", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ClientesActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("DNI", String.valueOf(DNI));

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ClientesActivity.this);
        requestQueue.add(request);
    }

    public void abrirAgregarCliente(View view){
        Intent intent = new Intent(ClientesActivity.this, ClientesAgregarActivity.class);
        startActivityForResult(intent, CODIGO_DE_SOLICITUD);
    }
}