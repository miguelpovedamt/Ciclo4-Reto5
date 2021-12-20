package com.example.tiresportapp.Modelo;

public class Sucursal {

    private int id;
    private String ciudad;
    private String direccion;
    private Double longitud;
    private Double latitud;
    private int imagen;

    public Sucursal(int id, String ciudad, String direccion, Double longitud, Double latitud, int imagen) {
        this.id = id;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.longitud = longitud;
        this.latitud = latitud;
        this.imagen = imagen;
    }

    public Sucursal(String ciudad, String direccion, Double longitud, Double latitud, int imagen) {
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.longitud = longitud;
        this.latitud = latitud;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}
