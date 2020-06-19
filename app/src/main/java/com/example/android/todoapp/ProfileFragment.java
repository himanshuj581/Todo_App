package com.example.android.todoapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private TextView firstnameProfile;
    private TextView  secondnameProfile;
    private TextView  usernameProfile;
    private TextView  contactProfile;
    private TextView  countryProfile;
    private TextView  mailidProfile;
    private String userid;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private User user;
    private ImageView imageView;
    private Dialog dialog;
    private View view;
    private Button updateButton;
    private ImageView profileUpload;
    private ImageView profileDelete;
    private ImageView profilePicture;
    private Uri filepath;
    private StorageReference storageReference;
    private ImageView profileImage;
    private ProgressBar progressBar;
    private ProgressBar progressBarUpdatePicture;


    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firstnameProfile = view.findViewById(R.id.firstnameProfile);
        secondnameProfile = view.findViewById(R.id.secondnameProfile);
        usernameProfile = view.findViewById(R.id.usernameProfile);
        contactProfile = view.findViewById(R.id.phoneProfile);
        countryProfile = view.findViewById(R.id.countryProfile);
        mailidProfile = view.findViewById(R.id.emailProfile);
        progressBar = view.findViewById(R.id.progressbarupdateFragment);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        imageView = view.findViewById(R.id.profilemenu);
        dialog = new Dialog(view.getContext());
        storageReference = FirebaseStorage.getInstance().getReference();
        profileImage = view.findViewById(R.id.profileImage);
        profileImage.setImageBitmap(profile());

        progressBar.setVisibility(View.VISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.profilemenu:
                        PopupMenu popup = new PopupMenu(getContext(), v);
                        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
                        popup.show(); popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.updateProfile:
                                    updateProfile();
                                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.updatePicture:
                                    updatePicture();
                                    Toast.makeText(getContext(), "Picture Updated", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
                        break;
                    default:
                        break;
                }
            }
        });



        return view;
    }

    public Bitmap profile() {
        Cursor cursor = MainActivity.databaseHandler.getData("Select image from picture where id = '"+userid+"';");
        cursor.moveToNext();
        byte[] image = cursor.getBlob(0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0 , image.length);
        return bitmap;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User temp = snapshot.getValue(User.class);
                    if(userid.equalsIgnoreCase(temp.getUserid())) {
                        user = temp;
                        break;
                    }
                }

                firstnameProfile.setText(user.getFirstname());
                secondnameProfile.setText(user.getSecondname());
                usernameProfile.setText(user.getUsername());
                contactProfile.setText(user.getPhoneno());
                mailidProfile.setText(user.getEmail());
                countryProfile.setText(user.getCountry());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void updateProfile(){
        dialog.setContentView(R.layout.update_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final EditText first = dialog.findViewById(R.id.firstnamePopup);
        final EditText second = dialog.findViewById(R.id.secondnamePopup);
        final EditText username = dialog.findViewById(R.id.usernamePopup);
        final EditText contact = dialog.findViewById(R.id.phonePopup);
        final EditText country = dialog.findViewById(R.id.countryPopup);
        EditText email = dialog.findViewById(R.id.emailPopup);
        updateButton = dialog.findViewById(R.id.updateProfileButton);

        first.setText(user.getFirstname());
        second.setText(user.getSecondname());
        username.setText(user.getUsername());
        contact.setText(user.getPhoneno());
        country.setText(user.getCountry());
        email.setText(user.getEmail());


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = first.getText().toString();
                String sname = second.getText().toString();
                String uname = username.getText().toString();
                String con = contact.getText().toString();
                String coun = country.getText().toString();

                if(fname.isEmpty()){
                    first.setError("Required");
                    first.requestFocus();
                    return;
                }
                if(sname.isEmpty()){
                    second.setError("Required");
                    second.requestFocus();
                    return;
                }
                if(uname.isEmpty()){
                    username.setError("Required");
                    username.requestFocus();
                    return;
                }
                if(con.isEmpty()){
                    contact.setError("Required");
                    contact.requestFocus();
                    return;
                }
                if(coun.isEmpty()){
                    country.setError("Required");
                    country.requestFocus();
                    return;
                }

                user.setFirstname(fname);
                user.setSecondname(sname);
                user.setUsername(uname);
                user.setCountry(coun);
                user.setPhoneno(con);

                databaseReference.child(userid).setValue(user);
                com.google.android.material.navigation.NavigationView  navigationView = getActivity().findViewById(R.id.nav_view);
                TextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.navName);
                nameTextView.setText(user.getFirstname()+" "+user.getSecondname());
                dialog.dismiss();
            }
        });

    }

    public void updatePicture(){
        dialog.setContentView(R.layout.profile_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        profileUpload = dialog.findViewById(R.id.UploadPicture);
        profileDelete = dialog.findViewById(R.id.DeletePicture);
        profilePicture = dialog.findViewById(R.id.profilePicture);
        progressBarUpdatePicture = dialog.findViewById(R.id.progressbarupdateProfile);
        profilePicture.setImageBitmap(profile());

        profileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        profileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setMessage("Do you want to remove profile picture?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ImageView image = new ImageView(getContext());
                        image.setImageResource(R.drawable.user);
                        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        MainActivity.databaseHandler.updateData(userid,byteArray);
                        profilePicture.setImageBitmap(profile());
                        profileImage.setImageBitmap(profile());

                        com.google.android.material.navigation.NavigationView  navigationView = getActivity().findViewById(R.id.nav_view);
                        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.navProfile);
                        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Cursor cursor = MainActivity.databaseHandler.getData("Select image from picture where id = '"+userid+"';");
                        cursor.moveToNext();
                        byte[] bytes = cursor.getBlob(0);
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        imageView.setImageBitmap(bitmap);

                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.create().show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();
            progressBarUpdatePicture.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                uploadPicture();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                MainActivity.databaseHandler.updateData(userid,byteArray);
                profileImage.setImageBitmap(profile());
                profilePicture.setImageBitmap(profile());

                //Navigation header
                com.google.android.material.navigation.NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.navProfile);
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Cursor cursor = MainActivity.databaseHandler.getData("Select image from picture where id = '"+userid+"';");
                cursor.moveToNext();
                byte[] bytes = cursor.getBlob(0);
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadPicture(){
        if(filepath!=null) {
            StorageReference temp = storageReference.child("images/" + userid + ".jpg");
            temp.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBarUpdatePicture.setVisibility(View.GONE);
                            Log.i("App","Profile Picture Updated");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           Log.i("App","Profile Picture Updation Failed");
                        }
                    });
        }
    }
}
