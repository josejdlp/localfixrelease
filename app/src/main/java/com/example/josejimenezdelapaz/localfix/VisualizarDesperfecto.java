package com.example.josejimenezdelapaz.localfix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VisualizarDesperfecto extends AppCompatActivity {

    private String id;
    private DesperfectoActivity desperfecto=new DesperfectoActivity();
    private int posImagen=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_desperfecto);
        visualizarDesperfecto();
        
    }

    private void visualizarDesperfecto(){
        TextView titulo = (TextView) findViewById(R.id.titulo_visualizar_desperfecto);
        ImageView img=(ImageView) findViewById(R.id.imagen_visualizar_desperfecto);
        Bundle bundleObject=getIntent().getExtras();
        desperfecto=(DesperfectoActivity) bundleObject.getSerializable("desperfecto");
        titulo.setText(desperfecto.getTitulo());
        //CARGAR IMAGENES
        if(desperfecto.getImagenes()!=null){
            Picasso.with(this).load(desperfecto.getImagenes().get(0)).into(img);
        }


    }
    public void siguienteImagen(View view){
        if(desperfecto.getImagenes()!=null){
            ImageView img=(ImageView) findViewById(R.id.imagen_visualizar_desperfecto);
            if(posImagen<desperfecto.getImagenes().size()-1){
                posImagen++;
                Picasso.with(this).load(desperfecto.getImagenes().get(posImagen)).into(img);
            }else if(posImagen==desperfecto.getImagenes().size()-1){
                posImagen=0;
                Picasso.with(this).load(desperfecto.getImagenes().get(0)).into(img);

            }
        }

    }
    public void anteriorImagen(View view){
        if(desperfecto.getImagenes()!=null){
            ImageView img=(ImageView) findViewById(R.id.imagen_visualizar_desperfecto);
            if(posImagen>0){
                posImagen--;
                Picasso.with(this).load(desperfecto.getImagenes().get(posImagen)).into(img);
            }else if(posImagen==0){
                posImagen=desperfecto.getImagenes().size()-1;
                Picasso.with(this).load(desperfecto.getImagenes().get(posImagen)).into(img);

            }
        }
    }
}
