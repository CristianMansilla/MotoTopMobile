package com.example.mototop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mototop.entidades.Clientes;

import java.util.HashMap;
import java.util.Map;

public class ClientesEditarActivity extends AppCompatActivity {
    EditText edtEditarDni, edtEditarNombre, edtEditarApellido, edtEditarTelefono, edtEditarCorreo, edtEditarDomicilio, edtEditarDireccionEntrega;
    Button btnGuardar;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_editar);
        edtEditarDni = findViewById(R.id.edtEditarDni);
        edtEditarNombre = findViewById(R.id.edtEditarNombre);
        edtEditarApellido = findViewById(R.id.edtEditarApellido);
        edtEditarTelefono = findViewById(R.id.edtEditarTelefono);
        edtEditarCorreo = findViewById(R.id.edtEditarCorreo);
        edtEditarDomicilio = findViewById(R.id.edtEditarDomicilio);
        edtEditarDireccionEntrega = findViewById(R.id.edtEditarDireccionEntrega);
        btnGuardar = findViewById(R.id.btnGuardar);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");
        Integer dniValue = ClientesActivity.clientesArrayList.get(position).getDNI();
        if (dniValue != null) {
            Log.d("DNI_Value", String.valueOf(dniValue));
            edtEditarDni.setText(String.valueOf(dniValue));
        } else {
            edtEditarDni.setText("");
        }
        edtEditarNombre.setText(ClientesActivity.clientesArrayList.get(position).getNombre());
        edtEditarApellido.setText(ClientesActivity.clientesArrayList.get(position).getApellido());
        Long telefonoValue = ClientesActivity.clientesArrayList.get(position).getTelefono();
        if (telefonoValue != null) {
            edtEditarTelefono.setText(String.valueOf(telefonoValue));
        } else {
            edtEditarTelefono.setText("");
        }
        edtEditarCorreo.setText(ClientesActivity.clientesArrayList.get(position).getCorreo());
        edtEditarDomicilio.setText(ClientesActivity.clientesArrayList.get(position).getDomicilio());
        edtEditarDireccionEntrega.setText(ClientesActivity.clientesArrayList.get(position).getDireccion_entrega());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarDatos();
            }
        });

    }

    public void editarDatos(){
        int DNI = Integer.parseInt(edtEditarDni.getText().toString().trim());
        String nombre = edtEditarNombre.getText().toString().trim();
        String apellido = edtEditarApellido.getText().toString().trim();
        long telefono = Long.parseLong(edtEditarTelefono.getText().toString().trim());
        String correo = edtEditarCorreo.getText().toString().trim();
        String domicilio = edtEditarDomicilio.getText().toString().trim();
        String direccion_entrega = edtEditarDireccionEntrega.getText().toString().trim();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando");


        if(nombre.isEmpty()||apellido.isEmpty()||correo.isEmpty()||domicilio.isEmpty()||direccion_entrega.isEmpty()){
            Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            String url = "http://192.168.56.1/ws_mototop/clientes/editar.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("ResponseFromServer", response);
                    progressDialog.dismiss();

                    if (response.toLowerCase().contains("datos actualizados")) {
                        Toast.makeText(ClientesEditarActivity.this, "Actualizado correctamente", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        //startActivity(new Intent(getApplicationContext(), ClientesActivity.class));
                        //finish();

                        Intent intent = new Intent(getApplicationContext(), ClientesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ClientesEditarActivity.this, "Error, no se puede actualizar", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ClientesEditarActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", "Error al enviar la solicitud", error);
                    progressDialog.dismiss();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("DNI", String.valueOf(DNI));
                    params.put("nombre", nombre);
                    params.put("apellido", apellido);
                    params.put("telefono", String.valueOf(telefono));
                    params.put("correo", correo);
                    params.put("domicilio", domicilio);
                    params.put("direccion_entrega", direccion_entrega);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(ClientesEditarActivity.this);
            requestQueue.add(request);
        }

    }
}