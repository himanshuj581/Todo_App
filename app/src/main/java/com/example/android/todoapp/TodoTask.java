package com.example.android.todoapp;

import java.util.Date;

public class TodoTask {
    private String id;
    private String title;
    private String task;
    private String type;
    private Date date;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTask() {
        return task;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public TodoTask() {
    }

    public TodoTask(String id, String title, String task, String type, Date date) {
        this.id = id;
        this.title = title;
        this.task = task;
        this.type = type;
        this.date = date;
    }
}
