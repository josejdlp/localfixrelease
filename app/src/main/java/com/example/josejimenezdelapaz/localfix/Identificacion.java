package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Identificacion extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ArrayList<String> admins = new ArrayList<String>(); //Contiene todos los UID de admins
    private String currentUID = ""; //UID del usuario actual
    private Boolean isAdmin = false;

    //Facebook
    private LoginButton fb_loginButton;
    private CallbackManager fb_callbackManager;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    //EmailPassword
    private Button btn_signin_emailpass;
    private EditText et_login_email;
    private EditText et_login_pass;
    private Button btn_create_account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identificacion);

        //Facebook
        fb_callbackManager = CallbackManager.Factory.create();

        fb_loginButton = (LoginButton)findViewById(R.id.btn_login_facebook);

        fb_loginButton.setReadPermissions("email", "public_profile");

        fb_loginButton.registerCallback(fb_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Inicio de sesion correcto
                handleFacebookAccessToken(loginResult.getAccessToken());
                //goMainScreen();
            }

            @Override
            public void onCancel() {
                // Inicio de sesion cancelado
                Toast.makeText(getApplicationContext(), "LoginFacebook:canceled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                // Error en el inicio de sesion
                Toast.makeText(getApplicationContext(), "LoginFacebook:Error", Toast.LENGTH_LONG).show();
            }
        });
        //--Facebook

        admins = getIntent().getStringArrayListExtra("UIDAdmin");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_login_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                switch (v.getId()){
                    case R.id.btn_login_google:
                        signIn();
                        break;
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //goMainScreen();
                    Toast.makeText(getApplicationContext(), "User OK", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "User Null", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Email&Password
        et_login_email = (EditText)findViewById(R.id.et_login_email);
        et_login_pass = (EditText)findViewById(R.id.et_login_pass);
        btn_signin_emailpass = (Button)findViewById(R.id.btn_signin_mailpass);
        btn_create_account = (Button)findViewById(R.id.btn_new_account);

        btn_signin_emailpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_login_email.getText().toString().trim();
                String pass = et_login_pass.getText().toString().trim();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    signInWithEmailPassword(email, pass);
                }else{
                    Toast.makeText(getApplicationContext(),"Error: Rellene los campos correo y contraseña", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCreateNewAccount();
            }
        });


    }

    private void goCreateNewAccount() {
        Intent toRegister = new Intent(Identificacion.this, RegistroUsuario.class);
        startActivity(toRegister);
    }

    private void signInWithEmailPassword(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    goMainScreen();
                }else{
                    Toast.makeText(getApplicationContext(), "EmailPass Auth failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Una vez que hemos iniciado sesion en facebook --> utilizamos el token para iniciar sesion en Firebase
    private void handleFacebookAccessToken(AccessToken accessToken) {
        Toast.makeText(getApplicationContext(), "SignIn FB OK -> HandleFacebookToken", Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        Toast.makeText(getApplicationContext(), "Credential OK", Toast.LENGTH_SHORT).show();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Cuando se completa el inicio de sesion en Firebase
                Toast.makeText(getApplicationContext(), "SignIn FB Firebase OK", Toast.LENGTH_LONG).show();

                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "SignIn FB Firebase ERROR", Toast.LENGTH_LONG).show();
                }

                goMainScreen();
            }
        });
    }


    @Override
    protected void onStart(){
        super.onStart();

        //Si es administrador y ya está logueado, se le manda a su vista directamente
        if (mAuth.getCurrentUser() != null) {
            for (String uid:admins){
                if(mAuth.getCurrentUser().getUid().equals(uid)){
                    Intent i = new Intent(Identificacion.this, VistaAdministrador.class);
                    startActivity(i);
                }
            }
        }

        mAuth.addAuthStateListener(firebaseAuthListener);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop(){
        super.onStop();

        mAuth.removeAuthStateListener(firebaseAuthListener);
    }


    private void updateUI(FirebaseUser user){

        if(user != null){
            findViewById(R.id.btn_login_google).setVisibility(View.GONE);
            findViewById(R.id.btn_login_facebook).setVisibility(View.GONE);
            findViewById(R.id.btn_new_account).setVisibility(View.GONE);

            findViewById(R.id.et_login_email).setVisibility(View.GONE);
            findViewById(R.id.et_login_pass).setVisibility(View.GONE);
            findViewById(R.id.btn_signin_mailpass).setVisibility(View.GONE);

            findViewById(R.id.btn_logout).setVisibility(View.VISIBLE);

            //Toast.makeText(Identificacion.this, "Estás logueado desde: "+user.getEmail(), Toast.LENGTH_SHORT).show();

        } else {
            findViewById(R.id.btn_login_google).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_login_facebook).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_new_account).setVisibility(View.VISIBLE);

            findViewById(R.id.et_login_email).setVisibility(View.VISIBLE);
            findViewById(R.id.et_login_pass).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_signin_mailpass).setVisibility(View.VISIBLE);

            findViewById(R.id.btn_logout).setVisibility(View.GONE);
        }
    }


    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void btn_logout(View view){

        mAuth.signOut();

        //Fb_logout
        LoginManager.getInstance().logOut();
        //-Fb_logout

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(Identificacion.this, "FUERA DE AQUÍ", Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                });
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        fb_callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch(ApiException e){
                Toast.makeText(Identificacion.this, e.toString(), Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUID = user.getUid();

                            for (String uid:admins){
                                if (currentUID.equals(uid)){
                                    isAdmin = true;
                                    break;
                                }
                            }
                            /*
                            if (currentUID.equals(admins)){
                                isAdmin = true;
                            }
                            */
                            updateUI(user);
                            openActivity();
                            finish();

                        } else {
                            Toast.makeText(Identificacion.this, "Fallo al autenticar", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


/*        if(acct!= null) {

            if (!isAdmin) {

                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
            } else {
                Intent actividadAdmin = new Intent(this, NuevoDesperfecto.class);
                startActivity(actividadAdmin);
                //Toast.makeText(Identificacion.this, "bbbbbbbbbbbbbb: "+ mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
            }
        }
*/
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void openActivity(){
        if (!isAdmin) {
            Intent i = new Intent(Identificacion.this, MainActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(Identificacion.this, VistaAdministrador.class);
            startActivity(i);
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
