package com.example.tiresportapp.Controlador;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiresportapp.Casodeuso.ProductoActionCase;
import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.ViewHolderCarrito> {

    ArrayList<Producto> productos;
    Context context;


    public AdaptadorCarrito(ArrayList<Producto> productos, Context context) {

        this.productos = productos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderCarrito onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcarrito, null, false);
        return new ViewHolderCarrito(vista,context);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCarrito holder, int position) {
/*
        Picasso.get().load(productos.get(position).getImagen()).into(holder.cartImagen);
        holder.precioUCart.setText(ProductoActionCase.formatearNumero(productos.get(position).getPrecio()));
        holder.cantidadCart.setText(""+productos.get(position).getCantidad());
        holder.subtotalCart.setText(ProductoActionCase.formatearNumero(productos.get(position).getPrecio()*productos.get(position).getCantidad()));
*/

    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class ViewHolderCarrito extends RecyclerView.ViewHolder{

        ImageView cartImagen;
        TextView precioUCart;
        TextView cantidadCart;
        TextView subtotalCart;
        ImageButton btnAumentarCart;
        ImageButton btnDisminuirCart;

        public ViewHolderCarrito(@NonNull View itemView,Context context) {
            super(itemView);
/*
            cartImagen = (ImageView) itemView.findViewById(R.id.imageCarritoItem);
            precioUCart = (TextView) itemView.findViewById(R.id.precioUCarritoItem);
            cantidadCart = (TextView) itemView.findViewById(R.id.CantCarritoItem);
            subtotalCart = (TextView) itemView.findViewById(R.id.SubtotalCarritoItem);
            btnAumentarCart = (ImageButton) itemView.findViewById(R.id.btnAumntarItem);
            btnDisminuirCart = (ImageButton) itemView.findViewById(R.id.btnDisminuirItem);
*/

        }

    }

}
