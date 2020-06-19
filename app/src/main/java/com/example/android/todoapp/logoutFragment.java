package com.example.android.todoapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class logoutFragment extends Fragment {


    public logoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view1 = inflater.inflate(R.layout.fragment_logout, container, false);
        final View view2 = inflater.inflate(R.layout.fragment_home, container, false);

        AlertDialog.Builder alert = new AlertDialog.Builder(view1.getContext());
        alert.setMessage("Do you want to Logout?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.status=false;
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(view1.getContext(),SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.create().show();

        return view2;
    }
}
