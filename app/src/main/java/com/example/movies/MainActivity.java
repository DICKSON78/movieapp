package com.example.movies;

import android.annotation.SuppressLint;
import android.app.ComponentCaller;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {
    ProgressDialog dialog;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient mGoogleSignInClient;
    Button signInBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        getWindow ().setFlags (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView (R.layout.activity_main);

        //Sign by Google Email
        googleSignInOptions = new GoogleSignInOptions.Builder (GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail ().build ();
        mGoogleSignInClient = GoogleSignIn.getClient (this, googleSignInOptions);
        signInBtn = (Button) findViewById (R.id.btnGoogleSignIn);

        signInBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent ();
        startActivityForResult (intent,1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent (data);
            try {
                task.getResult (ApiException.class);

                //Navigate to Home Screen
                homeScreen();
            } catch (ApiException e) {
            Toast.makeText (getApplicationContext (),"No internet Connection ... !",Toast.LENGTH_LONG).show ();
            }
        }
    }
    //End of SignIn By Email

    //Go to next HomeScreen that manage the Welcome , Favorite and the Bookmark Fragments
    private void homeScreen() {
        finish ();
        Intent intent = new Intent (MainActivity.this,HomeActivity.class);
        startActivity (intent);

    }
}