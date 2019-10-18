package com.example.cats_catalog.data.models;

public class VoteResponse {

    public static final String MESSAGE_SUCCESS = "SUCCESS";
    public static final String MESSAGE_ERROR = "ERROR";

    private String message;
    private int id;

    public VoteResponse(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
