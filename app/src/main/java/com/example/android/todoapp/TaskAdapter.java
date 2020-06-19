package com.example.android.todoapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskAdapter extends ArrayAdapter<TodoTask> {
    private Activity context;
    ArrayList<TodoTask> arrayList;

    public TaskAdapter(Activity context, ArrayList<TodoTask> arrayList){
        super(context,R.layout.list_item, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final int pos =position;
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final TodoTask task =getItem(position);
        TextView title =listItemView.findViewById(R.id.titleTextView);
        TextView body = listItemView.findViewById(R.id.bodyTextView);
        ImageView imageView = listItemView.findViewById(R.id.typeImageView);
        TextView time = listItemView.findViewById(R.id.timeTextview);

        title.setText(task.getTitle());
        body.setText(task.getTask());
        {
            Date current = Calendar.getInstance().getTime();
            long rest = (current.getTime()-task.getDate().getTime())/1000;

            String msg;
            if(rest<60){
                msg = "a few seconds ago";
            }
            else if(rest<300){
                msg = "a few minutes ago";
            }
            else if(rest<3600){
                msg = rest/60 + " minutes ago";
            }
            else if(rest<86400){
                msg = rest/3600 + " hours ago";
            }
            else {
                msg = rest/86400 + " days ago";
            }
            time.setText(msg);
        }

        if(task.getType().equalsIgnoreCase("Personal")){
            imageView.setImageResource(R.drawable.home);
        }
        else {
            imageView.setImageResource(R.drawable.office);
        }

        ImageView imageView1 = listItemView.findViewById(R.id.taskMenu);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.taskMenu:
                        PopupMenu popup = new PopupMenu(getContext(), v);
                        popup.getMenuInflater().inflate(R.menu.task_menu, popup.getMenu());
                        popup.show(); popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.editTaskMenu:
                                    TodoFragment.editTask(task,getContext());
                                    break;
                                case R.id.deleteTaskMenu:
                                    TodoFragment.deleteTask(task, getContext());
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
        return listItemView;
    }
}
