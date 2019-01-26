package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

public class Filtro extends AppCompatActivity {


    private CheckBox no_admitidos;
    private CheckBox admitidos;
    private CheckBox en_reparacion;
    private CheckBox reparados;

    private CheckBox ordenar_fecha;
    private CheckBox ordenar_comentarios;
    private CheckBox ordenar_gravedad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_filtro);

        no_admitidos = (CheckBox) findViewById(R.id.cb_no_admitidos);
        admitidos = (CheckBox) findViewById(R.id.cb_admitidos);
        en_reparacion = (CheckBox) findViewById(R.id.cb_en_reparacion);
        reparados = (CheckBox) findViewById(R.id.cb_reparados);

        ordenar_fecha = (CheckBox) findViewById(R.id.cb_ordenar_fecha);
        ordenar_comentarios = (CheckBox) findViewById(R.id.cb_ordenar_comentarios);
        ordenar_gravedad = (CheckBox) findViewById(R.id.cb_ordenar_gravedad);

        no_admitidos.setChecked(true);
        admitidos.setChecked(true);
        en_reparacion.setChecked(true);
        reparados.setChecked(true);

        ordenar_fecha.setChecked(true);
        ordenar_comentarios.setChecked(false);
        ordenar_gravedad.setChecked(false);


        ordenar_fecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ordenar_fecha.isChecked()){
                    ordenar_comentarios.setChecked(false);
                    ordenar_gravedad.setChecked(false);
                } else {
                    ordenar_fecha.setChecked(true);
                }
            }
        });

        ordenar_comentarios.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ordenar_comentarios.isChecked()){
                    ordenar_fecha.setChecked(false);
                    ordenar_gravedad.setChecked(false);
                } else {
                    ordenar_fecha.setChecked(true);
                }
            }
        });

        ordenar_gravedad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ordenar_gravedad.isChecked()){
                    ordenar_fecha.setChecked(false);
                    ordenar_comentarios.setChecked(false);
                } else {
                    ordenar_fecha.setChecked(true);
                }
            }
        });

    }

    public void btn_filtro(View view){

        Intent i = new Intent(Filtro.this, MainActivity.class);

        i.putExtra("no_admitidos", no_admitidos.isChecked());
        i.putExtra("admitidos", admitidos.isChecked());
        i.putExtra("en_reparacion", en_reparacion.isChecked());
        i.putExtra("reparados", reparados.isChecked());

        i.putExtra("fecha", ordenar_fecha.isChecked());
        i.putExtra("comentarios", ordenar_comentarios.isChecked());
        i.putExtra("gravedad", ordenar_gravedad.isChecked());
        
        startActivity(i);
    }


}
