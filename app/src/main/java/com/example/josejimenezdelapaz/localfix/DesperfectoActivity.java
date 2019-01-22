package com.example.josejimenezdelapaz.localfix;

import java.util.ArrayList;
import java.io.Serializable;

public class DesperfectoActivity implements Serializable {

    private String id;
    private String autor;
    private String titulo;
    private String latitud;
    private String longitud;
    private String descripcion;
    private float gravedad;
    private String estado;
    private ArrayList<Comentario> comentarios;
    private ArrayList<String> imagenes;//url de las imagenes del desperfecto

    public DesperfectoActivity(){};
    public DesperfectoActivity(String id , String autor, String titulo,String latitud,String longitud ,String descripcion, float gravedad, String estado,ArrayList<Comentario> comentarios,
                               ArrayList<String> imagenes){

        this.id=id;
        this.autor = autor;
        this.titulo=titulo;
        this.latitud=latitud;
        this.longitud=longitud;
        this.descripcion = descripcion;
        this.gravedad = gravedad;
        this.estado=estado;
        this.comentarios = comentarios;
        this.imagenes=imagenes;

    }


    public String getAutor() {
        return autor;
    }


    public float getGravedad() {
        return gravedad;
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
}
