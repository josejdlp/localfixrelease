package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Busqueda extends AppCompatActivity {

    TextView busqueda;
    ImageButton buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        busqueda = (TextView) findViewById(R.id.texto_busqueda);
        buscar = (ImageButton) findViewById(R.id.btn_buscar_vista_buscar);
    }



    public void btn_buscar(View view){
        String texto = busqueda.getText().toString();
        if (texto.isEmpty()){
            Toast.makeText(Busqueda.this, "Escribe algo", Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = new Intent(Busqueda.this, MainActivity.class);
        i.putExtra("Texto_Busqueda", texto);
        startActivity(i);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_ENTER:
                String texto = busqueda.getText().toString();
                if (texto.isEmpty()){
                    Toast.makeText(Busqueda.this, "Escribe algo", Toast.LENGTH_LONG).show();
                    return true;
                }
                Intent i = new Intent(Busqueda.this, MainActivity.class);
                i.putExtra("Texto_Busqueda", texto);
                startActivity(i);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }

    }
}

