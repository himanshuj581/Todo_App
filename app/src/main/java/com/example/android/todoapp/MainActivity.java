package com.example.android.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static boolean status;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static DatabaseHandler databaseHandler;
    private static int SPLASH_TIME_OUT = 5000;
    private LinearLayout mainContent,bottomContent;
    private Animation bottomAnimation,logoAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainContent = findViewById(R.id.mainContent);
        bottomContent = findViewById(R.id.bottomContent);
        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        bottomAnimation =AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        mainContent.setAnimation(logoAnimation);
        bottomContent.setAnimation(bottomAnimation);

        databaseHandler = new DatabaseHandler(this, "profile.sqlite",null,1);
        databaseHandler.queryData("CREATE TABLE IF NOT EXISTS PICTURE(Id VARCHAR, image BLOB)");
        mAuth = FirebaseAuth.getInstance();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null){
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                };
                mAuth.addAuthStateListener(authStateListener);
            }
        }, SPLASH_TIME_OUT);
    }


}
