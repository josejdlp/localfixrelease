package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NuevoDesperfecto extends AppCompatActivity {

    private DatabaseReference referenciaBBDD;
    private ArrayList<String> listaUrls = new ArrayList<String>();
    private final static int code = 1000;
    private final static int codeMapa = 1001;
    private float gravedad = 0;
    private String direccion = "";
    private String lat = "";
    private String lon = "";
    private ArrayList<Desperfecto> desperfectos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_desperfecto);

        referenciaBBDD = FirebaseDatabase.getInstance().getReference("Desperfectos");

        ImageView IVimages, IVcamera;
        IVimages = (ImageView) findViewById(R.id.IV_images);
        IVcamera = (ImageView) findViewById(R.id.IV_camera);

        Bundle bundleObject=getIntent().getExtras();

        Resources res = getResources();
        Drawable img_gallery = res.getDrawable(R.drawable.img_galeria);
        IVimages.setImageDrawable(img_gallery);

        img_gallery = res.getDrawable(R.drawable.img_camera);
        IVcamera.setImageDrawable(img_gallery);

        desperfectos = (ArrayList<Desperfecto>) bundleObject.getSerializable("desperfectos");

        IVimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent images = new Intent(NuevoDesperfecto.this, UploadImages.class);
                startActivityForResult(images, code);
            }
        });

        RatingBar rb = (RatingBar) findViewById(R.id.Rb_gravedad);
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                gravedad = rating;
            }
        });


        TextView selecMapa = (TextView) findViewById(R.id.tv_SelecMapa);
        selecMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irMapa();
            }
        });
    }

    private void irMapa() {
        Intent mapa = new Intent(this, Mapa.class);
        mapa.putExtra("EXTRA_MODALIDAD", 0);//0 indica que es desde crear desperfecto
        Bundle bundle = new Bundle();
        bundle.putSerializable("desperfectos", desperfectos);
        mapa.putExtras(bundle);
        startActivityForResult(mapa, codeMapa);
    }

    public void btn_agregar_nuevo_desperfecto(View view) {

        //Titulo
        TextView textTitulo = (TextView) findViewById(R.id.Tv_nombre);
        String titulo = textTitulo.getText().toString();

        if (titulo.isEmpty()){
            Toast.makeText(NuevoDesperfecto.this, "Debes escribir un título!!", Toast.LENGTH_LONG).show();
            return;
        }

        //Descripcion
        TextView textDescripcion = (TextView) findViewById(R.id.Tv_descripcion);
        String descripcion = textDescripcion.getText().toString();

        if (descripcion.isEmpty()){
            Toast.makeText(NuevoDesperfecto.this, "Indica una descripción del desperfecto", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Valoracion> valoraciones = new ArrayList<Valoracion>();
        valoraciones.add(new Valoracion(FirebaseAuth.getInstance().getCurrentUser().getUid(), gravedad));

        //Gravedad, en la variable gravedad
        String id = referenciaBBDD.push().getKey();
        Desperfecto nuevo = new Desperfecto(id
                , FirebaseAuth.getInstance().getCurrentUser().getEmail()
                , titulo, lat, lon, descripcion
                , "No admitido"
                , new ArrayList<Comentario>()
                , listaUrls
                , valoraciones);

        referenciaBBDD.child(id).setValue(nuevo);

        Toast.makeText(this, "Desperfecto creado", Toast.LENGTH_LONG).show();

        Intent principal = new Intent(NuevoDesperfecto.this, MainActivity.class);
        startActivityForResult(principal, code);
        this.finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data.getStringArrayListExtra("urls").size() > 0) {
                listaUrls = data.getStringArrayListExtra("urls");
                TextView text = (TextView) findViewById(R.id.Tv_imagenes);
                text.setText(listaUrls.size() + " imágenes añadidas");
            } else {
                Toast.makeText(this, "No has guardado las imágenes", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == codeMapa) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data.getStringExtra("direccion") != "" && data.getStringExtra("lat") != "" &&
                    data.getStringExtra("lon") != "") {
                direccion = data.getStringExtra("direccion");
                lat = data.getStringExtra("lat");
                lon = data.getStringExtra("lon");
                TextView text = (TextView) findViewById(R.id.tv_direccion);
                text.setText(direccion + lat + lon);
                Toast.makeText(this, "Direccion obtenida: " + direccion + lat + lon, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No has seleccionado ninguna dirección", Toast.LENGTH_SHORT).show();
            }

        }

    }
}


