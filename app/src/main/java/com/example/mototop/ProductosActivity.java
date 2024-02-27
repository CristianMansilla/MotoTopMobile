package com.example.mototop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mototop.adapters.ListaClientesAdapter;
import com.example.mototop.adapters.ListaProductosAdapter;
import com.example.mototop.entidades.Clientes;
import com.example.mototop.entidades.Productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProductosActivity extends AppCompatActivity {

    ListView listViewProductos;
    ListaProductosAdapter listaProductosAdapter;
    public static ArrayList<Productos> productosArrayList = new ArrayList<>();

    private static final int CODIGO_DE_SOLICITUD = 1;

    Productos productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_productos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewProductos = findViewById(R.id.listViewProductos);
        listaProductosAdapter = new ListaProductosAdapter(this, productosArrayList);
        listViewProductos.setAdapter(listaProductosAdapter);

        listarProductos();
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

    public void listarProductos() {
        String url = "http://192.168.56.1/ws_mototop/productos/mostrar.php";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                productosArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String exito = jsonObject.getString("exito");
                    JSONArray jsonArray = jsonObject.getJSONArray("datos");

                    if ("1".equals(exito)) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int ID = Integer.parseInt(object.getString("ID"));
                            String barcodeString = object.getString("barcode");
                            long barcode = 0;  // o cualquier otro valor predeterminado que tenga sentido en tu aplicación

                            try {
                                barcode = Long.parseLong(barcodeString);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Manejar el caso en que la cadena no sea un número válido
                            }
                            int rubro_ID = Integer.parseInt(object.getString("rubro_ID"));
                            int proveedor_ID = Integer.parseInt(object.getString("proveedor_ID"));
                            String nombre = object.getString("nombre");

                            String img_dataBase64 = object.getString("img_data");

                            // Decodificar la cadena Base64 a un array de bytes
                            byte[] img_data = Base64.decode(img_dataBase64, Base64.DEFAULT);

                            double precio = Double.parseDouble(object.getString("precio"));
                            int stock = Integer.parseInt(object.getString("stock"));
                            int oferta_descuento = 0;  // Valor predeterminado o cualquier otro valor predeterminado que tenga sentido en tu aplicación
                            try {
                                oferta_descuento = Integer.parseInt(object.getString("oferta_descuento"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Manejar el caso en que la cadena no sea un número válido
                            }
                            int oferta_paga = 0;  // Valor predeterminado o cualquier otro valor predeterminado que tenga sentido en tu aplicación
                            try {
                                oferta_paga = Integer.parseInt(object.getString("oferta_paga"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Manejar el caso en que la cadena no sea un número válido
                            }
                            int oferta_lleva = 0;  // Valor predeterminado o cualquier otro valor predeterminado que tenga sentido en tu aplicación
                            try {
                                oferta_lleva = Integer.parseInt(object.getString("oferta_lleva"));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Manejar el caso en que la cadena no sea un número válido
                            }

                            //int oferta_descuento = Integer.parseInt(object.getString("oferta_descuento"));
                            //int oferta_paga = Integer.parseInt(object.getString("oferta_paga"));
                            //int oferta_lleva = Integer.parseInt(object.getString("oferta_lleva"));

                            String fechaInicioStr = object.getString("oferta_inicio");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date ofertaInicio = null;
                            try {
                                ofertaInicio = sdf.parse(fechaInicioStr);
                                // Ahora 'ofertaInicio' contiene la fecha como un objeto Date
                            } catch (ParseException e) {
                                e.printStackTrace();
                                // Manejar la excepción en caso de que la cadena no pueda ser parseada
                            }

                            String fechaFinStr = object.getString("oferta_fin");
                            Date ofertaFin = null;
                            try {
                                ofertaFin = sdf.parse(fechaFinStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                // Manejar la excepción en caso de que la cadena no pueda ser parseada
                            }

                            productos = new Productos(ID, barcode, rubro_ID, proveedor_ID, nombre, img_data, precio, stock, oferta_descuento, oferta_paga, oferta_lleva, ofertaInicio, ofertaFin);
                            productosArrayList.add(productos);
                        }
                        listaProductosAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProductosActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductosActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ProductosActivity.this);
        requestQueue.add(request);

    }
}