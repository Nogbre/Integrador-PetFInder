package com.example.petfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petfinder.Models.Publicacion;

import java.util.List;

public class EncontradaAdapter extends RecyclerView.Adapter<EncontradaAdapter.EncontradaViewHolder> {

    private List<Publicacion> publicaciones;

    // Constructor
    public EncontradaAdapter(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    @NonNull
    @Override
    public EncontradaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de item_encontrada.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encontrada, parent, false);
        return new EncontradaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EncontradaViewHolder holder, int position) {
        // Obtener la publicación actual
        Publicacion publicacion = publicaciones.get(position);

        // Configurar la especie

        // Configurar la raza
        String raza = publicacion.getRaza();
        if (raza == null || raza.isEmpty()) {
            holder.razaTextView.setText("Raza: Desconocida");
        } else {
            holder.razaTextView.setText("Raza: " + raza);
        }

        // Cargar la foto de la mascota usando Glide
        Glide.with(holder.itemView.getContext())
                .load(publicacion.getFoto())
                .into(holder.fotoImageView);
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

    // Método para actualizar los datos del Adapter
    public void updateData(List<Publicacion> nuevasPublicaciones) {
        this.publicaciones = nuevasPublicaciones;
        notifyDataSetChanged();
    }

    // Clase ViewHolder interna
    public static class EncontradaViewHolder extends RecyclerView.ViewHolder {
        TextView razaTextView;
        ImageView fotoImageView;

        public EncontradaViewHolder(@NonNull View itemView) {
            super(itemView);
            razaTextView = itemView.findViewById(R.id.mascota_raza_encontrada);
            fotoImageView = itemView.findViewById(R.id.mascota_foto_encontrada);
        }
    }
}
