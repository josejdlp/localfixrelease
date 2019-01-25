package com.example.josejimenezdelapaz.localfix;

import java.io.Serializable;

public class Valoracion implements Serializable {

    private String uid;
    private float puntuacion;

    public Valoracion(){
        uid = "";
        puntuacion = 0;
    }

    public Valoracion(String uid, float puntuacion){
        this.uid = uid;
        this.puntuacion = puntuacion;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public String getUid() {
        return uid;
    }
}
