package com.example.movies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorActivity extends AppCompatActivity {

    private ImageView castImage;
    private AppCompatButton rateButton;
    private TextView castName, castBiography;
    private ProgressDialog progressDialog;

    private final String API_KEY = "fecae410722bc227d74f320928c3f0a4";
    private int castId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);

        castImage = findViewById(R.id.cast_image);
        rateButton = findViewById(R.id.rate);
        castName = findViewById(R.id.cast_name);
        castBiography = findViewById(R.id.cast_description);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_actor_details)); // Using string resource
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            castId = intent.getExtras().getInt("castId");
            String castProfilePath = intent.getExtras().getString("castProfilePath");

            fetchActorDetails(castId);
            if (castProfilePath != null && !castProfilePath.isEmpty()) {
                String imageUrl = "https://image.tmdb.org/t/p/w500" + castProfilePath;
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.person)
                        .error(R.drawable.person)
                        .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                Log.e("Glide", "Image load failed", e);
                                Toast.makeText(ActorActivity.this, getString(R.string.image_load_failed), Toast.LENGTH_SHORT).show(); // Using string resource
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(castImage);
            }
        } else {
            Log.e("ActorActivity", "Intent or extras are null.");
            Toast.makeText(this, getString(R.string.error_loading_actor_details), Toast.LENGTH_SHORT).show(); // Using string resource
            finish();
        }
    }

    private void fetchActorDetails(int castId) {
        progressDialog.show();
        long startTime = System.currentTimeMillis();
        ApiService apiService = ApiClient.getApiService();
        Call<Cast> call = apiService.getActorDetails(castId, API_KEY);
        call.enqueue(new Callback<Cast>() {
            @Override
            public void onResponse(Call<Cast> call, Response<Cast> response) {
                progressDialog.dismiss();
                long endTime = System.currentTimeMillis();
                Log.d("API_TIMING", "API call took: " + (endTime - startTime) + "ms");
                if (response.isSuccessful() && response.body() != null) {
                    Cast cast = response.body();
                    updateUI(cast);
                } else {
                    Log.e("API_RESPONSE", "Unsuccessful actor response: " + response.code());
                    Toast.makeText(ActorActivity.this, getString(R.string.failed_to_load_actor_information), Toast.LENGTH_SHORT).show(); // Using string resource
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_RESPONSE", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("API_RESPONSE", "Error reading error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Cast> call, Throwable t) {
                progressDialog.dismiss();
                long endTime = System.currentTimeMillis();
                Log.d("API_TIMING", "API call failed after: " + (endTime - startTime) + "ms");
                Log.e("API_FAILURE", "API actor call failed: " + t.getMessage());
                Toast.makeText(ActorActivity.this, getString(R.string.failed_to_load_actor_information), Toast.LENGTH_SHORT).show(); // Using string resource
            }
        });
    }

    private void updateUI(Cast actor) {
        if (actor != null) {
            if (castName != null) {
                castName.setText(actor.getName());
            }
            if (castBiography != null) {
                castBiography.setText(actor.getBiography());
            }
        }
    }
}