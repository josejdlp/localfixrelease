package com.example.josejimenezdelapaz.localfix;

import java.util.ArrayList;
import java.io.Serializable;

public class Desperfecto implements Serializable {

    private String id;
    private String autor;
    private String titulo;
    private String latitud;
    private String longitud;
    private String descripcion;
    private ArrayList<Valoracion> valoraciones = new ArrayList<Valoracion>(); //Lista de todas las valoraciones
    private String estado; //No aceptado - Aceptado - En reparación - Reparado
    private ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
    private ArrayList<String> imagenes = new ArrayList<String>();//url de las imagenes del desperfecto

    public Desperfecto(){};

    public Desperfecto(String id, String autor
            ,String titulo, String latitud, String longitud
            ,String descripcion, String estado
            ,ArrayList<Comentario> comentarios, ArrayList<String> imagenes, ArrayList<Valoracion> valoraciones){

        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
        this.estado = estado;
        this.comentarios = comentarios;
        this.imagenes = imagenes;
        this.valoraciones = valoraciones;
    }


    public String getAutor() {
        return autor;
    }

    public ArrayList<Comentario> getComentarios() {
        return comentarios;
    }

    public ArrayList<String> getImagenes() {
        return imagenes;
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getEstado() {
        return estado;
    }

    public ArrayList<Valoracion> getValoraciones(){
        return valoraciones;
    }

    public float calcularGravedad(){

        float valoracionTotal = 0;

        for (Valoracion valoracion : valoraciones){
            valoracionTotal += valoracion.getPuntuacion();
        }

        return valoracionTotal / valoraciones.size();
    }
}
