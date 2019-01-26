package com.example.josejimenezdelapaz.localfix;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.graphics.Color.rgb;

public class VisualizarDesperfecto extends AppCompatActivity {

    private String idDesperfecto;
    private Desperfecto desperfecto=new Desperfecto();
    private int posImagen=0;

    //Referencias a la BBDD
    private DatabaseReference referenciaBBDD;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_desperfecto);

        idDesperfecto = getIntent().getStringExtra("desperfecto");

        referenciaBBDD = FirebaseDatabase.getInstance().getReference("Desperfectos").child(idDesperfecto);
        mAuth = FirebaseAuth.getInstance();

        //Insertar un ListView dentro de un ScrollView requiere de inicializar
        //una serie de listeners para poder mostar la lista a tamaño completo y poder
        //navegar por la misma.


        int ancho = ViewGroup.LayoutParams.MATCH_PARENT;
        int alto = 500;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ancho, alto);
        ImageView im = (ImageView) findViewById(R.id.iv_img);
        im.setLayoutParams(params);

        referenciaBBDD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                desperfecto = dataSnapshot.getValue(Desperfecto.class);
                visualizarDesperfecto();
                actualizarPuntaciones();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void actualizarPuntaciones() {
        TextView texto = (TextView) findViewById(R.id.tv_gravedad);
        texto.setText("GRAVEDAD " + String.valueOf(desperfecto.calcularGravedad()));

        RatingBar rt = (RatingBar)findViewById(R.id.Rb_gravedad);
        rt.setRating(desperfecto.calcularGravedad());
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

        ViewGroup.LayoutParams params = comentarios.getLayoutParams();
        params.height = 400;
        comentarios.setLayoutParams(params);


    }

    private void visualizarDesperfecto(){


        TextView titulo = (TextView) findViewById(R.id.tv_title);
        ImageView img = (ImageView) findViewById(R.id.iv_img);
        TextView estado = (TextView) findViewById(R.id.tv_estado_visualizar_desp);
        TextView usuario = (TextView) findViewById(R.id.tv_user);
        TextView descripcion = (TextView) findViewById(R.id.tv_description);
        TextView gravedad = (TextView) findViewById(R.id.tv_gravedad);

        titulo.setText(desperfecto.getTitulo());
        usuario.setText(desperfecto.getAutor());
        descripcion.setText(desperfecto.getDescripcion());
        String g = String.format("%.1f", desperfecto.calcularGravedad());
        gravedad.setText("GRAVEDAD: " + g);

        estado.setText(desperfecto.getEstado());

        if (desperfecto.getEstado().equals("Admitido")) estado.setTextColor(rgb(23, 23, 255));
        if (desperfecto.getEstado().equals("En reparacion")) estado.setTextColor(rgb(255, 116, 29));
        if (desperfecto.getEstado().equals("Reparado")) estado.setTextColor(rgb(2, 94, 29));

        //CARGAR IMAGENES
        if(!desperfecto.getImagenes().isEmpty()){
            Picasso.with(this).load(desperfecto.getImagenes().get(0)).into(img);
        }

        if(!desperfecto.getComentarios().isEmpty()){
            cargarComentarios();
        }
    }

    public void cargarComentarios() {

        ListView listaComentarios = (ListView) findViewById(R.id.lv_coments);

        listaComentarios.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, desperfecto.getComentarios()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);


                int tam = desperfecto.getComentarios().size() - 1;

                ((TextView) view.findViewById(android.R.id.text1))
                        .setText("Autor: " + desperfecto.getComentarios().get(tam - position).getAutor());
                ((TextView) view.findViewById(android.R.id.text2))
                        .setText(desperfecto.getComentarios().get(tam - position).getTexto());

                return view;
            }
                                    }
        );
        setScrollListView();
        setListViewHeightBaseOnChildren();
    }

    public void btn_enviar(View view){
        if (mAuth.getCurrentUser() == null){
            Toast.makeText(VisualizarDesperfecto.this, "¡No puedes comentar si no estás logueado!", Toast.LENGTH_LONG).show();
            return;

        }

        TextView texto = (TextView) findViewById(R.id.et_escribir_comentario);

        if (texto.getText().toString().isEmpty()){
            Toast.makeText(VisualizarDesperfecto.this, "No se ha escrito nada", Toast.LENGTH_LONG).show();
            return;
        }

        Integer id = desperfecto.getComentarios().size();
        Comentario nuevoComentario = new Comentario(texto.getText().toString(),
                mAuth.getCurrentUser().getEmail());


        referenciaBBDD.child("comentarios").child(id.toString()).setValue(nuevoComentario);

        texto.setText("");
        Toast.makeText(VisualizarDesperfecto.this, "Mensaje enviado con éxito", Toast.LENGTH_LONG).show();
    }

    public void btn_puntuar(View view){

        if(mAuth.getCurrentUser() == null){
            Toast.makeText(VisualizarDesperfecto.this, "No puedes puntuar si no estás logueado", Toast.LENGTH_LONG).show();
            return;
        }

        RatingBar rb = (RatingBar) findViewById(R.id.Rb_gravedad);
        float puntuacion = rb.getRating();
        Boolean haVotado = false;

        for (int i = 0; i < desperfecto.getValoraciones().size(); i++){
            if (desperfecto.getValoraciones().get(i).getUid().equals(mAuth.getCurrentUser().getUid())){
                haVotado = true;
                referenciaBBDD.child("valoraciones").child(String.valueOf(i)).setValue(
                        new Valoracion(mAuth.getCurrentUser().getUid(), puntuacion));
                break;
            }
        }

        if (!haVotado){
            Valoracion nueva = new Valoracion(mAuth.getCurrentUser().getUid(), puntuacion);
            referenciaBBDD.child("valoraciones")
                    .child(String.valueOf(desperfecto.getValoraciones().size()))
                    .setValue(nueva);
        }

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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER:
                if (mAuth.getCurrentUser() == null){
                    Toast.makeText(VisualizarDesperfecto.this, "¡No puedes comentar si no estás logueado!", Toast.LENGTH_LONG).show();
                    return true;

                }

                TextView texto = (TextView) findViewById(R.id.et_escribir_comentario);

                if (texto.getText().toString().isEmpty()){
                    Toast.makeText(VisualizarDesperfecto.this, "No se ha escrito nada", Toast.LENGTH_LONG).show();
                    return true;
                }

                Integer id = desperfecto.getComentarios().size();
                Comentario nuevoComentario = new Comentario(texto.getText().toString(),
                        mAuth.getCurrentUser().getEmail());


                referenciaBBDD.child("comentarios").child(id.toString()).setValue(nuevoComentario);

                texto.setText("");
                Toast.makeText(VisualizarDesperfecto.this, "Mensaje enviado con éxito", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
