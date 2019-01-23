package com.example.josejimenezdelapaz.localfix;
import java.io.Serializable;

public class Comentario implements Serializable {

    private String texto;
    private String autor;

    public Comentario () {
        texto = "";
        autor = "";
    }

    public Comentario (String texto, String autor){
        this.texto = texto;
        this.autor = autor;
    }

    public String getTexto(){
        return this.texto;
    }

    public String getAutor(){
        return this.autor;
    }


}
