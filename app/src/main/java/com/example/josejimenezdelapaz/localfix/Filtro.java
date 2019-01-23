package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class Filtro extends AppCompatActivity {


    private Switch NO_ADMITIDOS;
    private Switch ADMITIDOS;
    private Switch EN_REPARACION;
    private Switch REPARADOS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        NO_ADMITIDOS = (Switch) findViewById(R.id.switch_no_admitidos);
        ADMITIDOS = (Switch) findViewById(R.id.switch_admitidos);
        EN_REPARACION = (Switch) findViewById(R.id.switch_en_reparacion);
        REPARADOS = (Switch) findViewById(R.id.switch_reparados);

        NO_ADMITIDOS.setChecked(true);
        ADMITIDOS.setChecked(true);
        EN_REPARACION.setChecked(true);
        REPARADOS.setChecked(true);

    }

    public void btn_filtro(View view){
        Intent i = new Intent(Filtro.this, MainActivity.class);
        i.putExtra("NO_ADMITIDOS", NO_ADMITIDOS.isChecked());
        i.putExtra("ADMITIDOS", ADMITIDOS.isChecked());
        i.putExtra("EN_REPARACION", EN_REPARACION.isChecked());
        i.putExtra("REPARADOS", REPARADOS.isChecked());
        
        startActivity(i);
    }


}
