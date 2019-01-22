package com.example.josejimenezdelapaz.localfix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroUsuario extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private Button mRegisterButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        mAuth = FirebaseAuth.getInstance();

        mNameField = (EditText)findViewById(R.id.et_new_name);
        mEmailField = (EditText)findViewById(R.id.et_new_email);
        mPasswordField = (EditText)findViewById(R.id.et_new_pass);
        mRegisterButton = (Button)findViewById(R.id.btn_register_new_user);

        mProgress = new ProgressDialog(this);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });





    }

    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.setMessage("Registrando, por favor espere...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgress.dismiss();

                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();

                        Toast.makeText(getApplicationContext(), user_id, Toast.LENGTH_SHORT).show();


                        goMainScreen();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "createUserWithEmail:failure", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);

        //Esto sirve para que cuando vaya al main activity sea la unica actividad en ejecucion de la app
        //con lo cual si se presiona el boton "atras" en el dispositivo en lugar de volver a la pantalla de login
        //se saldra de la app (volvera al menu del dispositivo movil)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
