package com.com.grupocontinental;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdadpterListaAutos extends RecyclerView.Adapter<AdadpterListaAutos.ViewHolderRecycler> {
    private ArrayList<ListaAutos> autosRecycler;
    AdadpterListaAutos.ViewHolderRecycler viewholderListaGastos;
    private RecyclerView recyclerView;
    private Context context;
    private int cantidad;
    private String id, marca, modelo, precio, link,foto_1,foto_2,foto_3;


    public AdadpterListaAutos(ArrayList<ListaAutos> autosRecycler) {
        this.autosRecycler = autosRecycler;
    }

    @Override
    public AdadpterListaAutos.ViewHolderRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_autos, parent, false);
        context = parent.getContext();
        vista.setFocusable(true);
        return new AdadpterListaAutos.ViewHolderRecycler(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdadpterListaAutos.ViewHolderRecycler holder, int position) {
        viewholderListaGastos = holder;
        id = autosRecycler.get(position).getId();
        marca = autosRecycler.get(position).getMarca();
        modelo = autosRecycler.get(position).getModelo();
        precio = autosRecycler.get(position).getPrecio();
        link = autosRecycler.get(position).getLink();
        foto_1= autosRecycler.get(position).getFoto_1();
        foto_2= autosRecycler.get(position).getFoto_2();
        foto_3= autosRecycler.get(position).getFoto_3();
        String url_server="http://192.168.0.11:8888/grupoContinental/vista/img/autos/";

        holder.marca_tv.setText(marca);
        holder.modelo_tv.setText(modelo);
        holder.precio_tv.setText("$"+String.valueOf(precio));
        Picasso.get().load(url_server+foto_1).into(holder.foto_1_tv);
        holder.mas_Informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posiTion2 = holder.getAdapterPosition();
                id = autosRecycler.get(posiTion2).getId();
                marca = autosRecycler.get(posiTion2).getMarca();
                modelo = autosRecycler.get(posiTion2).getModelo();
                precio = autosRecycler.get(posiTion2).getPrecio();
                link = autosRecycler.get(posiTion2).getLink();
                foto_1 = autosRecycler.get(posiTion2).getFoto_1();
                foto_2 = autosRecycler.get(posiTion2).getFoto_2();
                foto_3 = autosRecycler.get(posiTion2).getFoto_3();
                ((Principal) context).masInformacion(id,marca,modelo,precio,link,foto_1,foto_2,foto_3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return autosRecycler.size();
    }

    public class ViewHolderRecycler extends RecyclerView.ViewHolder {

        TextView marca_tv, modelo_tv, precio_tv,mas_Informacion;
        LinearLayout caja_info_deudor;
        ImageView foto_1_tv;


        public ViewHolderRecycler(View itemView) {
            super(itemView);
            marca_tv=itemView.findViewById(R.id.marca);
            modelo_tv=itemView.findViewById(R.id.modelo);
            precio_tv=itemView.findViewById(R.id.precio);
            foto_1_tv=itemView.findViewById(R.id.foto_1_tv);
            mas_Informacion=itemView.findViewById(R.id.mas_Informacion);
        }
    }
}
