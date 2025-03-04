package com.example.todo_app;

public class ToDoModel {
    private int id;
    private String task;
    private int status;
    private int priority;
    private String datetime;

    public ToDoModel() {
        this.id = 0;
        this.task = "";
        this.status = 0;
        this.priority = 0;
        this.datetime="";
    }

    public ToDoModel(int id, String task, int status, int priority,String datetime) {
        this.id = id;
        this.task = task;
        this.status = status;
        this.priority = priority;
        this.datetime=datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    public String getDateTime(){
        return datetime;
    }
    public void setDatetime(String datetime){
        this.datetime=datetime;
    }
}
