package com.example.android.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private String email;
    private  String password;
    public FirebaseAuth mAuth;
    private TextView textView;
    private ProgressBar progressBar;
    private EditText firstname;
    private EditText secondname;
    private EditText username;
    private EditText phoneno;
    private EditText country;
    private EditText confirmpassword;
    public DatabaseReference mDatabase;
    private String fname;
    private String sname;
    private String uname;
    private String phone;
    private String count;
    private String confpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signupButton);
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.signupTextView);
        firstname = findViewById(R.id.firstNameEditTextView);
        secondname = findViewById(R.id.secondNameEditTextView);
        username = findViewById(R.id.usernameEditTextView);
        phoneno = findViewById(R.id.PhoneEditTextView);
        country = findViewById(R.id.countryEditTextView);
        confirmpassword = findViewById(R.id.confirmPasswordEditTextView);
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        progressBar = findViewById(R.id.progressbarSignup);



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                fname = firstname.getText().toString();
                sname = secondname.getText().toString();
                uname = username.getText().toString();
                phone = phoneno.getText().toString();
                count = country.getText().toString();
                confpass = confirmpassword.getText().toString();

                if(fname.isEmpty()){
                    firstname.setError("Required Field");
                    firstname.requestFocus();
                    return;
                }
                if(sname.isEmpty()){
                    secondname.setError("Required Field");
                    secondname.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    emailEditText.setError("Email Required");
                    emailEditText.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditText.setError("Enter Valid Email");
                    emailEditText.requestFocus();
                    return;
                }
                if(count.isEmpty()){
                    country.setError("Required Field");
                    country.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    passwordEditText.setError("Password Required");
                    passwordEditText.requestFocus();
                    return;
                }
                if(confpass.isEmpty()){
                    confirmpassword.setError("Password Required");
                    confirmpassword.requestFocus();
                    return;
                }
                if(!password.equals(confpass)){
                    confirmpassword.setError("Password not Match");
                    passwordEditText.setError("Password not Match");
                    passwordEditText.requestFocus();
                    confirmpassword.requestFocus();
                    return;
                }
                if(password.length()<6){
                    passwordEditText.setError("At least 6 char password required");
                    passwordEditText.requestFocus();
                    return;
                }

                signUp();
            }
        });


    }

    public void signUp(){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            String id = task.getResult().getUser().getUid();
                            User user = new User(id,fname,sname,uname,phone,email,count);
                            mDatabase.child(id).setValue(user);

                            MainActivity.databaseHandler.insertData(id, imageToByte(R.drawable.user));
                            Toast.makeText(getApplicationContext(),"Sign Up Successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Sign Up Failed",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    private byte[] imageToByte(int imageId) {
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.drawable.user);
                        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        Log.i("sql",byteArray.toString());
                        return byteArray;
                    }
                });
    }
}
