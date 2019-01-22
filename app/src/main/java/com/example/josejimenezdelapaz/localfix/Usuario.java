package com.example.josejimenezdelapaz.localfix;

public class Usuario {

    private String nombreUsuario;

    public Usuario (){
        this.nombreUsuario = "";
    }

    public Usuario(String nombreUsuario){
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario(){
        return this.nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario){
        this.nombreUsuario = nombreUsuario;
    }

}
