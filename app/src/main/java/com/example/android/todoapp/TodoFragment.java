package com.example.android.todoapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class TodoFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private EditText title;
    private EditText body;
    private Spinner spinner;
    private TextView cancel;
    private TextView submit;
    private Button button;
    private Dialog dialog;
    private String taskTitle;
    private String taskBody;
    private String taskType;
    private String userid;
    private String taskid;
    private ArrayList<TodoTask> taskArrayList;
    private ListView listView;
    private TaskAdapter taskAdapter;

    public TodoFragment() {
        // Required empty public constructor
    }

    public static void deleteTask(TodoTask todoTask, final Context context) {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userid).child(todoTask.getId());
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Do you want to delete Task?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.removeValue();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create().show();
    }

    public static void editTask(final TodoTask todoTask, final Context context) {

        final Dialog editdialog = new Dialog(context);
        editdialog.setContentView(R.layout.todopopup);
        editdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editdialog.show();
        final EditText title1 = editdialog.findViewById(R.id.todoTitle);
        final EditText body1 = editdialog.findViewById(R.id.todoBody);
        final Spinner spinner1 = editdialog.findViewById(R.id.typeSpinner);
        TextView cancel1 = editdialog.findViewById(R.id.cancelTextview);
        TextView submit1 = editdialog.findViewById(R.id.submitTextview);
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userid);

        title1.setText(todoTask.getTitle());
        body1.setText(todoTask.getTask());
        if (todoTask.getType().equalsIgnoreCase("Personal")) {
            spinner1.setSelection(0);
        }
        else {
            spinner1.setSelection(1);
        }

        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdialog.dismiss();
            }
        });

        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = title1.getText().toString();
                String taskBody = body1.getText().toString();
                String taskType = spinner1.getSelectedItem().toString();

                if (taskTitle.isEmpty()) {
                    title1.setError("Please Enter title");
                    title1.requestFocus();
                    return;
                }
                if (taskBody.isEmpty()) {
                    body1.setError("Please Enter Task");
                    body1.requestFocus();
                    return;
                }

                databaseReference.child(todoTask.getId()).setValue(new TodoTask(todoTask.getId(),taskTitle,taskBody,taskType,Calendar.getInstance().getTime()));
                editdialog.dismiss();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);


        button = view.findViewById(R.id.addTask);
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(userid);
        dialog = new Dialog(view.getContext());
        taskArrayList = new ArrayList<>();
        listView = view.findViewById(R.id.todoListView);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.todopopup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                title = dialog.findViewById(R.id.todoTitle);
                body = dialog.findViewById(R.id.todoBody);
                spinner = dialog.findViewById(R.id.typeSpinner);
                cancel = dialog.findViewById(R.id.cancelTextview);
                submit = dialog.findViewById(R.id.submitTextview);
                taskid = databaseReference.child(userid).push().getKey();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        taskTitle = title.getText().toString();
                        taskBody = body.getText().toString();
                        taskType = spinner.getSelectedItem().toString();

                        if (taskTitle.isEmpty()) {
                            title.setError("Please Enter title");
                            title.requestFocus();
                            return;
                        }
                        if (taskBody.isEmpty()) {
                            body.setError("Please Enter Task");
                            body.requestFocus();
                            return;
                        }

                        addTask();
                        dialog.dismiss();
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskArrayList.clear();

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    TodoTask todoTask = postSnapShot.getValue(TodoTask.class);
                    taskArrayList.add(todoTask);
                }
                taskAdapter = new TaskAdapter(getActivity(), taskArrayList);
                listView.setAdapter(taskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void addTask() {
        Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
        TodoTask todoTask = new TodoTask(taskid, taskTitle, taskBody, taskType, Calendar.getInstance().getTime());
        databaseReference.child(taskid).setValue(todoTask);

    }

}
