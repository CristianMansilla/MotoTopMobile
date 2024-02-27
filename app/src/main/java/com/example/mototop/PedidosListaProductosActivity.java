package com.example.mototop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mototop.adapters.ListaProductosAdapter;
import com.example.mototop.adapters.ListaProductosPedidosAdapter;
import com.example.mototop.entidades.Productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PedidosListaProductosActivity extends AppCompatActivity {

    EditText editTextBuscador;
    ListView listViewPedidosProductos;
    ListaProductosPedidosAdapter listaProductosPedidosAdapter;
    private ArrayList<Productos> productosListFull = new ArrayList<>();
    public static ArrayList<Productos> productosArrayList = new ArrayList<>();

    private static final int CODIGO_DE_SOLICITUD = 1;

    Productos productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pedidos_lista_productos);

        // Verificar si la ActionBar no es nula antes de habilitar el botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listViewPedidosProductos = findViewById(R.id.listViewPedidosProductos);

        listaProductosPedidosAdapter = new ListaProductosPedidosAdapter(this, productosArrayList, productosListFull);
        listViewPedidosProductos.setAdapter(listaProductosPedidosAdapter);

        listarProductos();

        editTextBuscador = findViewById(R.id.editTextBuscador);
        // Agrega un listener al EditText para detectar cambios en el texto
        editTextBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No necesitamos hacer nada antes de que el texto cambie
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filtra la lista de productos según el texto ingresado
                filterProducts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No necesitamos hacer nada después de que el texto cambie
            }
        });

        listViewPedidosProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtén el objeto Productos para la posición clicada
                Productos productos = (Productos) parent.getItemAtPosition(position);

                aumentarCantidad(productos);
                listaProductosPedidosAdapter.notifyDataSetChanged(); // Notificar cambios en el conjunto de datos
            }
        });
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

    private void aumentarCantidad(Productos productos) {
        // Incrementar la cantidad en el modelo
        productos.setCantidadActual(productos.getCantidadActual() + 1);
    }

    private void disminuirCantidad(Productos productos) {
        // Asegurarse de que la cantidad no sea negativa
        if (productos.getCantidadActual() > 0) {
            // Disminuir la cantidad en el modelo
            productos.setCantidadActual(productos.getCantidadActual() - 1);
        }
    }

    // Método para filtrar la lista de productos según el texto ingresado
    private void filterProducts(String text) {
        ArrayList<Productos> filteredList = new ArrayList<>();

        if (text.isEmpty()) {
            // Si el texto de búsqueda está vacío, muestra todos los productos
            filteredList.addAll(productosListFull);
        } else {
            // Filtra por nombre del producto
            for (Productos productos : productosListFull) {
                if (productos.getNombre().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(productos);
                }
            }
        }

        // Actualiza el adaptador con la lista filtrada
        listaProductosPedidosAdapter.filterList(filteredList);
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
                            //long barcode = Long.parseLong(object.getString("barcode"));
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
                            productosListFull.add(productos);
                        }
                        listaProductosPedidosAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PedidosListaProductosActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PedidosListaProductosActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(PedidosListaProductosActivity.this);
        requestQueue.add(request);

    }

    public void guardarProductosPedidos(View view) {
        // Crear una lista para almacenar los productos con cantidades mayores a 0
        ArrayList<Productos> productosSeleccionados = new ArrayList<>();

        // Variable para verificar si hay suficiente stock
        boolean stockSuficiente = true;

        // Iterar sobre la lista de productos y agregar aquellos con cantidad mayor a 0
        for (Productos producto : productosArrayList) {
            if (producto.getCantidadActual() > 0) {
                // Verificar el stock antes de agregar al pedido
                if (verificarStockSuficiente(producto)) {
                    productosSeleccionados.add(producto);
                } else {
                    // Indicar que no hay suficiente stock
                    stockSuficiente = false;
                }
            }
        }

        if (!stockSuficiente) {
            // Mostrar un AlertDialog indicando que no hay suficiente stock
            mostrarAlertaNoStock();
            return;
        }

        // Obtener la cantidad total de productos del adaptador
        int cantidadTotalProductos = listaProductosPedidosAdapter.getCantidadTotalProductos();

        double totalProductos = listaProductosPedidosAdapter.getTotalProductos();


        // Crear un intent para almacenar los productos seleccionados
        Intent intent = new Intent();

        // Agregar la lista de productos seleccionados al intent
        intent.putParcelableArrayListExtra("productosSeleccionados", productosSeleccionados);
        intent.putExtra("cantidadTotalProductos", cantidadTotalProductos);

        intent.putExtra("totalProductos", totalProductos);
        Log.d("PedidosListaProductosActivity", "Cantidad total de productos: " + cantidadTotalProductos);

        // Establecer el resultado de la actividad como RESULT_OK y adjuntar el intent
        setResult(RESULT_OK, intent);

        // Cerrar la actividad
        finish();
    }
    // Método para verificar si hay suficiente stock para un producto
    private boolean verificarStockSuficiente(Productos producto) {
        return producto.getCantidadActual() <= producto.getStock();
    }

    // Método para mostrar un AlertDialog indicando que no hay suficiente stock
    private void mostrarAlertaNoStock() {
        new AlertDialog.Builder(this)
                .setTitle("No hay suficiente stock")
                .setMessage("Algunos productos no tienen suficiente stock para realizar el pedido.")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}

