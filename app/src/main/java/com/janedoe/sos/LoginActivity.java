package com.janedoe.sos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button register;
    private EditText email;
    private EditText pass;
    private TextView signIn;
    private int ok=0;
    private SharedPreferences sharedPreferences;

    private ProgressDialog progressDial;

    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        progressDial = new ProgressDialog(this);
        register = (Button) findViewById(R.id.register);

        email = (EditText) findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);

        signIn = (TextView) findViewById(R.id.signIn);
        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(this);
        signIn.setOnClickListener(this);

    }


    private void registerUser(){
        final String emaill = email.getText().toString().trim();
        final String passs = pass.getText().toString().trim();

        Log.d("EMAIL", emaill);
        Log.d("PASS", passs);

        if(TextUtils.isEmpty(emaill)){
            //email is empry
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show();
            //stops the function
            return;
        }

        if(TextUtils.isEmpty(passs)){
            //password is empty
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
            return;
        }

        //if valid = ok, show progress

        progressDial.setMessage("Registering User...");
        progressDial.show();

        firebaseAuth.createUserWithEmailAndPassword(emaill, passs)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user is succ registered
                            Toast.makeText(LoginActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            progressDial.hide();
                            sharedPreferences.edit().putString("user",emaill).putString("pass",passs).apply();
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Could not register, please try again!", Toast.LENGTH_SHORT).show();
                            progressDial.hide();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if(view == register){
            registerUser();
        }

        if(view == signIn){
            //will open signin activity
        }
    }
}

