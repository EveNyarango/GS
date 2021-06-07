package com.example.green;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    ImageView mLogo;
    TextView mSplash;
    long animTime = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        mLogo = findViewById(R.id.logo);
        mSplash = findViewById(R.id.tvsplash);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mLogo, "y", 400f);
        ObjectAnimator animatorname = ObjectAnimator.ofFloat(mSplash, "x", 200f);
        animatorY.setDuration(animTime);
        animatorname.setDuration(animTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY, animatorname);
        animatorSet.start();

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null){
                    Intent intent = new Intent( SplashActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent( SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, 4000);

    }
}