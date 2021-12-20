package com.example.tiresportapp.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tiresportapp.Modelo.Sucursal;
import com.example.tiresportapp.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AdaptadorSucursales extends ArrayAdapter{

    ArrayList<Sucursal> sucursales;
    Context context;


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return CustomSpinner(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return CustomSpinner(position,convertView,parent);
    }

    public AdaptadorSucursales(Context context, ArrayList<Sucursal> sucursales){
        super(context, R.layout.itemsucursal,sucursales);
        this.sucursales = sucursales;
        this.context = context;
    }

    public View CustomSpinner(int posicion, View convertView, ViewGroup parent){

        if(convertView==null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itemsucursal, parent, false);

        }

        ImageView imagen = convertView.findViewById(R.id.imagenSucursal);

        imagen.setImageResource(sucursales.get(posicion).getImagen());

        TextView sucursalCiudad = convertView.findViewById(R.id.sucursalCiudadText);
        sucursalCiudad.setText(sucursales.get(posicion).getCiudad());

        TextView sucursalDireccion = convertView.findViewById(R.id.sucursalDireccionText);
        sucursalDireccion.setText(sucursales.get(posicion).getDireccion());

        return convertView;
    }
}
