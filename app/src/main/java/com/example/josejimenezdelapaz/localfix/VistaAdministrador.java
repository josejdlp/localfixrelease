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
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_administrador);

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
                    , View convertView
                    , ViewGroup parent){
                        LayoutInflater inflater = (LayoutInflater) getContext()
                                .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);

                        //Crear la vista para cada fila
                        View fila = inflater.inflate(R.layout.desperfectoitemlayout_admin, parent, false);
                        TextView tituloView = (TextView) fila.findViewById(R.id.tit_lista_desp_admin);
                        tituloView.setText(listaDesperfectos.get(position).getDescripcion());

                        //Establecer comportamiento del botón
                        Button boton = (Button) fila.findViewById(R.id.borrar_lista_desp_admin);
                        boton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v){
                                referenciaBBDD.child(listaDesperfectos.get(position).getId()).removeValue();
                                //Cuando se borra, no se refrescan las vistas, así que recargamos
                                showListaDesperfectos();
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

    public void bt_mapa(View view){
        Toast.makeText(VistaAdministrador.this, "Función Mapa", Toast.LENGTH_SHORT).show();
    }

    public void bt_home(View view){
        Intent login = new Intent(this, Identificacion.class);
        //login.putExtra("UIDAdmin", UIDAdmin);
        startActivity(login);
    }

    public void bt_nuevo(View view){

    }

    public void bt_filtrar(View view){
        Toast.makeText(VistaAdministrador.this, "Función Filtrar", Toast.LENGTH_SHORT).show();
    }

}
