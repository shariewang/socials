package com.sharie.mdb.mdbsocials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private EditText email;
    private EditText password;
    private String my_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //UI references.
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        Button signIn = (Button) findViewById(R.id.sign_in);
        Button reg = (Button) findViewById(R.id.register);

        //Database references.
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.sign_in:
                signIn(email.getText().toString(),password.getText().toString());
                break;
            case R.id.register:
                createUser(email.getText().toString(),password.getText().toString());
                break;
        }
    }

    /*
    * Signs in a user using FireBase Auth. If unsuccessful, displays a Toast.
    * @param A string email and a string password
    * @return None
     */
    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
    * Creates a user with FireBase Auth and adds the user to the database. If it fails, displays a Toast
    * @param a String email and a String password
    * @return None
     */
    public void createUser(String email_field, String password){
        mAuth.createUserWithEmailAndPassword(email_field, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            my_uid = mAuth.getCurrentUser().getUid();
                            User current = new User(my_uid,email.getText().toString());
                            database.child("users").child(my_uid).setValue(current);

                            Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Failed to create an account.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}

