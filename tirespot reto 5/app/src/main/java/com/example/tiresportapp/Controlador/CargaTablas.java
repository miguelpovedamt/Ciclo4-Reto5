package com.example.tiresportapp.Controlador;

import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.Modelo.Sucursal;
import com.example.tiresportapp.R;

import java.util.ArrayList;

public class CargaTablas {
    public static ArrayList<Producto> cargaInicial= Llantas();
    public static ArrayList<Sucursal> sucursales;

    public static ArrayList<Producto> Llantas(){

        ArrayList<Producto> listaProductos = new ArrayList<>();

        listaProductos.add(new Producto("LLanta goodyear R13",250000,100,0,0, "https://previews.123rf.com/images/alexlmx/alexlmx2008/alexlmx200800364/153914171-winter-and-summer-tires-with-car-rim-tire-fitting-concept-3d-rendering-isolated-on-white-background.jpg"));
        listaProductos.add(new Producto("LLanta Michelin R14",175000,100,0,0,"https://previews.123rf.com/images/nobilior/nobilior2007/nobilior200700057/153443456-.jpg"));
        listaProductos.add(new Producto("LLanta goodyear R15",110000,100,0,0,"https://previews.123rf.com/images/archman/archman1110/archman111000269/11047361-ruedas-de-coches-en-el-fondo-blanco-.jpg?fj=1"));

        return listaProductos;
    }

    public  static ArrayList<Sucursal> sucursalesCarga(){

        ArrayList<Sucursal> sucursales = new ArrayList<>();

        sucursales.add(new Sucursal(1, "Bogota", "Calle 73 #11c-89", -74.0755119, 4.6682703, R.drawable.suc1));
        sucursales.add(new Sucursal(2, "Medellin", "Cra. 65 #25-23", -75.5858072, 6.2269233, R.drawable.suc2));
        sucursales.add(new Sucursal(3, "Cali", "Calle 49 #15-22", -76.5001088, 3.4104391, R.drawable.suc3));
        return sucursales;
    }
}
