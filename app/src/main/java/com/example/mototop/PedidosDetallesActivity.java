package com.example.mototop;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mototop.adapters.ListaPedidosDetallesAdapter;
import com.example.mototop.adapters.ListaProductosAdapter;
import com.example.mototop.entidades.ListaPedidos;
import com.example.mototop.entidades.Pedidos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class PedidosDetallesActivity extends AppCompatActivity {
    ListView listViewProductosDetalles;
    ListaPedidosDetallesAdapter listaPedidosDetallesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_detalles);

        // Recupera la posición del pedido seleccionado de los extras
        int position = getIntent().getIntExtra("position", -1);

        if (position != -1) {
            // Recupera el pedido de la lista de pedidos
            Pedidos pedido = PedidosActivity.pedidosArrayList.get(position);

            // Formatea la fecha del pedido
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String fechaGeneradoFormatted = dateFormat.format(pedido.getFecha_generado());

            // Actualiza los EditText con la información del pedido
            EditText edtDetallePedidoFechaGenerado = findViewById(R.id.edtDetallePedidoFechaGenerado);
            edtDetallePedidoFechaGenerado.setText(fechaGeneradoFormatted);

            EditText edtDetalleNombreYApellido = findViewById(R.id.edtDetalleNombreYApellido);
            edtDetalleNombreYApellido.setText(pedido.getCliente_nombre());

            EditText edtDetalleDireccionEntrega = findViewById(R.id.edtDetalleDireccionEntrega);
            edtDetalleDireccionEntrega.setText(pedido.getDireccion_entrega());

            // Muestra el estado del pedido
            EditText txtEstadoPedido = findViewById(R.id.edtDetalleEstado);
            txtEstadoPedido.setText(pedido.getEstado());

            // Obtén la lista de productos para el pedido seleccionado
            ArrayList<ListaPedidos> listaProductos = obtenerListaProductos(pedido.getID());

            // Configura el ListView para mostrar la lista de productos
            listViewProductosDetalles = findViewById(R.id.listViewProductosDetalle);
            //ArrayAdapter<ListaPedidos> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, );
            listaPedidosDetallesAdapter = new ListaPedidosDetallesAdapter(this, listaProductos);
            listViewProductosDetalles.setAdapter(listaPedidosDetallesAdapter);

            EditText edtDetalleTotal = findViewById(R.id.edtDetalleTotal);
            edtDetalleTotal.setText(String.valueOf(pedido.getPrecio_total()));
        }
    }

    private ArrayList<ListaPedidos> obtenerListaProductos(int pedidoID) {
        ArrayList<ListaPedidos> listaProductos = new ArrayList<>();

        // Realiza una solicitud al servidor para obtener la lista de productos asociados al pedidoID
        String url = "http://192.168.56.1/ws_mototop/pedidos/mostrarListaPedido.php?pedidoID=" + pedidoID;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String exito = jsonObject.getString("exito");
                    JSONArray jsonArray = jsonObject.getJSONArray("datos");

                    if ("1".equals(exito)) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int ID = Integer.parseInt(object.getString("ID"));
                            int pedido_ID = Integer.parseInt(object.getString("pedido_ID"));
                            String barcodeString = object.getString("barcode");
                            long barcode = 0;  // o cualquier otro valor predeterminado que tenga sentido en tu aplicación

                            try {
                                barcode = Long.parseLong(barcodeString);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Manejar el caso en que la cadena no sea un número válido
                            }
                            int cantidad = Integer.parseInt(object.getString("cantidad"));
                            String nombre = object.getString("nombre");
                            double precio = Double.parseDouble(object.getString("precio"));

                            // Verificar si la cadena es "null" antes de convertirla a int
                            String ofertaDescuentoString = object.getString("oferta_descuento");
                            int oferta_descuento = (ofertaDescuentoString != null && !ofertaDescuentoString.equals("null")) ? Integer.parseInt(ofertaDescuentoString) : 0;

                            String ofertaPagaString = object.getString("oferta_paga");
                            int oferta_paga = (ofertaPagaString != null && !ofertaPagaString.equals("null")) ? Integer.parseInt(ofertaPagaString) : 0;

                            String ofertaLlevaString = object.getString("oferta_lleva");
                            int oferta_lleva = (ofertaLlevaString != null && !ofertaLlevaString.equals("null")) ? Integer.parseInt(ofertaLlevaString) : 0;

                            double total = Double.parseDouble(object.getString("total"));

                            ListaPedidos listaPedido = new ListaPedidos(ID, pedido_ID, barcode, cantidad, nombre, precio, oferta_descuento, oferta_paga, oferta_lleva, total);
                            listaProductos.add(listaPedido);
                        }
                        listaPedidosDetallesAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PedidosDetallesActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PedidosDetallesActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(PedidosDetallesActivity.this);
        requestQueue.add(request);

        return listaProductos;
    }
}