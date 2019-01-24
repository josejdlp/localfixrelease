package com.example.josejimenezdelapaz.localfix;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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

        //Insertar un ListView dentro de un ScrollView requiere de inicializar
        //una serie de listeners para poder mostar la lista a tamaño completo y poder
        //navegar por la misma.
        setScrollListView();
        setListViewHeightBaseOnChildren();


        
    }

    private void setScrollListView(){
        ListView comentarios = (ListView) findViewById(R.id.lv_coments);
        comentarios.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void setListViewHeightBaseOnChildren(){
        ListView comentarios = (ListView) findViewById(R.id.lv_coments);
        ListAdapter listAdapter = comentarios.getAdapter();

        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(comentarios.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < comentarios.getCount(); i++){
            view = listAdapter.getView(i, view, comentarios);
            if(i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = comentarios.getLayoutParams();
        params.height = totalHeight + (comentarios.getDividerHeight() * (listAdapter.getCount() - 1));
        comentarios.setLayoutParams(params);

    }

    private void visualizarDesperfecto(){
        TextView titulo = (TextView) findViewById(R.id.tv_title);
        ImageView img=(ImageView) findViewById(R.id.iv_img);
        Bundle bundleObject=getIntent().getExtras();
        desperfecto=(DesperfectoActivity) bundleObject.getSerializable("desperfecto");
        titulo.setText(desperfecto.getTitulo());
        //CARGAR IMAGENES
        if(desperfecto.getImagenes()!=null){
            Picasso.with(this).load(desperfecto.getImagenes().get(0)).into(img);
        }

        if(!desperfecto.getComentarios().isEmpty()){
            cargarComentarios();
        }
    }

    public void cargarComentarios() {

        if (desperfecto.getComentarios().isEmpty())
            return;

        ListView listaComentarios = (ListView) findViewById(R.id.lv_coments);

        listaComentarios.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, desperfecto.getComentarios()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);

                ((TextView) view.findViewById(android.R.id.text1))
                        .setText("Autor: " + desperfecto.getComentarios().get(position).getAutor());
                ((TextView) view.findViewById(android.R.id.text2))
                        .setText(desperfecto.getComentarios().get(position).getTexto());

                return view;
            }
                                    }
        );

    }

    public void btn_enviar(View view){
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            Toast.makeText(VisualizarDesperfecto.this, "No estás logueado!!", Toast.LENGTH_LONG).show();
            return;

        }

        TextView texto = (TextView) findViewById(R.id.et_escribir_comentario);

        if (texto.getText().toString().isEmpty()){
            Toast.makeText(VisualizarDesperfecto.this, "Escribe algo!!", Toast.LENGTH_LONG).show();
            return;
        }

        //String id = FirebaseDatabase.getInstance().getReference().child("Desperfectos").child(desperfecto.getId()).child("comentarios").push().getKey();
        //Toast.makeText(VisualizarDesperfecto.this, id, Toast.LENGTH_LONG).show();
        Integer id = desperfecto.getComentarios().size();
        Comentario nuevoComentario = new Comentario(FirebaseAuth.getInstance().getCurrentUser().getEmail()
        ,texto.getText().toString());


        FirebaseDatabase.getInstance().getReference("Desperfectos")
                .child(desperfecto.getId())
                .child("comentarios")
                .child(id.toString())
                .setValue(nuevoComentario);

        desperfecto.getComentarios().add(nuevoComentario);

        texto.setText("");
        Toast.makeText(VisualizarDesperfecto.this, "Mensaje enviado con éxito", Toast.LENGTH_LONG).show();
        cargarComentarios();
    }

    public void siguienteImagen(View view){
        if(desperfecto.getImagenes()!=null){
            ImageView img=(ImageView) findViewById(R.id.iv_img);
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
            ImageView img=(ImageView) findViewById(R.id.iv_img);
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
