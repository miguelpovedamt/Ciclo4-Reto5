package com.example.tiresportapp.Vista;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tiresportapp.Casodeuso.WebService;
import com.example.tiresportapp.Controlador.AdaptadorCarrito;
import com.example.tiresportapp.Controlador.AdaptadorProductos;
import com.example.tiresportapp.Controlador.Peticiones;
import com.example.tiresportapp.Modelo.MySingleton;
import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.Casodeuso.ProductoActionCase;
import com.example.tiresportapp.Controlador.MyOpenHelper;
import com.example.tiresportapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class Carrito extends AppCompatActivity {

    ArrayList<Producto> listaCarrito;
    private Double precioTotal;
    private RecyclerView rcvCarrito;
    ProgressDialog progressDialog;

    int azulPrincipal;
    int naranjaPrincipal;
    int blanco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);




        ActionBar barraMenu = getSupportActionBar();

        barraMenu.setDisplayShowHomeEnabled(true);
        barraMenu.setLogo(R.mipmap.ic_launcher);
        barraMenu.setTitle("Carrito");
        barraMenu.setSubtitle("Mi Compra");
        barraMenu.setDisplayUseLogoEnabled(true);
        barraMenu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#313d8c")));
        new ConsultaProductosCarrito().execute();

    }


    private Boolean verificarCantidad(ArrayList<Producto> productos){

        int contador=0;

        for (Producto producto : productos
             ) {
                if(producto.getCantidad()>=1){
                    contador+=1;
                }
        }if (contador == 0){
            return false;
        }else {
            return true;
        }
    }

    private Double calcularTotal(ArrayList<Producto> listaCar){
        Double precioTotal = 0.0;
        for (Producto l : listaCar
             ) {
                precioTotal += l.getCantidad()*l.getPrecio();
        }
        return precioTotal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menuPrincipal) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimnu,menuPrincipal);
        MenuItem carrito = menuPrincipal.findItem(R.id.carrito);
        carrito.setVisible(false);
        MenuItem buscar = menuPrincipal.findItem(R.id.buscar);
        buscar.setVisible(false);
        MenuItem mapa = menuPrincipal.findItem((R.id.cambiMapa));
        mapa.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){
        switch (menuItem.getItemId())
        {
            case R.id.botonProductos:
                Intent principal = new Intent(getApplicationContext(), Catalogo.class );
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

            default:
                return true;

        }

    }

    private void lanzarDialogVaciarCar(ArrayList<Producto> Ps){
        DialogDeConfirmacion ddc = new DialogDeConfirmacion();
        ConstraintLayout principalCarrito = (ConstraintLayout) findViewById(R.id.principalCarrito);
        ddc.vaciar(principalCarrito,Ps);
        ddc.show(getSupportFragmentManager(), "DialogDeConfirmacion");

    }

    public static class DialogDeConfirmacion extends DialogFragment {

        ConstraintLayout principalCarrito;
        ArrayList<Producto> Ps;

        public void vaciar(ConstraintLayout constraintLayout,ArrayList<Producto> Ps) {
            this.principalCarrito = constraintLayout;
            this.Ps = Ps;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("¿Usted está seguro de vaciar el carrito")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //ProductoActionCase.vaciarCarrito(getActivity());

                            for (Producto p: Ps) {
                                p.setCantidad(0);
                                Peticiones.peticionPut(p,getActivity());
                            }

                            getActivity().finish();

                            Intent intent = new Intent(getActivity(),Carrito.class);
                            getActivity().startActivity(intent);
                            Snackbar miSnackBar = Snackbar.make(principalCarrito,"Usted ha vaciado el carrito",4000);
                            miSnackBar.show();


                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "Usted ha cancelado la operación", Toast.LENGTH_LONG).show();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }




    public  class ConsultaProductosCarrito extends AsyncTask<Void, Void, Void> {

        ArrayList<Producto> listaCarrito = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Carrito.this);
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
                                        listaCarrito.add(producto);
                                        Log.e("response", response.toString());

                                    }
                                }catch (Exception e){
                                    Log.e("Exceptión EX", e.getMessage());
                                }
                                /*
                                rcvCarrito = (RecyclerView) findViewById(R.id.rcvCarrito);
                                rcvCarrito.setHasFixedSize(false);
                                rcvCarrito.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                AdaptadorCarrito adaptadorcarrito = new AdaptadorCarrito(listaCarrito,Carrito.this);
                                rcvCarrito.setAdapter(adaptadorcarrito);

                                */

                                crearVista(listaCarrito);


                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Log.e("REST", error.toString());
                                progressDialog.cancel();
                            }
                        });
                MySingleton.getInstance(Carrito.this).addToRequestQueue(jsonObjectRequest);

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

    private void crearVista(ArrayList<Producto> listaCarrito) {

        int azulPrincipal = getResources().getColor(R.color.azulPrincipal);
        int naranjaPrincipal = getResources().getColor(R.color.naranjaPrincipal);
        int blanco = getResources().getColor(R.color.white);

        ConstraintLayout principalCarrito = (ConstraintLayout) findViewById(R.id.principalCarrito);
        principalCarrito.setBackground(new ColorDrawable(blanco));

        ScrollView scroll = new ScrollView(Carrito.this);
        int anchoMatch = ScrollView.LayoutParams.MATCH_PARENT;
        int altoWrap = ScrollView.LayoutParams.WRAP_CONTENT;
        scroll.setLayoutParams(new ConstraintLayout.LayoutParams(anchoMatch,altoWrap));
        scroll.setBackground(new ColorDrawable(blanco));
        scroll.setId(View.generateViewId());

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(principalCarrito);
        constraintSet.connect(scroll.getId(), ConstraintSet.LEFT,principalCarrito.getId(),ConstraintSet.LEFT);
        constraintSet.connect(scroll.getId(),ConstraintSet.RIGHT,principalCarrito.getId(),ConstraintSet.RIGHT);
        constraintSet.connect(scroll.getId(),ConstraintSet.TOP,principalCarrito.getId(),ConstraintSet.TOP);
        constraintSet.connect(scroll.getId(),ConstraintSet.BOTTOM,principalCarrito.getId(),ConstraintSet.BOTTOM);

        principalCarrito.addView(scroll);



        if(verificarCantidad(listaCarrito)==false){

            LinearLayout layoutVacio = new LinearLayout(Carrito.this);
            layoutVacio.setOrientation(LinearLayout.VERTICAL);
            layoutVacio.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layoutVacioParams = new LinearLayout.LayoutParams(anchoMatch,altoWrap);
            layoutVacioParams.setMargins(0,0,0,0);
            layoutVacio.setLayoutParams(layoutVacioParams);
            principalCarrito.addView(layoutVacio);


            ImageView triste = new ImageView(Carrito.this);
            triste.setImageResource(R.drawable.carritovacio);
            LinearLayout.LayoutParams parametrosTriste = new LinearLayout.LayoutParams(anchoMatch,anchoMatch);
            triste.setLayoutParams(parametrosTriste);
            layoutVacio.addView(triste);

        }else {

            TableLayout tablaCarrito = new TableLayout(Carrito.this);
            TableLayout.LayoutParams tablaParams = new TableLayout.LayoutParams(anchoMatch, altoWrap);
            tablaCarrito.setLayoutParams(tablaParams);
            scroll.addView(tablaCarrito);
            tablaCarrito.setBackground(new ColorDrawable(blanco));
            TableRow titulos = new TableRow(Carrito.this);

            titulos.setLayoutParams(new TableRow.LayoutParams(anchoMatch, altoWrap));

            LinearLayout layoutProducto = new LinearLayout(Carrito.this);
            layoutProducto.setOrientation(LinearLayout.VERTICAL);
            layoutProducto.setGravity(Gravity.CENTER);
            TableRow.LayoutParams parametros = new TableRow.LayoutParams(anchoMatch, 100, 1);
            parametros.setMargins(0, 10, 0, 0);
            layoutProducto.setLayoutParams(parametros);

            TextView textoProducto = new TextView(Carrito.this);
            textoProducto.setText("Producto");
            TableRow.LayoutParams parametrosText = new TableRow.LayoutParams(anchoMatch, altoWrap, 1);
            textoProducto.setTextSize(17);
            textoProducto.setGravity(Gravity.CENTER);
            textoProducto.setMaxLines(1);
            textoProducto.setTypeface(null, Typeface.BOLD);
            textoProducto.setTextColor(azulPrincipal);
            titulos.addView(layoutProducto);
            textoProducto.setLayoutParams(parametrosText);
            layoutProducto.addView(textoProducto);

            LinearLayout layoutPrecio = new LinearLayout(Carrito.this);
            layoutPrecio.setOrientation(LinearLayout.VERTICAL);
            layoutPrecio.setGravity(Gravity.CENTER);
            layoutPrecio.setLayoutParams(parametros);

            TextView textoPrecio = new TextView(Carrito.this);
            textoPrecio.setText("Precio/u");
            textoPrecio.setTypeface(null, Typeface.BOLD);
            textoPrecio.setTextSize(17);
            textoPrecio.setGravity(Gravity.CENTER);
            textoPrecio.setMaxLines(1);
            textoPrecio.setLayoutParams(parametrosText);
            textoPrecio.setTextColor(azulPrincipal);
            titulos.addView(layoutPrecio);
            layoutPrecio.addView(textoPrecio);

            LinearLayout layoutCantidadTit = new LinearLayout(Carrito.this);
            layoutCantidadTit.setOrientation(LinearLayout.VERTICAL);
            layoutCantidadTit.setGravity(Gravity.CENTER);
            layoutCantidadTit.setLayoutParams(parametros);

            TextView textoCantidad = new TextView(Carrito.this);
            textoCantidad.setText("Cantidad");
            textoCantidad.setTypeface(null, Typeface.BOLD);
            textoCantidad.setTextSize(17);
            textoCantidad.setGravity(Gravity.CENTER);
            textoCantidad.setLines(1);
            textoCantidad.setLayoutParams(parametrosText);
            textoCantidad.setTextColor(azulPrincipal);
            titulos.addView(layoutCantidadTit);
            layoutCantidadTit.addView(textoCantidad);

            LinearLayout layoutSubtotal = new LinearLayout(Carrito.this);
            layoutSubtotal.setOrientation(LinearLayout.VERTICAL);
            layoutSubtotal.setGravity(Gravity.CENTER);
            layoutSubtotal.setLayoutParams(parametros);

            TextView textoSubtotal = new TextView(Carrito.this);
            textoSubtotal.setText("Subtotal");
            textoSubtotal.setTypeface(null, Typeface.BOLD);
            textoSubtotal.setTextSize(17);
            textoSubtotal.setGravity(Gravity.CENTER);
            textoSubtotal.setMaxLines(1);
            textoSubtotal.setLayoutParams(parametrosText);
            textoSubtotal.setTextColor(azulPrincipal);
            titulos.addView(layoutSubtotal);
            layoutSubtotal.addView(textoSubtotal);

            tablaCarrito.addView(titulos);

            LinearLayout divisor = new LinearLayout(Carrito.this);
            divisor.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams diviParams = new TableRow.LayoutParams(anchoMatch, 8);
            diviParams.setMargins(0, 20, 0, 10);
            divisor.setLayoutParams(diviParams);
            divisor.setBackground(new ColorDrawable(naranjaPrincipal));

            tablaCarrito.addView(divisor);

            DecimalFormatSymbols punto = new DecimalFormatSymbols();
            punto.setGroupingSeparator('.');
            DecimalFormat formatoNumero = new DecimalFormat("###,###,###", punto);

            TextView precioTotalView = new TextView(Carrito.this);

            for (Producto l : listaCarrito) {

                if (l.getCantidad() >= 1) {

                    TableRow productoRow = new TableRow(Carrito.this);
                    productoRow.setLayoutParams(new TableRow.LayoutParams(anchoMatch, altoWrap));
                    tablaCarrito.addView(productoRow);
                    productoRow.setGravity(Gravity.CENTER);

                    LinearLayout layoutImage = new LinearLayout(Carrito.this);
                    layoutImage.setOrientation(LinearLayout.VERTICAL);
                    layoutImage.setLayoutParams(new TableRow.LayoutParams(altoWrap, altoWrap, 1));

                    productoRow.addView(layoutImage);

                    ImageView imagenProducto = new ImageView(Carrito.this);
                    Picasso.get().load(l.getImagen()).into(imagenProducto);
                    TableRow.LayoutParams imagenPrParams = new TableRow.LayoutParams(150, 150);
                    imagenPrParams.setMargins(30, 0, 0, 0);
                    imagenProducto.setLayoutParams(imagenPrParams);

                    layoutImage.addView(imagenProducto);

                    LinearLayout layoutPrecioPr = new LinearLayout(Carrito.this);
                    layoutPrecioPr.setOrientation(LinearLayout.VERTICAL);
                    layoutPrecioPr.setGravity(Gravity.CENTER);
                    layoutPrecioPr.setLayoutParams(new TableRow.LayoutParams(anchoMatch, altoWrap));

                    productoRow.addView(layoutPrecioPr);

                    TextView precioProducto = new TextView(Carrito.this);
                    precioProducto.setText("$" + formatoNumero.format(l.getPrecio() * 1.0));
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(precioProducto, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    precioProducto.setTypeface(null, Typeface.BOLD);
                    precioProducto.setTextSize(17f);
                    precioProducto.setGravity(Gravity.CENTER);
                    precioProducto.setMaxLines(1);
                    LinearLayout.LayoutParams text2Params = new TableRow.LayoutParams(anchoMatch, altoWrap, 1);
                    text2Params.setMargins(20, 0, 30, 0);
                    precioProducto.setLayoutParams(text2Params);
                    precioProducto.setTextColor(azulPrincipal);

                    layoutPrecioPr.addView(precioProducto);

                    LinearLayout layoutCantidadPr = new LinearLayout(Carrito.this);
                    layoutCantidadPr.setOrientation(LinearLayout.HORIZONTAL);
                    layoutCantidadPr.setGravity(Gravity.CENTER);
                    layoutCantidadPr.setLayoutParams(new TableRow.LayoutParams(anchoMatch, altoWrap, 1));

                    productoRow.addView(layoutCantidadPr);

                    TextView cantProducto = new TextView(Carrito.this);
                    TextView precioSubTotal = new TextView(Carrito.this);

                    Button btnBajar = new Button(Carrito.this);
                    LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(50, 50);
                    btnParams.setMargins(10, 10, 0, 15);
                    btnBajar.setLayoutParams(btnParams);
                    btnBajar.setTextColor(azulPrincipal);
                    btnBajar.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    btnBajar.setPadding(0, 0, 0, 0);
                    btnBajar.setTypeface(null, Typeface.BOLD);
                    btnBajar.setText("<");
                    btnBajar.setTextSize(20f);
                    btnBajar.setBackgroundTintList(ColorStateList.valueOf(blanco));


                    btnBajar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (l.getCantidad() > 1) {
                                l.setCantidad(l.getCantidad() - 1);
                                cantProducto.setText("" + l.getCantidad());
                                precioSubTotal.setText("$" + formatoNumero.format(l.getPrecio() * 1.0 * l.getCantidad()));
                                precioTotalView.setText(String.valueOf("Total:    $ " + formatoNumero.format(calcularTotal(listaCarrito))));
                            }
                        }
                    });

                    layoutCantidadPr.addView(btnBajar);

                    cantProducto.setText("" + l.getCantidad());
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(cantProducto, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    cantProducto.setTypeface(null, Typeface.BOLD);
                    cantProducto.setGravity(Gravity.CENTER);
                    cantProducto.setMaxLines(1);
                    LinearLayout.LayoutParams text3Params = new LinearLayout.LayoutParams(anchoMatch, altoWrap, 2);
                    text3Params.setMargins(10, 0, 10, 0);
                    cantProducto.setLayoutParams(text3Params);
                    cantProducto.setTextColor(azulPrincipal);

                    layoutCantidadPr.addView(cantProducto);

                    Button btnSubir = new Button(Carrito.this);
                    LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(50, 50);
                    btnParams2.setMargins(0, 10, 20, 15);
                    btnSubir.setLayoutParams(btnParams2);
                    btnSubir.setTextColor(azulPrincipal);
                    btnSubir.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    btnSubir.setPadding(0, 0, 0, 0);
                    btnSubir.setTypeface(null, Typeface.BOLD);
                    btnSubir.setText(">");
                    btnSubir.setTextSize(20f);
                    btnSubir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (l.getCantidad() < 39) {
                                l.setCantidad(l.getCantidad() + 1);
                                cantProducto.setText("" + l.getCantidad());
                                precioSubTotal.setText("$" + formatoNumero.format(l.getPrecio() * 1.0 * l.getCantidad()));
                                precioTotalView.setText(String.valueOf("Total:    $ " + formatoNumero.format(calcularTotal(listaCarrito))));

                            }
                        }
                    });
                    btnSubir.setBackgroundTintList(ColorStateList.valueOf(blanco));

                    layoutCantidadPr.addView(btnSubir);

                    LinearLayout layoutprecioSubTotal = new LinearLayout(Carrito.this);
                    layoutprecioSubTotal.setOrientation(LinearLayout.VERTICAL);
                    layoutprecioSubTotal.setGravity(Gravity.CENTER);
                    layoutprecioSubTotal.setLayoutParams(new TableRow.LayoutParams(anchoMatch, altoWrap));

                    productoRow.addView(layoutprecioSubTotal);

                    precioSubTotal.setText("$" + formatoNumero.format(l.getPrecio() * 1.0 * l.getCantidad()));
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(precioSubTotal, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    precioSubTotal.setTypeface(null, Typeface.BOLD);
                    precioSubTotal.setGravity(Gravity.CENTER);
                    precioSubTotal.setMaxLines(1);
                    LinearLayout.LayoutParams precioSubTotalParam = new LinearLayout.LayoutParams(anchoMatch, altoWrap);
                    precioSubTotalParam.setMargins(20, 0, 30, 0);
                    precioSubTotal.setLayoutParams(precioSubTotalParam);
                    precioSubTotal.setTextColor(azulPrincipal);

                    layoutprecioSubTotal.addView(precioSubTotal);

                    LinearLayout divisor2 = new LinearLayout(Carrito.this);
                    divisor2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams diviParams2 = new LinearLayout.LayoutParams(anchoMatch, 4);
                    divisor2.setLayoutParams(diviParams2);
                    divisor2.setBackground(new ColorDrawable(naranjaPrincipal));


                    tablaCarrito.addView(divisor2);
                }

            }

            LinearLayout barraInferior = new LinearLayout(Carrito.this);
            barraInferior.setOrientation(LinearLayout.VERTICAL);

            ConstraintLayout.LayoutParams parametros2 = new ConstraintLayout.LayoutParams(anchoMatch, altoWrap);
            parametros2.verticalBias = 1f;
            barraInferior.setLayoutParams(parametros2);
            barraInferior.setBackground(new ColorDrawable(blanco));

            LinearLayout divisor2 = new LinearLayout(Carrito.this);
            divisor2.setOrientation(LinearLayout.HORIZONTAL);
            divisor2.setLayoutParams(diviParams);
            divisor2.setBackground(new ColorDrawable(naranjaPrincipal));

            barraInferior.addView(divisor2);

            LinearLayout layoutprecioTotal = new LinearLayout(Carrito.this);
            layoutprecioTotal.setOrientation(LinearLayout.HORIZONTAL);
            layoutprecioTotal.setGravity(Gravity.CENTER);
            layoutprecioTotal.setLayoutParams(new TableRow.LayoutParams(anchoMatch, altoWrap));

            barraInferior.addView(layoutprecioTotal);


            precioTotalView.setText(String.valueOf("Total:    $ " + formatoNumero.format(calcularTotal(listaCarrito))));
            precioTotalView.setTypeface(null, Typeface.BOLD);
            precioTotalView.setGravity(Gravity.RIGHT);
            precioTotalView.setMaxLines(1);
            precioTotalView.setTextSize(20);
            LinearLayout.LayoutParams precioTotalParam = new LinearLayout.LayoutParams(anchoMatch, altoWrap);
            precioTotalParam.setMargins(30, 30, 30, 30);
            precioTotalView.setLayoutParams(precioTotalParam);
            precioTotalView.setTextColor(azulPrincipal);

            layoutprecioTotal.addView(precioTotalView);


            Button btnComprarCarrito = new Button(Carrito.this);
            btnComprarCarrito.setLayoutParams(new LinearLayout.LayoutParams(anchoMatch, altoWrap));
            LinearLayout.LayoutParams btnCompraParams = new LinearLayout.LayoutParams(anchoMatch, altoWrap);
            btnCompraParams.setMargins(30, 30, 30, 10);
            btnComprarCarrito.setLayoutParams(btnCompraParams);
            barraInferior.addView(btnComprarCarrito);
            btnComprarCarrito.setText("Continuar con el pago");
            btnComprarCarrito.setTextSize(20);
            btnComprarCarrito.setTextColor(blanco);
            btnComprarCarrito.setTypeface(null, Typeface.BOLD);
            btnComprarCarrito.setBackgroundTintList(ColorStateList.valueOf(naranjaPrincipal));
            btnComprarCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Carrito.this, "Se habilitara pronto", Toast.LENGTH_SHORT).show();
                }
            });

            Button btnVaciarCarrito = new Button(Carrito.this);
            btnVaciarCarrito.setLayoutParams(new LinearLayout.LayoutParams(anchoMatch, altoWrap));
            LinearLayout.LayoutParams btnVaciarParams = new LinearLayout.LayoutParams(anchoMatch, altoWrap);
            btnVaciarParams.setMargins(30, 10, 30, 50);
            btnVaciarCarrito.setLayoutParams(btnVaciarParams);
            barraInferior.addView(btnVaciarCarrito);
            btnVaciarCarrito.setText("Vaciar carrito de compras");
            btnVaciarCarrito.setTextSize(15);
            btnVaciarCarrito.setTextColor(blanco);
            btnVaciarCarrito.setTypeface(null, Typeface.BOLD);
            btnVaciarCarrito.setBackgroundTintList(ColorStateList.valueOf(azulPrincipal));
            btnVaciarCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lanzarDialogVaciarCar(listaCarrito);

                }
            });

            barraInferior.setId(View.generateViewId());

            principalCarrito.addView(barraInferior);
            constraintSet.clone(principalCarrito);
            constraintSet.connect(barraInferior.getId(), ConstraintSet.LEFT, principalCarrito.getId(), ConstraintSet.LEFT);
            constraintSet.connect(barraInferior.getId(), ConstraintSet.RIGHT, principalCarrito.getId(), ConstraintSet.RIGHT);
            constraintSet.connect(barraInferior.getId(), ConstraintSet.TOP, scroll.getId(), ConstraintSet.TOP);
            constraintSet.connect(barraInferior.getId(), ConstraintSet.BOTTOM, principalCarrito.getId(), ConstraintSet.BOTTOM);

            constraintSet.applyTo(principalCarrito);


        }

        }

}

