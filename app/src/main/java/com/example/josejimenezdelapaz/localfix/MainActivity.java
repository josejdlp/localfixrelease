package com.example.josejimenezdelapaz.localfix;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   // private ArrayList<Desperfecto> desperfectos;
    public ArrayList<DesperfectoActivity> listaDesperfectos=new ArrayList<DesperfectoActivity>();
    private DatabaseReference referenciaBBDD;
    private DesperfectoActivity desp=new DesperfectoActivity();
    private FirebaseAuth mAuth;
    private String UIDAdmin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        referenciaBBDD = FirebaseDatabase.getInstance().getReference("Desperfectos");
        mAuth = FirebaseAuth.getInstance();
        showListaDesperfectos();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart(){
        super.onStart();

        //redireccionarUsuario();
    }

    private void redireccionarUsuario(){
        if (mAuth.getCurrentUser()!= null) {
            if (mAuth.getCurrentUser().getUid().equals(UIDAdmin)) {
                Intent i = new Intent(MainActivity.this, VistaAdministrador.class);
                startActivity(i);
            }
        }
    }

    private void showListaDesperfectos(){

        //CARGAR AL INICIO LOS DATOS DE LA BD
        referenciaBBDD.addValueEventListener(new ValueEventListener() {
            // Se activa una vez con el estado inicial de los datos y nuevamente cada vez que estos se cambian.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDesperfectos.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    desp=postSnapshot.getValue(DesperfectoActivity.class);
                    listaDesperfectos.add(desp);
                }
                cargarLista();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        //Se carga el UID del administrador
        referenciaBBDD = referenciaBBDD.getParent();
        referenciaBBDD = referenciaBBDD.child("Admin");
        referenciaBBDD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UIDAdmin = dataSnapshot.child("uid").getValue().toString();
                //redireccionarUsuario();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
    public void cargarLista(){
        ListView lista = (ListView) findViewById(R.id.lista);
        Log.i("2 TAMM","2 tamanno lista"+listaDesperfectos.size());
        // desperfectos = Desperfecto.populateDesperfectos();

        ArrayAdapter adaptator =
                new ArrayAdapter(this, R.layout.desperfectoitemlayout, listaDesperfectos){
                    public View getView(int position
                            ,View convertView
                            ,ViewGroup parent){
                        LayoutInflater inflater = (LayoutInflater) getContext()
                                .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);

                        //Crear la vista para cada fila
                        View fila = inflater.inflate(R.layout.desperfectoitemlayout, parent, false);
                        TextView tituloView = (TextView) fila.findViewById(R.id.textTitulo);
                        //TextView ubicacionView = (TextView) fila.findViewById(R.id.textUbicacion);
                        //Establecer valores que queremos que se muestren en los widgets
                        //iconoView.setImageResource(desperfectos.get(position).getIcono());
                        tituloView.setText(listaDesperfectos.get(position).getDescripcion());
                        // ubicacionView.setText(listaDesperfectos.get(position).getUbicacion());
                        return fila;
                    }
                };
        lista.setAdapter(adaptator);
        lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView
                            ,View view
                            ,int position
                            , long l){
                        //Enviar el desperfecto seleccionado a la vista.
                        Intent visualizarDesperfecto = new Intent (MainActivity.this, VisualizarDesperfecto.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("desperfecto",listaDesperfectos.get(position));
                        visualizarDesperfecto.putExtras(bundle);
                        // visualizarDesperfecto.putExtra("EXTRA_IMAGENES", listaDesperfectos.get(position).getImagenes());
                        startActivity(visualizarDesperfecto);
                    }
                }
        );
    }

    public void bt_mapa(View view){
       // Toast.makeText(MainActivity.this, "Función Mapa", Toast.LENGTH_SHORT).show();
        Intent map=new Intent(this,Mapa.class);
        map.putExtra("EXTRA_MODALIDAD",1);
        Bundle bundle=new Bundle();
        bundle.putSerializable("desperfectos",listaDesperfectos);
        map.putExtras(bundle);
        startActivity(map);
    }

    public void bt_buscar(View view){
        Bundle bundle=new Bundle();
        bundle.putSerializable("desperfectos",listaDesperfectos);

        Intent i = new Intent(MainActivity.this, Busqueda.class);
        i.putExtras(bundle);
        startActivity(i);
    }
    public void bt_home(View view){
        Intent login = new Intent(this, Identificacion.class);
        login.putExtra("UIDAdmin", UIDAdmin);
        startActivity(login);
    }

    public void bt_nuevo(View view){
         FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null) {
            Intent nuevoDesperfecto = new Intent(this, NuevoDesperfecto.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("desperfectos",listaDesperfectos);
            nuevoDesperfecto.putExtras(bundle);
            startActivity(nuevoDesperfecto);
        } else {
            Intent login = new Intent(this, Identificacion.class);
            startActivity(login);
        }
    }

    public void bt_filtrar(View view){
        Toast.makeText(MainActivity.this, "Función Filtrar", Toast.LENGTH_SHORT).show();
    }

/*    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            Toast.makeText(getApplicationContext(), "User: "+user.getUid(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "No User", Toast.LENGTH_SHORT).show();
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();

        if(res_id == R.id.action_settings){
            Toast.makeText(getApplicationContext(), "Settings Options", Toast.LENGTH_LONG).show();
        }
        return true;
    }
}
