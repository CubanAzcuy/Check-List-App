package com.checkmeout.ninetofivedevelopment.tasks;

import io.realm.RealmObject;

public class TaskObject extends RealmObject {

    private String name;
    private String color;
    private boolean completed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}