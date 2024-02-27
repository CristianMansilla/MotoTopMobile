package com.example.mototop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.mototop.entidades.Productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class PedidosAgregarActivity extends AppCompatActivity {
    Spinner spinnerClientes;
    TextView txtPedidoDireccionEntrega;
    TextView txtNombreVendedor;

    String dni;

    // Mapa para almacenar el DNI correspondiente a cada cliente
    private Map<String, String> dniClientesMap = new HashMap<>();

    private ArrayList<Productos> productosSeleccionados = new ArrayList<>();

    private static final int CODIGO_DE_SOLICITUD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_agregar);

        Bundle recibirDni = getIntent().getExtras();
        dni = recibirDni.getString("DNI");

        Log.d("PedidosAgregarActivity", "DNI obtenido: " + dni);

        // Obtener el nombre del vendedor de manera asíncrona
        obtenerNombreVendedorAsync(dni);


        configurarFechaInicial();

        spinnerClientes = findViewById(R.id.spinnerClientes);
        txtPedidoDireccionEntrega = findViewById(R.id.txtPedidoDireccionEntrega);
        txtNombreVendedor = findViewById(R.id.txtNombreVendedor);

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

        Button btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para guardar en la base de datos al hacer clic en el botón
                guardarEnBaseDeDatos();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_DE_SOLICITUD) {
            if (resultCode == RESULT_OK) {
                // Se recibió el resultado correctamente
                int cantidadTotalProductos = data.getIntExtra("cantidadTotalProductos", 0);
                actualizarCantidadTotal(cantidadTotalProductos);

                productosSeleccionados = data.getParcelableArrayListExtra("productosSeleccionados");
                // Hacer algo con la lista de productos seleccionados, por ejemplo, imprimir los nombres
                for (Productos producto : productosSeleccionados) {
                    Log.d("PedidosAgregarActivity", "Producto seleccionado: " + producto.getNombre());
                    // Puedes realizar otras acciones con el producto seleccionado según tus necesidades.
                }

                double totalProductos = data.getDoubleExtra("totalProductos", 0);

                actualizarTotalProductos(totalProductos);

                // Aquí puedes hacer lo que quieras con la información recibida
                Log.d("PedidosAgregarActivity", "Cantidad total de productos: " + cantidadTotalProductos);
                Log.d("PedidosAgregarActivity", "Total $: " + totalProductos);
            } else {
                // La actividad secundaria podría haberse cerrado inesperadamente o con un error
                Toast.makeText(this, "Error o actividad cancelada", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void actualizarCantidadTotal(int cantidadTotal) {
        // Implementa tu lógica para actualizar la cantidad total en tu actividad
        TextView txtCantidadTotal = findViewById(R.id.txtCantidadProducto);
        txtCantidadTotal.setText(String.valueOf(cantidadTotal));
    }

    private double calcularTotalProductos(ArrayList<Productos> productosSeleccionados) {
        double total = 0.0;
        for (Productos producto : productosSeleccionados) {
            // Realiza el cálculo del total de cada producto (cantidad * precio)
            total += producto.getPrecio() * producto.getCantidadActual();
        }
        return total;
    }

    private void actualizarTotalProductos(double totalProductos) {
        // Implementa tu lógica para actualizar la cantidad total en tu actividad
        TextView txtTotalProductos = findViewById(R.id.txtTotalProducto);
        txtTotalProductos.setText(String.format("%.2f", totalProductos));
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
        String urlClientes = "http://192.168.56.1/ws_mototop/pedidos/obtenerDatosClientes.php?dni=" + dni;

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

    private void obtenerNombreVendedorAsync(String dniVendedor) {
        String urlObtenerNombreVendedor = "http://192.168.56.1/ws_mototop/pedidos/obtenerNombreVendedor.php?dni=" + dniVendedor;

        hacerSolicitudHTTP(urlObtenerNombreVendedor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String exito = jsonObject.getString("exito");

                    if ("Success".equals(exito)) {
                        String nombreVendedor = jsonObject.getString("nombreVendedor");

                        // Actualizar el TextView con el nombre del vendedor
                        txtNombreVendedor.setText(nombreVendedor);
                    } else {
                        Toast.makeText(PedidosAgregarActivity.this, "Error al obtener el nombre del vendedor", Toast.LENGTH_SHORT).show();
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

    public void abrirAgregarProducto(View view){
        Intent intent = new Intent(PedidosAgregarActivity.this, PedidosListaProductosActivity.class);
        startActivityForResult(intent, CODIGO_DE_SOLICITUD);
    }

    //Guardar PEDIDO en la base de datos
    private void guardarEnBaseDeDatos() {
        // Obtener los datos de la interfaz gráfica
        String clienteDNI = obtenerClienteDNI();
        String vendedorDNI = dni;
        String clienteNombre = obtenerClienteNombre();
        String vendedorNombre = obtenerVendedorNombre();
        String fechaGenerado = obtenerFechaGenerado();
        //String fechaEnvio = "";
        //String fechaEntrega = "";
        int estado = 0;
        String codSeguimiento = generarCodigoSeguimiento();
        String direccionEntrega = obtenerDireccionEntrega();
        double precioTotal = obtenerPrecioTotal();

        // Realizar la solicitud HTTP para guardar los datos en el servidor
        String urlGuardarPedido = "http://192.168.56.1/ws_mototop/pedidos/guardar.php";

        StringRequest request = new StringRequest(Request.Method.POST, urlGuardarPedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");

                            if ("Success".equals(exito)) {
                                // Obtener el ID del pedido recién insertado
                                int pedidoID = jsonObject.getInt("pedidoID");

                                // Guardar los productos en la tabla LISTA_PEDIDO
                                guardarProductosEnListaPedido(pedidoID);
                                Toast.makeText(PedidosAgregarActivity.this, "Pedido guardado exitosamente", Toast.LENGTH_SHORT).show();

                                // Cerrar la actividad actual
                                finish();

                                // Abrir la actividad anterior (PedidosActivity)
                                abrirPedidosActivity();
                            } else {
                                Toast.makeText(PedidosAgregarActivity.this, "Error al guardar el pedido", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PedidosAgregarActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PedidosAgregarActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros se envían al servidor
                Map<String, String> params = new HashMap<>();
                params.put("cliente_DNI", clienteDNI);
                params.put("vendedor_DNI", vendedorDNI);
                params.put("cliente_nombre", clienteNombre);
                params.put("vendedor_nombre", vendedorNombre);
                params.put("fecha_generado", fechaGenerado);
                //params.put("fecha_envio", fechaEnvio);
                //params.put("fecha_entrega", fechaEntrega);
                params.put("estado", String.valueOf(estado));
                params.put("codSeguimiento", codSeguimiento);
                params.put("direccion_entrega", direccionEntrega);
                params.put("precio_total", String.valueOf(precioTotal));

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Tiempo de espera en milisegundos antes de que expire la solicitud
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Número máximo de intentos de reintentos
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void guardarProductosEnListaPedido(int pedidoID) {
        // Obtener la lista de productos seleccionados
        ArrayList<Productos> productosSeleccionados = obtenerProductosSeleccionados();

        // Realizar la solicitud HTTP para guardar los productos en la tabla LISTA_PEDIDO
        String urlGuardarListaPedido = "http://192.168.56.1/ws_mototop/pedidos/guardarListaPedido.php";

        // Crear una solicitud para cada producto en la lista
        for (Productos producto : productosSeleccionados) {
            descontarStock(producto.getBarcode(), producto.getCantidadActual());
            guardarProductoEnListaPedido(urlGuardarListaPedido, pedidoID, producto);
        }
    }

    private void guardarProductoEnListaPedido(String url, int pedidoID, Productos producto) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta si es necesario
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error si es necesario
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pedido_ID", String.valueOf(pedidoID));
                params.put("barcode", String.valueOf(producto.getBarcode()));
                params.put("cantidad", String.valueOf(producto.getCantidadActual()));
                params.put("nombre", producto.getNombre());
                params.put("precio", String.valueOf(producto.getPrecio()));
                params.put("total", String.valueOf(producto.getCantidadActual() * producto.getPrecio()));
                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private ArrayList<Productos> obtenerProductosSeleccionados() {
        return productosSeleccionados;
    }

    private void abrirPedidosActivity() {
        Intent intent = new Intent(PedidosAgregarActivity.this, PedidosActivity.class);
        startActivity(intent);
    }

    private void descontarStock(long barcode, int cantidad) {
        // Realizar la solicitud HTTP para deducir la cantidad del stock en la tabla PRODUCTO
        String urlDescontarStock = "http://192.168.56.1/ws_mototop/pedidos/descontarStock.php";

        StringRequest request = new StringRequest(Request.Method.POST, urlDescontarStock,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");

                            if (!"Success".equals(exito)) {
                                // Manejar el caso en que la deducción del stock no fue exitosa
                                Toast.makeText(PedidosAgregarActivity.this, "Error al descontar stock", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PedidosAgregarActivity.this, "Error al analizar JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error si es necesario
                        Toast.makeText(PedidosAgregarActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("barcode", String.valueOf(barcode));
                params.put("cantidad", String.valueOf(cantidad));
                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private String obtenerFechaGenerado() {
        // Obtener la fecha y hora actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // Configurar la zona horaria a America/Argentina/Buenos_Aires (UTC-3)
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        String fechaActual = dateFormat.format(calendar.getTime());
        return fechaActual;
    }

    private String obtenerClienteDNI() {
        String clienteSeleccionado = spinnerClientes.getSelectedItem().toString();
        return dniClientesMap.get(clienteSeleccionado);
    }

    private String obtenerClienteNombre() {
        String clienteSeleccionado = spinnerClientes.getSelectedItem().toString();
        return clienteSeleccionado;
    }
    private String obtenerVendedorNombre() {
        return txtNombreVendedor.getText().toString();
    }
    private String generarCodigoSeguimiento() {
        // Generar números aleatorios de 100000000 a 999999999
        int numeroAleatorio = new Random().nextInt(900000000) + 100000000;

        // Formatear el código de seguimiento
        String codSeguimiento = "CU" + numeroAleatorio + "AR";
        return codSeguimiento;
    }

    private String obtenerDireccionEntrega() {
        return txtPedidoDireccionEntrega.getText().toString();
    }

    private double obtenerPrecioTotal() {
        TextView txtTotalProductos = findViewById(R.id.txtTotalProducto);
        String total = txtTotalProductos.getText().toString().replace("$", "");
        return Double.parseDouble(total);
    }
}