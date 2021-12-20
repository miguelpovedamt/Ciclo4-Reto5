package com.example.tiresportapp.Controlador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tiresportapp.Casodeuso.WebService;
import com.example.tiresportapp.Modelo.MySingleton;
import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.Modelo.Sucursal;
import com.example.tiresportapp.R;
import com.example.tiresportapp.Vista.Catalogo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Peticiones {




    public static void peticionPut(Producto producto,Context context){


        JSONObject json = new JSONObject();

        try {
            json.put("id", producto.getId());
            json.put("nombre", producto.getNombre());
            json.put("precio", producto.getPrecio());
            json.put("inventario", producto.getInventario());
            json.put("cantidad", producto.getCantidad());
            json.put("estadofav", producto.getEstadoFav());
            json.put("imagen", producto.getImagen());

        }catch (Exception e){
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, WebService.GET_POST_PUT_PRODUCTOS, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String test = response.toString();
                            Log.e("Respuesta", "Volley: " + test);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "VolleyError error: " + error.getMessage());

            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {


                return super.parseNetworkResponse(response);
            }
        };


        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }


}
