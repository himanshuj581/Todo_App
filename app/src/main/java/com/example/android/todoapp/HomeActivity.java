package com.example.android.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    private com.google.android.material.navigation.NavigationView navigationView;
    private  androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private NavController navController;
    private String userid;
    private TextView nameTextview;
    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        nameTextview = navigationView.getHeaderView(0).findViewById(R.id.navName);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.navProfile);
        Cursor cursor = MainActivity.databaseHandler.getData("Select image from picture where id = '"+userid+"';");
        if(cursor.moveToFirst()) {
            byte[] image = cursor.getBlob(0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(bitmap);
        }
        setSupportActionBar(toolbar);
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        NavigationUI.setupActionBarWithNavController(this,navController, drawerLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    User temp = post.getValue(User.class);
                    if(userid.equalsIgnoreCase(temp.getUserid())){
                        user =temp;
                        break;
                    }
                }
                nameTextview.setText(user.getFirstname()+" "+user.getSecondname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,drawerLayout);
    }



}
