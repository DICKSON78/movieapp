package com.example.movies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        getWindow ().setFlags (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView (R.layout.activity_home);


        BottomNavigationView bottomNavigationView = findViewById (R.id.bottom_navigation);
        //Initial Fragment
        replaceFragment (new WelcomFragment ());

        bottomNavigationView.setOnItemSelectedListener (item -> {
            if (item.getItemId () == R.id.home) {
                replaceFragment (new WelcomFragment ());
            } else if (item.getItemId () == R.id.bookmark) {
                replaceFragment (new BoorkmarkFragment ());
            }
            if (item.getItemId () == R.id.favorite) {
                replaceFragment (new FavoriteFragment ());
            }
            return true;
        });

    }

     //Fragment
   public void replaceFragment(Fragment fragment){
    FragmentManager fragmentManager = getSupportFragmentManager ();
    FragmentTransaction transaction = fragmentManager.beginTransaction ();
    transaction.replace (R.id.fragment_container,fragment);
    transaction.commit ();
}



}