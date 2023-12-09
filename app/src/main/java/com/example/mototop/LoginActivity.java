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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText txtDniVendedor, txtPassVendedor;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtDniVendedor = findViewById(R.id.txtDniVendedor);
        txtPassVendedor = findViewById(R.id.txtPassVendedor);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }

    public void login(View view){
        if (txtDniVendedor.getText().toString().equals("") || txtPassVendedor.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Debe ingresar usuario y contraseña!", Toast.LENGTH_LONG).show();
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Por favor espere");
            progressDialog.show();

            int DNI = Integer.parseInt(txtDniVendedor.getText().toString().trim());
            String pass = txtPassVendedor.getText().toString().trim();

            String url = "http://192.168.56.1/ws_mototop/vendedores/login.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("LoginResponse", response);
                    progressDialog.dismiss();
                    if (response.contains("Success")) {
                        Log.d("LoginActivity", "Ingresó correctamente");
                        txtDniVendedor.setText("");
                        txtPassVendedor.setText("");

                        //startActivity(new Intent(getApplicationContext(), MenuActivity.class));

                        /*Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();*/
                        String dni = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            dni = jsonObject.getString("dni");
                            Log.d("LoginActivity", "DNI obtenido: " + dni);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        Bundle enviarDni = new Bundle();
                        enviarDni.putString("DNI", dni);
                        intent.putExtras(enviarDni);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "No ingresó correctamente");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", "Error al enviar la solicitud", error);
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

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(request);
        }
    }
}