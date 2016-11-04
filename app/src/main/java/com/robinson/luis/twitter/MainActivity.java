package com.robinson.luis.twitter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPass;

    private Button login;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPass = (EditText) findViewById(R.id.txtPass);



        mAuthListener = new FirebaseAuth.AuthStateListener(){
          @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d("LOG D:", "Usuario:" + user.getUid());
                    Intent i = new Intent(getApplicationContext(),LogedActivity.class);
                    //i.putExtra()
                    startActivity(i);
                } else {
                    Log.d("LOG D:", "Sem usuário conectado");
                }
          }

        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove listener when application stop
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(View view){
        // it still needs a to validate null fields.
        mAuth.signInWithEmailAndPassword(txtEmail.getText().toString(),txtPass.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getBaseContext(),"Error to validate",Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    public void newUser(View view){
        mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(),txtPass.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( @NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getBaseContext(),"Error to create account",Toast.LENGTH_SHORT);
                            Log.d("LOG D", "erro ao criar a conta");
                        } else {
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            // create a object id in the field "users"
                            DatabaseReference ref = db.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            // add in firebase database
                            ref.child("name").setValue(txtName.getText().toString());
                            ref.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //Toast.makeText(getBaseContext(),"caiu aqui",Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    public void clScream(){
        txtName.setText("");
        txtEmail.setText("");
        txtPass.setText("");
    }


}
