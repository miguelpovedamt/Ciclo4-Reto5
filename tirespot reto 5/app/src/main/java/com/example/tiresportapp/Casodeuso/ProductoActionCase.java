package com.example.tiresportapp.Casodeuso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;

import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.Controlador.MyOpenHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class ProductoActionCase {


    public static void setCantidad(Integer id,Integer cantidad,Context context){

        MyOpenHelper dataBase = new MyOpenHelper(context);
        SQLiteDatabase db = dataBase.getWritableDatabase();

        try{
            dataBase.cambiarCantidad(id, cantidad, db);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

    public static void vaciarCarrito(Context context){
        MyOpenHelper dataBase = new MyOpenHelper(context);
        SQLiteDatabase db = dataBase.getReadableDatabase();

        ArrayList<Producto> productos = consultarProductos(context);

        try{
            for (Producto l: productos
                 ) {
                dataBase.cambiarCantidad(l.getId(), 0, db);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

    public static ArrayList<Producto> consultarProductos(Context context){
        ArrayList<Producto> productos = new ArrayList<>();

        MyOpenHelper dataBase = new MyOpenHelper(context);
        SQLiteDatabase db = dataBase.getReadableDatabase();


        Cursor c = db.query("productos", null,null,null,null,null,null);
        if (c != null){
            c.moveToFirst();

            do{
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex("id"));
                @SuppressLint("Range") String nombre = c.getString(c.getColumnIndex("nombre"));
                @SuppressLint("Range") int precio = c.getInt(c.getColumnIndex("precio"));
                @SuppressLint("Range") int inventario = c.getInt(c.getColumnIndex("inventario"));
                @SuppressLint("Range") int cantidad = c.getInt(c.getColumnIndex("cantidad"));
                @SuppressLint("Range") int estadoFav = c.getInt(c.getColumnIndex("estadofav"));
                @SuppressLint("Range") String imagen = c.getString(c.getColumnIndex("imagen"));

                productos.add(new Producto(id,nombre,precio,inventario,cantidad,estadoFav,imagen));
            } while (c.moveToNext());
            c.close();
            db.close();
        }return productos;
    }

    public static String formatearNumero(int numero){

        DecimalFormatSymbols punto = new DecimalFormatSymbols();
        punto.setGroupingSeparator('.');
        DecimalFormat formatoNumero= new DecimalFormat("###,###,###",punto);
        String numeroFormateado = "$"+formatoNumero.format(numero*1.0);

        return numeroFormateado;

    }

    public static void setFavorito(Integer id,Integer esFavorito,Context context){

        MyOpenHelper dataBase = new MyOpenHelper(context);
        SQLiteDatabase db = dataBase.getWritableDatabase();

        try{
            dataBase.alternarFavorito(id, esFavorito, db);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

}
