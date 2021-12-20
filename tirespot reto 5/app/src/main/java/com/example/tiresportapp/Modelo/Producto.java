package com.example.tiresportapp.Modelo;


import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable {

    private int id;
    private String nombre;
    private int precio;
    private int inventario;
    private int cantidad;
    private int estadoFav;
    private String imagen;

    public Producto(String nombre, int precio, int inventario, int cantidad, int estadoFav, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.inventario = inventario;
        this.cantidad = cantidad;
        this.estadoFav = estadoFav;
        this.imagen = imagen;
    }

    public Producto(int id, String nombre, int precio, int inventario, int cantidad, int estadoFav, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.inventario = inventario;
        this.cantidad = cantidad;
        this.estadoFav = estadoFav;
        this.imagen = imagen;
    }

    public Producto(String nombre, int precio, int inventario) {
        this.nombre = nombre;
        this.precio = precio;
        this.inventario = inventario;
        this.estadoFav = 0;
        this.cantidad = 0;
    }

    public Producto(String nombre, int precio, int inventario, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.inventario = inventario;
        this.cantidad = 0;
        this.estadoFav = 0;
        this.imagen = imagen;

    }




    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getInventario() {
        return inventario;
    }

    public void setInventario(int inventario) {
        this.inventario = inventario;
    }

    public int getEstadoFav() {
        return estadoFav;
    }

    public void setEstadoFav(int estadoFav) {
        this.estadoFav = estadoFav;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Parcelable.Creator<Producto> CREATOR = new Parcelable.Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
    protected Producto(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        precio = in.readInt();
        inventario = in.readInt();
        cantidad = in.readInt();
        imagen = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeInt(id);
        dest.writeString(nombre);
        dest.writeInt(precio);
        dest.writeInt(inventario);
        dest.writeInt(cantidad);
        dest.writeString(imagen);
    }
}
