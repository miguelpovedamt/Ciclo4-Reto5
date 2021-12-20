package com.example.tiresportapp.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiresportapp.Casodeuso.ProductoActionCase;
import com.example.tiresportapp.Modelo.Producto;
import com.example.tiresportapp.R;
import com.example.tiresportapp.Vista.Catalogo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolderProductos>{

    ArrayList<Producto> productos;
    Context context;

    public AdaptadorProductos(ArrayList<Producto> productos, Context context) {

        this.productos = productos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderProductos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemproducto, null, false);
        return new ViewHolderProductos(vista,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductos holder, int position) {

        Producto producto = productos.get(position);
        Picasso.get().load(productos.get(position).getImagen()).into(holder.prodImagen);
        holder.produNombre.setText(productos.get(position).getNombre());
        holder.produPrecio.setText(ProductoActionCase.formatearNumero(productos.get(position).getPrecio()));
        holder.producto = producto;
        if(holder.producto.getEstadoFav()==0){
            holder.btnFavorito.setImageResource(R.drawable.heart);
        }else {
            holder.btnFavorito.setImageResource(R.drawable.heart1);
        }


        holder.btnFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(producto.getEstadoFav()==0){


                    holder.btnFavorito.setImageResource(R.drawable.heart1);
                    producto.setEstadoFav(1);
                    Peticiones.peticionPut(producto, context);
                    Toast.makeText(context, "añadio: "+producto.getNombre()+" a favoritos",
                            Toast.LENGTH_SHORT).show();

                }else {

                    holder.btnFavorito.setImageResource(R.drawable.heart);
                    producto.setEstadoFav(0);
                    Peticiones.peticionPut(producto, context);
                    Toast.makeText(context, "quito: "+producto.getNombre()+" de favoritos",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });


        holder.contador = producto.getCantidad();
        holder.btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.producto.getCantidad()==0){
                    holder.producto.setCantidad(1);
                    Peticiones.peticionPut(holder.producto,context);
                    Toast.makeText(context, "Usted agrego: "+
                            producto.getNombre()+" a el carrito", Toast.LENGTH_SHORT).show();
                }else{
                    holder.producto.setCantidad(holder.producto.getCantidad()+1);
                    Peticiones.peticionPut(holder.producto,context);
                    Toast.makeText(context, "Usted aumento en 1: "+
                            producto.getNombre()+" a el carrito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder{

        ImageView prodImagen;
        TextView produNombre;
        TextView produPrecio;
        Producto producto;
        Button btnCarrito;
        Integer contador;
        ImageButton btnFavorito;

        public ViewHolderProductos(@NonNull View itemView,Context context) {
            super(itemView);

            prodImagen = (ImageView) itemView.findViewById(R.id.imagenProducto);
            produNombre = (TextView) itemView.findViewById(R.id.nombreProductoVie);
            produPrecio = (TextView) itemView.findViewById(R.id.precioProductoVie);
            btnCarrito = (Button) itemView.findViewById(R.id.btnCarrito);
            btnFavorito = (ImageButton) itemView.findViewById(R.id.btnFav);

        }

    }
}

