package com.example.tiresportapp.Vista;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tiresportapp.Casodeuso.WebService;
import com.example.tiresportapp.Controlador.AdaptadorProductos;
import com.example.tiresportapp.Controlador.Peticiones;
import com.example.tiresportapp.Modelo.MySingleton;
import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.Casodeuso.ProductoActionCase;
import com.example.tiresportapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


//metodo principal de nuestra actividad
public class Catalogo extends AppCompatActivity {

    //declara un boton para comprar
    private Button btnComprar1;

    //declara un boton para comprar
    private Button btnComprar2;

    //declara un boton para buscar
    private Button btnBuscar;

    //declara un visor de texto
    private TextView txtLLanta;

    private ConstraintLayout padre;

    private Producto producto;

    ArrayList<Producto> listaProductos;

    ArrayList<Producto> listaCarrito;

    private RecyclerView rcvProd;

    ProgressDialog progressDialog;

    @Override
    //sobrescribe el metodo cuando se crea la actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        //declara una barra de accion
        ActionBar barraMenu = getSupportActionBar();

        //muestra la barra de acciones en la pantalla
        barraMenu.setDisplayShowHomeEnabled(true);

        //determina un logo previamente guardado
        barraMenu.setLogo(R.mipmap.ic_launcher);

        //muestra un titulo en la barra de acciones
        barraMenu.setTitle("Productos");

        //muestra un subtitulo en la barra de acciones
        barraMenu.setSubtitle("Elige un producto");

        //muestra un logo previamente determinado
        barraMenu.setDisplayUseLogoEnabled(true);

        //cambiar el color de la barra para que siempre sea el mismo
        barraMenu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#313d8c")));


        int azulPrincipal = getResources().getColor(R.color.azulPrincipal);
        int naranjaPrincipal = getResources().getColor(R.color.naranjaPrincipal);
        int blanco = getResources().getColor(R.color.white);

        new ConsultaProductos().execute();

    }

    public  class ConsultaProductos extends AsyncTask<Void, Void, Void> {

        ArrayList<Producto> listaProductos = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Catalogo.this);
            progressDialog.setMessage("Cargando Información...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                Thread.sleep(1000);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, WebService.GET_POST_PUT_PRODUCTOS, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Producto producto = null;
                                    JSONArray arrayProducto = response.getJSONArray("items");
                                    JSONObject jsonObject = null;
                                    for(int i = 0; i < arrayProducto.length(); i++){
                                        jsonObject = arrayProducto.getJSONObject(i);
                                        int id = jsonObject.getInt("id");
                                        String nombre = jsonObject.getString("nombre");
                                        int precio = jsonObject.getInt("precio");
                                        int inventario = jsonObject.getInt("inventario");
                                        int cantidad = jsonObject.getInt("cantidad");
                                        int estadofav = jsonObject.getInt("estadofav");
                                        String imagen = jsonObject.getString("imagen");
                                        producto = new Producto(id, nombre, precio, inventario, cantidad, estadofav,imagen);
                                        listaProductos.add(producto);
                                        Log.e("response", response.toString());

                                    }
                                }catch (Exception e){
                                    Log.e("Exceptión EX", e.getMessage());
                                }

                                rcvProd = (RecyclerView) findViewById(R.id.rcvProductos);
                                rcvProd.setHasFixedSize(true);
                                rcvProd.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                AdaptadorProductos adaptador = new AdaptadorProductos(listaProductos,Catalogo.this);
                                rcvProd.setAdapter(adaptador);


                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Log.e("REST", error.toString());
                                progressDialog.cancel();
                            }
                        });
                MySingleton.getInstance(Catalogo.this).addToRequestQueue(jsonObjectRequest);

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.cancel();

        }
    }

    @Override
    //sobrescribe el metodo de creacion de menu
    public boolean onCreateOptionsMenu(Menu menuPrincipal) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimnu,menuPrincipal);
        MenuItem productos = menuPrincipal.findItem(R.id.botonProductos);
        productos.setVisible(false);
        MenuItem mapa = menuPrincipal.findItem((R.id.cambiMapa));
        mapa.setVisible(false);
        return true;
    }
    @Override
    //determina que accion se va a ejecutar dependiendo de eleccion en el menu
    public boolean onOptionsItemSelected (MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.botonProductos:
                Intent principal = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(principal);
                return true;

            case R.id.botonServicios:
                Intent servicios = new Intent(getApplicationContext(), com.example.tiresportapp.Vista.servicios.class);
                startActivity(servicios);
                return true;

            case R.id.botonSucursales:
                Intent sucursales = new Intent(getApplicationContext(), Sucursales.class);
                startActivity(sucursales);

                return true;

            case R.id.carrito:

                Intent intent = new Intent(this, Carrito.class);
                startActivity(intent);

            default:
                return true;

        }
    }
}
