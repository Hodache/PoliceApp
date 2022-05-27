package com.dataclasses;

public class Fine {
    private String date;
    private String description;
    private int size;

    public Fine(String date, String description, int size){
        this.date = date;
        this.description = description;
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }
}
