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


    public ArrayList<DesperfectoActivity> listaDesperfectos=new ArrayList<DesperfectoActivity>(); //Lista con todos los desperfectos
    private ArrayList<DesperfectoActivity> listaDesperfectosMostrar = new ArrayList<DesperfectoActivity>(); //Lista con los desperfectos que se mostraran
    private DesperfectoActivity desp=new DesperfectoActivity();

    private DatabaseReference referenciaBBDD;
    private FirebaseAuth mAuth;
    private ArrayList<String> admins = new ArrayList<String>(); //Contiene los UID de los admins

    private Boolean MOSTRAR_NO_ACEPTADOS = true;
    private Boolean MOSTRAR_ACEPTDOS = true;
    private Boolean MOSTRAR_EN_REPARACION = true;
    private Boolean MOSTRAR_REPARADOS = true;

    private Boolean ORDENAR_POR_FECHA = false;
    private Boolean ORDENAR_POR_VALORACION = false;
    private Boolean ORDENAR_POR_COMENTARIOS = false;

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
            if (mAuth.getCurrentUser().getUid().equals(admins)) {
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

        //Se cargan los UID de los admins
        referenciaBBDD = referenciaBBDD.getParent();
        referenciaBBDD = referenciaBBDD.child("Admins");
        referenciaBBDD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    admins.add(postSnapshot.child("uid").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
    public void cargarLista(){
        ListView lista = (ListView) findViewById(R.id.lista);
        Log.i("2 TAMM","2 tamanno lista"+listaDesperfectos.size());
        // desperfectos = Desperfecto.populateDesperfectos();

        ArrayList<String> filtro = new ArrayList<String>();

        if(MOSTRAR_NO_ACEPTADOS) filtro.add("no aceptado");
        if(MOSTRAR_ACEPTDOS) filtro.add("admitido");
        if(MOSTRAR_EN_REPARACION) filtro.add("en reparacion");
        if(MOSTRAR_REPARADOS) filtro.add("reparado");

        listaDesperfectosMostrar.clear();

        for (DesperfectoActivity desperfecto:listaDesperfectos){
            for (String estado:filtro){
                if (estado.equals(desperfecto.getEstado())){
                    listaDesperfectosMostrar.add(desperfecto);
                    break;
                }
            }
        }

        ArrayAdapter adaptator =
                new ArrayAdapter(this, R.layout.desperfectoitemlayout, listaDesperfectosMostrar){
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
                        tituloView.setText(listaDesperfectosMostrar.get(position).getDescripcion());
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
                        bundle.putSerializable("desperfecto",listaDesperfectosMostrar.get(position));
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
        login.putStringArrayListExtra("UIDAdmin", admins);
        startActivity(login);
    }

    public void bt_nuevo(View view){
         FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null) {
            Intent nuevoDesperfecto = new Intent(this, NuevoDesperfecto.class);
            startActivity(nuevoDesperfecto);
        } else {
            Intent login = new Intent(this, Identificacion.class);
            startActivity(login);
        }
    }

    public void bt_filtrar(View view){
        Toast.makeText(MainActivity.this, "Función Filtrar", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();

        switch(res_id){
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "Boton Buscar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Boton Ajustes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_filter:
                Toast.makeText(getApplicationContext(), "Boton Filtrar", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        
        return true;
    }
}
