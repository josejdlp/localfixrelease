package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VistaAdministrador extends AppCompatActivity {

    private ArrayList<DesperfectoActivity> listaDesperfectos = new ArrayList<DesperfectoActivity>();
    private DesperfectoActivity desp=new DesperfectoActivity();
    private DatabaseReference referenciaBBDD;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private String ESTADO_ADMITIDO = "Admitido";
    private String ESTADO_EN_REPARACION = "En reparacion";
    private String ESTADO_REPARADO = "Reparado";

    private int GRIS = 0xF0F8FF;
    private int AZUL = 0x00FFFF;
    private int NARANJA =0xFF7F50;
    private int VERDE = 0x7FFFD4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_administrador);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        referenciaBBDD = FirebaseDatabase.getInstance().getReference("Desperfectos");
        mAuth = FirebaseAuth.getInstance();
        showListaDesperfectos();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }



    private void showListaDesperfectos(){

        referenciaBBDD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDesperfectos.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    desp=postSnapshot.getValue(DesperfectoActivity.class);
                    listaDesperfectos.add(desp);

                }
                cargarLista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void cargarLista(){
        ListView lista = (ListView) findViewById(R.id.lista_desperfectos_admin);

        ArrayAdapter adaptator =
                new ArrayAdapter(this, R.layout.desperfectoitemlayout, listaDesperfectos){
                    public View getView(final int position
                            , final View convertView
                            , ViewGroup parent){
                        LayoutInflater inflater = (LayoutInflater) getContext()
                                .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);


                        //Crear la vista para cada fila
                        final View fila = inflater.inflate(R.layout.desperfectoitemlayout_admin, parent, false);
                        TextView tituloView = (TextView) fila.findViewById(R.id.tit_lista_desp_admin);
                        tituloView.setText(listaDesperfectos.get(position).getDescripcion());

                        TextView estado = (TextView) fila.findViewById(R.id.estado_lista_desp_admin);
                        estado.setText(listaDesperfectos.get(position).getEstado());

                        //Localizar los botones
                        Button botonBorrar = (Button) fila.findViewById(R.id.borrar_lista_desp_admin);
                        Button botonAdmitir = (Button) fila.findViewById(R.id.admitir_lista_desp_admin);
                        Button botonReparar = (Button) fila.findViewById(R.id.reparar_lista_desp_admin);
                        Button botonFinalizar = (Button) fila.findViewById(R.id.finalizar_lista_desp_admin);

                        //Asignar funcionalidad a los botones
                        botonBorrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v){
                                referenciaBBDD.child(listaDesperfectos.get(position).getId()).removeValue();
                                //Cuando se borra, no se refrescan las vistas, así que recargamos
                                showListaDesperfectos();
                            }
                        });

                        botonAdmitir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                referenciaBBDD.child(listaDesperfectos.get(position).getId()).child("estado").setValue(ESTADO_ADMITIDO);
                                TextView estado = (TextView) fila.findViewById(R.id.estado_lista_desp_admin);
                                estado.setText(ESTADO_ADMITIDO);

                            }
                        });

                        botonReparar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                referenciaBBDD.child(listaDesperfectos.get(position).getId()).child("estado").setValue(ESTADO_EN_REPARACION);
                                TextView estado = (TextView) fila.findViewById(R.id.estado_lista_desp_admin);
                                estado.setText(ESTADO_EN_REPARACION);
                            }
                        });

                        botonFinalizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                referenciaBBDD.child(listaDesperfectos.get(position).getId()).child("estado").setValue(ESTADO_REPARADO);
                                TextView estado = (TextView) fila.findViewById(R.id.estado_lista_desp_admin);
                                estado.setText(ESTADO_REPARADO);
                            }
                        });

                        return fila;
                    }
                };
        lista.setAdapter(adaptator);
        lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView
                            ,View view
                            ,int i
                            ,long l) {

                    }
                }
        );

    }

    public void btn_salir(View view){
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(Identificacion.this, "FUERA DE AQUÍ", Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                });


        Intent i = new Intent(VistaAdministrador.this, MainActivity.class);
        startActivity(i);
    }

    public void btn_principal(View view){
        Intent i = new Intent(VistaAdministrador.this, MainActivity.class);
        startActivity(i);
    }

}