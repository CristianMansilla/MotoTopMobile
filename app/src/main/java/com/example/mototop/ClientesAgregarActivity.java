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

import java.util.HashMap;
import java.util.Map;

public class ClientesAgregarActivity extends AppCompatActivity {
    EditText edtNombre, edtApellido, edtDni, edtPass, edtTelefono, edtCorreo, edtDomicilio, edtDireccionEntrega;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_agregar);

        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtDni = findViewById(R.id.edtDni);
        edtPass = findViewById(R.id.edtPass);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtCorreo = findViewById(R.id.edtCorreo);
        edtDomicilio = findViewById(R.id.edtDomicilio);
        edtDireccionEntrega = findViewById(R.id.edtDireccionEntrega);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDatos();
            }
        });

    }

    public void registrarDatos(){
        String dniString = edtDni.getText().toString().trim();
        if (dniString.isEmpty()) {
            Toast.makeText(this, "Ingrese un valor para el DNI", Toast.LENGTH_SHORT).show();
            return; // Sale del método si el DNI está vacío
        }
        int DNI = Integer.parseInt(dniString);
        String pass = edtPass.getText().toString().trim();
        String nombre = edtNombre.getText().toString().trim();
        String apellido = edtApellido.getText().toString().trim();
        long telefono = Long.parseLong(edtTelefono.getText().toString().trim());
        String correo = edtCorreo.getText().toString().trim();
        String domicilio = edtDomicilio.getText().toString().trim();
        String direccion_entrega = edtDireccionEntrega.getText().toString().trim();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando");


        if(nombre.isEmpty()||apellido.isEmpty()||correo.isEmpty()||domicilio.isEmpty()||direccion_entrega.isEmpty()){
            Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            String url = "http://192.168.56.1/ws_mototop/clientes/insertar.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("ResponseFromServer", response);
                    progressDialog.dismiss();

                    if (response.toLowerCase().contains("datos insertados")) {
                        Toast.makeText(ClientesAgregarActivity.this, "Registrado correctamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (response.toLowerCase().contains("dni ya existe")) {
                        Toast.makeText(ClientesAgregarActivity.this, "Error: El DNI ya existe", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ClientesAgregarActivity.this, "Error, no se puede registrar", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ClientesAgregarActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", "Error al enviar la solicitud", error);
                    progressDialog.dismiss();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("DNI", String.valueOf(DNI));
                    params.put("pass", pass);
                    params.put("nombre", nombre);
                    params.put("apellido", apellido);
                    params.put("telefono", String.valueOf(telefono));
                    params.put("correo", correo);
                    params.put("domicilio", domicilio);
                    params.put("direccion_entrega", direccion_entrega);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(ClientesAgregarActivity.this);
            requestQueue.add(request);
        }

    }
}