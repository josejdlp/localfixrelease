package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Busqueda extends AppCompatActivity {

    //private ArrayList<String> palabrasClave = new ArrayList<String>();
    //private ArrayList<Desperfecto> listaDesperfectos = new ArrayList<Desperfecto>();
    //private ArrayList<Desperfecto> busqueda = new ArrayList<>();

    TextView busqueda;
    ImageButton buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        busqueda = (TextView) findViewById(R.id.texto_busqueda);
        buscar = (ImageButton) findViewById(R.id.btn_buscar_vista_buscar);

        //Bundle bundleObject=getIntent().getExtras();
        //listaDesperfectos=(ArrayList<Desperfecto>) bundleObject.getSerializable("desperfectos");

        //cargarLista();

    }

    public void btn_buscar(View view){
        String texto =  busqueda.getText().toString();
        Intent i =  new Intent(Busqueda.this, MainActivity.class);
        i.putExtra("Texto_Busqueda", texto);
        startActivity(i);
    }

 /*   public void btn_buscar(View view) {

        palabrasClave.clear();
        busqueda.clear();

        TextView textoBuscar = (TextView) findViewById(R.id.texto_busqueda);
        String palabras [] = textoBuscar.getText().toString().split("\\s+");

        for (String palabra: palabras){
            palabrasClave.add(palabra);
        }

        for (String palabra: palabrasClave){
            for (Desperfecto desperfecto: listaDesperfectos){
                String palabrasTitulo[] = desperfecto.getTitulo().split("\\s+");
                for (String palabraTitulo: palabrasTitulo){
                    if(palabraTitulo.equals(palabra))
                        busqueda.add(desperfecto);
                }
            }
        }
    }

    public void btn_limpiar(View view){
        palabrasClave.clear();
        busqueda.clear();
        //cargarLista();
    }

    /*private void cargarLista(){
        ListView lista = (ListView) findViewById(R.id.lista_buscar);

        ArrayAdapter adaptator =
                new ArrayAdapter(this, R.layout.desperfectoitemlayout, busqueda){
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
                        tituloView.setText(busqueda.get(position).getDescripcion());
                        // ubicacionView.setText(listaDesperfectos.get(position).getUbicacion());
                        return fila;
                    }
                };
        lista.setAdapter(adaptator);
        /*lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView
                            ,View view
                            ,int position
                            , long l){
                        Intent visualizarDesperfecto = new Intent (MainActivity.this, VisualizarDesperfecto.class);
                        visualizarDesperfecto.putExtra("EXTRA_POS", position);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("desperfectos",listaDesperfectos);
                        //PASAR SOLO el  OBJETO  clickeado DESPERFECTOPRUEBA...
                        visualizarDesperfecto.putExtras(bundle);
                        // visualizarDesperfecto.putExtra("EXTRA_IMAGENES", listaDesperfectos.get(position).getImagenes());
                        startActivity(visualizarDesperfecto);
                    }
                }
        );
        */
    }

