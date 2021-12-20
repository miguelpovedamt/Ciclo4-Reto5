package com.example.tiresportapp.Controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tiresportapp.Modelo.Producto;

import java.util.ArrayList;

public class MyOpenHelper extends SQLiteOpenHelper {

    private static final String  PRODUCTOS_TABLE_CREATE = "CREATE TABLE productos (id INTEGER " +
            "PRIMARY KEY AUTOINCREMENT, nombre TEXT, precio INTEGER, inventario INTEGER, estadofav INTEGER," +
            " cantidad INTEGER, imagen TEXT)";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "autopartes.db";

    public MyOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        crearTablaProductos(db);
        ArrayList<Producto> productos = CargaTablas.Llantas();

        for (Producto l : productos
             ) {
            insertarProducto(l,db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }
    public void crearTablaProductos (SQLiteDatabase db){
        db.execSQL(PRODUCTOS_TABLE_CREATE);

    }
    public void insertarProducto(Producto l, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("nombre",l.getNombre());
        cv.put("precio",l.getPrecio());
        cv.put("inventario",l.getInventario());
        cv.put("cantidad",l.getCantidad());
        cv.put("imagen",l.getImagen());
        db.insert("productos",null,cv);
    }
    public void borrarProducto(int id, SQLiteDatabase db){
        String[] args = new String[]{""+id};
        db.delete("productos", "id=?",args);
    }
    public Cursor leerTodo(SQLiteDatabase db){
        return db.query("productos", null,null,null,null,null,null);
    }
    public void actualizarProducto(Producto l, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("nombre",l.getNombre());
        cv.put("precio",l.getPrecio());
        cv.put("inventario",l.getInventario());
        cv.put("cantidad",l.getCantidad());
        cv.put("imagen",l.getImagen());
        String[] args = new String[]{""+l.getId()};
        db.update("productos",cv,"id=?",args);
    }

    public  void cambiarCantidad(Integer id,Integer cantidad,SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("cantidad",cantidad);
        String[] arg = new String[]{""+id};
        db.update("productos", cv, "id=?", arg);
    }

    public  void alternarFavorito(Integer id,int esFavorito,SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("estadofav",esFavorito);
        String[] arg = new String[]{""+id};
        db.update("productos", cv, "id=?", arg);
    }

}
