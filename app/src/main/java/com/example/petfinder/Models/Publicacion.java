package com.example.petfinder.Models;

import com.google.gson.annotations.SerializedName;

public class Publicacion {
    @SerializedName("id") // Coincide con el ID en MongoDB
    private String id;

    @SerializedName("nombre") // Solo aplica para Adopción y Perdida
    private String nombre;

    @SerializedName("raza") // Raza del animal
    private String raza;

    @SerializedName("especie") // Especie (Perro, Gato, etc.)
    private String especie;

    @SerializedName("foto") // URL o referencia a la imagen
    private String foto;

    @SerializedName("descripcion") // Información adicional
    private String descripcion;

    @SerializedName("estado")
    private String estado;

    @SerializedName("ubicacion") // Relación con la ubicación (latitud y longitud)
    private Ubicacion ubicacion;

    // Constructor vacío (obligatorio para Gson)
    public Publicacion() {}

    // Constructor para crear una publicación con todos los campos
    public Publicacion(String nombre, String raza, String especie, String foto, String descripcion, String tipo, Ubicacion ubicacion) {
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        this.foto = foto;
        this.descripcion = descripcion;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }
}
