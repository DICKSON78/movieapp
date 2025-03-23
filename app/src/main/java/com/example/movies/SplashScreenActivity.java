package com.example.movies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 5000; // 3 seconds
    private static final long ANIMATION_DURATION = 4000; // 1 second

    private TextView movieText, magicText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow ().setFlags (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash_screen);

        movieText = findViewById(R.id.movie_text);
        magicText = findViewById(R.id.magic_text);

        startAnimations();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    private void startAnimations() {
        animateText(movieText, 0);
        new Handler().postDelayed(() -> animateText(magicText, 0), ANIMATION_DURATION / 2); // Delay the magic text animation
    }

    private void animateText(final View view, final float fromAlpha) {
        AlphaAnimation fadeIn = new AlphaAnimation(fromAlpha, 1.0f);
        fadeIn.setDuration(ANIMATION_DURATION);
        fadeIn.setFillAfter(true);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        view.startAnimation(fadeIn);
    }
}